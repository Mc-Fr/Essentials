package net.mcfr;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.inject.Inject;

import net.mcfr.babel.TribalWord;
import net.mcfr.commands.utils.AbstractCommand;
import net.mcfr.commands.utils.Commands;
import net.mcfr.death.CareImp;
import net.mcfr.death.CareService;
import net.mcfr.expedition.ExpeditionImp;
import net.mcfr.expedition.ExpeditionService;
import net.mcfr.listeners.BurrowListener;
import net.mcfr.listeners.CommandListener;
import net.mcfr.listeners.LoginListener;
import net.mcfr.listeners.NatureListener;
import net.mcfr.listeners.PlayerListener;
import net.mcfr.roleplay.RolePlayImp;
import net.mcfr.roleplay.RolePlayService;

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
    
    registerListeners();

    getLogger().info("McFrEssentials Plugin has loaded.");
  }
  
  private void registerListeners() {
    Sponge.getEventManager().registerListeners(this, new BurrowListener());
    Sponge.getEventManager().registerListeners(this, new CommandListener());
    Sponge.getEventManager().registerListeners(this, new LoginListener(this));
    Sponge.getEventManager().registerListeners(this, new NatureListener());
    Sponge.getEventManager().registerListeners(this, new PlayerListener());
  }

  @Listener
  public void onPreInit(GamePreInitializationEvent e) {
    Sponge.getServiceManager().setProvider(this, RolePlayService.class, new RolePlayImp());
  }

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
    
    Sponge.getServiceManager().setProvider(this, ExpeditionService.class, new ExpeditionImp());
    Sponge.getServiceManager().setProvider(this, CareService.class, new CareImp());

    Sponge.getScheduler().createTaskBuilder().execute(() -> Sponge.getCommandManager().process(Sponge.getServer().getConsole(), "burrow load"))
        .delay(4, TimeUnit.SECONDS).submit(this);
    TribalWord.loadFromDatabase();
  }

  public void toggleServerLock() {
    this.serverLock = !this.serverLock;
  }

  public boolean isServerLocked() {
    return this.serverLock;
  }
}
