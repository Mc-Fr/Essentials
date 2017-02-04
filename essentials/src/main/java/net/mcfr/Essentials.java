package net.mcfr;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSources;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
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

import net.mcfr.babel.TribalWord;
import net.mcfr.burrows.Burrow;
import net.mcfr.burrows.BurrowListener;
import net.mcfr.chat.MessageData;
import net.mcfr.commands.utils.AbstractCommand;
import net.mcfr.commands.utils.Commands;
import net.mcfr.death.CareSystem;
import net.mcfr.expedition.ExpeditionSystem;
import net.mcfr.listeners.CommandListener;
import net.mcfr.listeners.DamageListener;
import net.mcfr.roleplay.RolePlayImp;
import net.mcfr.roleplay.RolePlayService;
import net.mcfr.utils.McFrConnection;
import net.mcfr.utils.McFrPlayer;

@Plugin(id = "essentials", name = "Essentials", version = "1.0", dependencies = @Dependency(id = "mcfr_b_i"))
public class Essentials {
  private final long LAST_BREATH_INVICIBILITY = 2000;
  private final long LAST_BREATH_DELAY = 15000;
  private static List<EntityType> forbiddenEntities = new ArrayList<>();
  
  static {
    forbiddenEntities.add(EntityTypes.BAT);
    forbiddenEntities.add(EntityTypes.BLAZE);
    forbiddenEntities.add(EntityTypes.CAVE_SPIDER);
    forbiddenEntities.add(EntityTypes.CHICKEN);
    forbiddenEntities.add(EntityTypes.COW);
    forbiddenEntities.add(EntityTypes.CREEPER);
    forbiddenEntities.add(EntityTypes.ENDER_DRAGON);
    forbiddenEntities.add(EntityTypes.ENDERMAN);
    forbiddenEntities.add(EntityTypes.GHAST);
    forbiddenEntities.add(EntityTypes.GIANT);
    forbiddenEntities.add(EntityTypes.GUARDIAN);
    forbiddenEntities.add(EntityTypes.IRON_GOLEM);
    forbiddenEntities.add(EntityTypes.MAGMA_CUBE);
    forbiddenEntities.add(EntityTypes.MUSHROOM_COW);
    forbiddenEntities.add(EntityTypes.OCELOT);
    forbiddenEntities.add(EntityTypes.PIG);
    forbiddenEntities.add(EntityTypes.PIG_ZOMBIE);
    forbiddenEntities.add(EntityTypes.POLAR_BEAR);
    forbiddenEntities.add(EntityTypes.RABBIT);
    forbiddenEntities.add(EntityTypes.SHEEP);
    forbiddenEntities.add(EntityTypes.SILVERFISH);
    forbiddenEntities.add(EntityTypes.SKELETON);
    forbiddenEntities.add(EntityTypes.SLIME);
    forbiddenEntities.add(EntityTypes.SNOWMAN);
    forbiddenEntities.add(EntityTypes.SPIDER);
    forbiddenEntities.add(EntityTypes.SQUID);
    forbiddenEntities.add(EntityTypes.VILLAGER);
    forbiddenEntities.add(EntityTypes.WITCH);
    forbiddenEntities.add(EntityTypes.WITHER);
    forbiddenEntities.add(EntityTypes.WITHER_SKULL);
    forbiddenEntities.add(EntityTypes.WOLF);
    forbiddenEntities.add(EntityTypes.ZOMBIE);
  }

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
    Sponge.getEventManager().registerListeners(this, new ExpeditionSystem());
    Sponge.getEventManager().registerListeners(this, new CommandListener());
    Sponge.getEventManager().registerListeners(this, new BurrowListener());

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
              e.setCancelled(!e.getTargetUser().hasPermission("essentials.admin.connect_without_character"));
            }
            deathData.close();

          } else {
            e.setCancelled(!e.getTargetUser().hasPermission("essentials.admin.connect_without_character"));
          }
          characterSheet.close();
        } else {
          e.setCancelled(!e.getTargetUser().hasPermission("essentials.admin.connect_without_character"));
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
      if (Calendar.getInstance().getTime().getTime() - McFrPlayer.getMcFrPlayer(player).getReadDescriptionTime() > 100) {
        McFrPlayer.getMcFrPlayer(player).updateReadDescriptionTime();
        McFrPlayer otherPlayer = McFrPlayer.getMcFrPlayer(((Player) e.getTargetEntity()));
        player.sendMessage(Text.of(TextColors.DARK_GREEN, "* " + otherPlayer.getName() + " * " + otherPlayer.getDescription() + " *"));
      }
    }
  }

  @Listener
  public void onDamageEntity(DamageEntityEvent e) {
    if (e.getTargetEntity() instanceof Player) {
      Player player = (Player) e.getTargetEntity();
      double health = player.health().get();
      double damage = e.getOriginalFinalDamage();

      if (damage >= health) {
        long lastBreathTime = Calendar.getInstance().getTime().getTime() - McFrPlayer.getMcFrPlayer(player).getLastBreathTime();

        if (lastBreathTime > this.LAST_BREATH_DELAY) {
          player.damage(health - 0.5D, DamageSources.GENERIC);
          e.setCancelled(true);
          McFrPlayer.getMcFrPlayer(player).updateLastBreathTime();

          // #f:0
          PotionEffectData effects = player.getOrCreate(PotionEffectData.class).get();
          effects.addElement(PotionEffect.builder()
              .potionType(PotionEffectTypes.SLOWNESS)
              .duration(300)
              .amplifier(3)
              .particles(false)
              .build());
          player.offer(effects);
          // #f:1

          player.sendMessage(Text.of(TextColors.DARK_RED, "Vous arrivez à votre dernier souffle. Encore un peu et vous mourrez."));

        } else if (lastBreathTime < this.LAST_BREATH_INVICIBILITY) {
          player.damage(health - 0.5D, DamageSources.GENERIC);
          e.setCancelled(true);
        }
      }

    }
  }

  @Listener
  public void onPlayerDisconnect(ClientConnectionEvent.Disconnect e) {
    McFrPlayer.removePlayer(e.getTargetEntity());
  }

  /**
   * Déclenché quand un item est looté depuis un bloc cassé ou une entité tuée
   */
  /*@Listener
  public void onLootItem(DropItemEvent.Destruct e) {
    boolean mustLoot = true;

    Optional<EntityDamageSource> optDamageSource = e.getCause().first(EntityDamageSource.class);

    if (optDamageSource.isPresent()) {
      mustLoot = false;
      Entity source = optDamageSource.get().getSource();

      if (source instanceof Player) {
        McFrPlayer player = McFrPlayer.getMcFrPlayer((Player) source);
        int skillLevel = player.getAttributePoints(Attributes.DEXTERITE) + player.getSkillLevel(Skills.getSkillByName("chasse"));

        if (skillLevel > 12) {
          mustLoot = true;
        }
      }
    }

    e.setCancelled(!mustLoot);
  }*/

  @Listener
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
        .delay(4, TimeUnit.SECONDS).submit(this);
    TribalWord.loadFromDatabase();
  }

  @Listener
  public void onMessageChannelEvent(MessageChannelEvent.Chat e, @First CommandSource sender) {
    if (sender instanceof Player) {
      MessageData data = new MessageData((Player) sender, e.getRawMessage().toPlain());
      if (data.checkConditions()) {
        data.send();
      }

      e.setCancelled(true);
    }
  }

  @Listener
  public void onSpawnEntity(SpawnEntityEvent event) {
    event.getEntities().stream().filter(e -> forbiddenEntities.contains(e.getType())).forEach(e -> event.setCancelled(true));
  }

  @Listener
  public void onServerStop(GameStoppingServerEvent event) {
    Burrow.save();
    BurrowListener.saveInDatabase();
  }

  public void toggleServerLock() {
    this.serverLock = !this.serverLock;
  }

  public boolean isServerLocked() {
    return this.serverLock;
  }
}
