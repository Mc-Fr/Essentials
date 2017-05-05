package net.mcfr;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.inject.Inject;

import net.mcfr.chat.TribalWord;
import net.mcfr.commands.utils.Command;
import net.mcfr.death.CareImp;
import net.mcfr.expedition.ExpeditionImp;
import net.mcfr.expedition.ExpeditionService;
import net.mcfr.listeners.BurrowListener;
import net.mcfr.listeners.CommandListener;
import net.mcfr.listeners.LoginListener;
import net.mcfr.listeners.NatureListener;
import net.mcfr.listeners.PlayerListener;
import net.mcfr.roleplay.RolePlayImp;
import net.mcfr.roleplay.RolePlayService;
import net.mcfr.services.CareService;

@Plugin(id = "essentials", name = "Essentials", version = "1.0", dependencies = @Dependency(id = "mcfr_b_i"))
public class Essentials {
  private boolean serverLock;

  @Inject
  private Logger logger;

  public Logger getLogger() {
    return this.logger;
  }

  @Listener
  public void onInit(GameInitializationEvent e) {
    this.serverLock = false;

    Arrays.stream(Command.values()).map(c -> c.f()).filter(o -> o.isPresent()).map(o -> o.get()).forEach(
        c -> Sponge.getCommandManager().register(this, c.getCommandSpec(), c.getAliases()));

    Sponge.getServiceManager().setProvider(this, RolePlayService.class, new RolePlayImp());
    Sponge.getServiceManager().setProvider(this, ExpeditionService.class, new ExpeditionImp());
    Sponge.getServiceManager().setProvider(this, CareService.class, new CareImp());

    Sponge.getEventManager().registerListeners(this, new BurrowListener());
    Sponge.getEventManager().registerListeners(this, new CommandListener());
    Sponge.getEventManager().registerListeners(this, new LoginListener(this));
    Sponge.getEventManager().registerListeners(this, new NatureListener());
    Sponge.getEventManager().registerListeners(this, new PlayerListener());

    getLogger().info("McFrEssentials Plugin has loaded.");
  }

  @Listener
  public void onServerStart(GameStartedServerEvent event) throws IOException {
    File commandsFile = new File("config/esssentials-config/commands.json");
    if (commandsFile.exists()) {
      new JsonParser().parse(new JsonReader(new FileReader(commandsFile))).getAsJsonObject().get("commands").getAsJsonArray().forEach(this::planTask);
    }

    Sponge.getScheduler().createTaskBuilder().execute(() -> Sponge.getCommandManager().process(Sponge.getServer().getConsole(), "burrow load")).delay(
        4, TimeUnit.SECONDS).submit(this);
    TribalWord.loadFromDatabase();
  }

  public void toggleServerLock() {
    this.serverLock = !this.serverLock;
  }

  public boolean isServerLocked() {
    return this.serverLock;
  }

  private void planTask(JsonElement c) {
    Task.Builder taskBuilder = Sponge.getScheduler().createTaskBuilder();
    CommandManager manager = Sponge.getCommandManager();

    JsonObject command = c.getAsJsonObject();
    String expression = command.get("command").getAsString();
    long delay = command.get("delay").getAsLong();
    long interval = command.get("interval").getAsLong();
    taskBuilder.execute(() -> manager.process(Sponge.getServer().getConsole(), expression)).delay(delay, TimeUnit.SECONDS).interval(interval,
        TimeUnit.SECONDS).submit(this);
  }
}
