package net.mcfr;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.inject.Inject;

import net.mcfr.burrows.Burrow;
import net.mcfr.chat.MessageData;
import net.mcfr.commands.utils.AbstractCommand;
import net.mcfr.commands.utils.Commands;
import net.mcfr.death.CareSystem;
import net.mcfr.entities.mobs.gender.EntityBurrowed;
import net.mcfr.listeners.CommandListener;
import net.mcfr.listeners.DamageListener;
import net.mcfr.roleplay.RolePlayImp;
import net.mcfr.roleplay.RolePlayService;
import net.mcfr.utils.McFrConnection;
import net.mcfr.utils.McFrPlayer;

@Plugin(id = "essentials", name = "Essentials", version = "1.0", dependencies = @Dependency(id = "mcfr_b_i"))
public class Essentials {

  private boolean serverLock;

  @Inject
  private Game game;

  @Inject
  private Logger logger;

  public Game getGame() {
    return this.game;
  }

  public Logger getLogger() {
    return this.logger;
  }

  @Listener
  public void onInit(GameInitializationEvent e) {
    this.serverLock = false;
    for (Commands command : Commands.values()) {
      try {
        AbstractCommand cmd = command.getCommandClass().getConstructor(Essentials.class).newInstance(this);
        getGame().getCommandManager().register(this, cmd.getCommandSpec(), cmd.getAliases());
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
          | SecurityException e1) {
        e1.printStackTrace();
      }
    }

    Sponge.getEventManager().registerListeners(this, new DamageListener());
    Sponge.getEventManager().registerListeners(this, new CareSystem());
    Sponge.getEventManager().registerListeners(this, new CommandListener());

    getLogger().info("McFrEssentials Plugin has loaded.");
  }

  @Listener
  public void onPreInit(GamePreInitializationEvent e) {
    Sponge.getServiceManager().setProvider(this, RolePlayService.class, new RolePlayImp());
  }

  @Listener
  public void onPlayerLogin(ClientConnectionEvent.Login e) {
    if (this.serverLock && !e.getTargetUser().hasPermission("essentials.admin.log_when_lock")) {
      e.setCancelled(true);
    } else {
      try {
        int userId = -1;
        PreparedStatement forumAccountId = McFrConnection.getJdrConnection()
            .prepare("SELECT user_id FROM phpbb_users PU JOIN account_link AL ON AL.forum = PU.username WHERE AL.minecraft = ?");
        forumAccountId.setString(1, e.getTargetUser().getName());
        ResultSet user = forumAccountId.executeQuery();

        if (user.next()) {
          userId = user.getInt(1);
          PreparedStatement activeCharacterSheet = McFrConnection.getJdrConnection()
              .prepare("SELECT id FROM fiche_perso_personnage WHERE id_user = ? AND active = 1");
          activeCharacterSheet.setInt(1, userId);
          ResultSet characterSheet = activeCharacterSheet.executeQuery();

          if (characterSheet.next()) {
            PreparedStatement deathDataReq = McFrConnection.getJdrConnection()
                .prepare("SELECT avantage FROM fiche_perso_personnage_avantage WHERE avantage = \"mort\" AND id_fiche_perso_personnage = ?");
            deathDataReq.setInt(1, characterSheet.getInt(1));
            ResultSet deathData = deathDataReq.executeQuery();
            if (deathData.next()) {
              e.setCancelled(true);
            }
            deathData.close();

          } else {
            e.setCancelled(!e.getTargetUser().hasPermission("essentials.admin.connect_without_character"));
          }
          characterSheet.close();
        } else {
          e.setCancelled(true);
        }
        user.close();
      } catch (SQLException ex) {
        ex.printStackTrace();
      }
    }
  }

  @Listener
  public void onPlayerJoin(ClientConnectionEvent.Join e) {
    McFrPlayer player = new McFrPlayer(e.getTargetEntity());
    McFrPlayer.addPlayer(player);
    player.loadFromDataBase();
  }

  @Listener
  public void onPlayerRightClick(InteractEntityEvent.Secondary e, @First Player player) {
    Entity target = e.getTargetEntity();
    if (target instanceof Player) {
      player.sendMessage(Text.of(TextColors.DARK_GREEN, "* " + McFrPlayer.getMcFrPlayer((Player) e.getTargetEntity()).getDescription() + " *"));
    }
  }

  @Listener
  public void onPlayerDisconnect(ClientConnectionEvent.Disconnect e) {
    McFrPlayer.removePlayer(e.getTargetEntity());
  }

  @Listener
  public void onBlockChange(ChangeBlockEvent.Place e) {
    if (!e.getCause().first(Player.class).isPresent()) {
      e.setCancelled(true);
    }
  }

  @Listener
  public void onItemDrop(DropItemEvent.Destruct e) {
    e.setCancelled(true);
  }

  @Listener(order = Order.POST)
  public void onServerStart(GameStartedServerEvent event) throws IOException {
    File commandsFile = new File("config/esssentials-config/commands.json");
    if (commandsFile.exists()) {
      Task.Builder taskBuilder = Sponge.getScheduler().createTaskBuilder();
      CommandManager manager = Sponge.getCommandManager();

      JsonArray commands = new JsonParser().parse(new JsonReader(new FileReader(commandsFile))).getAsJsonObject().get("commands").getAsJsonArray();

      commands.forEach(c -> {
        JsonObject command = c.getAsJsonObject();
        String expression = command.get("command").getAsString();
        long delay = command.get("delay").getAsLong();
        long interval = command.get("interval").getAsLong();
        taskBuilder.execute(() -> manager.process(Sponge.getServer().getConsole(), expression)).delay(delay, TimeUnit.SECONDS)
            .interval(interval, TimeUnit.SECONDS).submit(this);
      });
    }

    Sponge.getScheduler().createTaskBuilder().execute(() -> Sponge.getCommandManager().process(Sponge.getServer().getConsole(), "burrow load"))
        .delay(3, TimeUnit.SECONDS).submit(this);
  }

  @Listener
  public void onMessageChannelEvent(MessageChannelEvent.Chat e, @First CommandSource sender) {
    MessageData data = new MessageData((Player) sender, e.getRawMessage().toPlain());
    if (data.checkConditions()) {
      data.send();
    }

    e.setCancelled(true);
  }
  
  @Listener
  public void onEntityDestruct(DestructEntityEvent event) {
    if (event.getTargetEntity() instanceof EntityBurrowed) {
      Burrow.removeFromBurrow(event.getTargetEntity().getUniqueId());
    }
  }

  @Listener
  public void onServerStop(GameStoppingServerEvent event) {
    Burrow.saveAndClose();
  }

  public void toggleServerLock() {
    this.serverLock = !this.serverLock;
  }

  public boolean isServerLocked() {
    return this.serverLock;
  }
}
