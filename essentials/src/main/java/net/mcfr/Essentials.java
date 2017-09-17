package net.mcfr;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.inject.Inject;

import net.mcfr.chat.TribalWord;
import net.mcfr.commands.Command;
import net.mcfr.death.CareImp;
import net.mcfr.death.CareService;
import net.mcfr.expedition.ExpeditionImp;
import net.mcfr.expedition.ExpeditionService;
import net.mcfr.harvest.HarvestImp;
import net.mcfr.harvest.HarvestService;
import net.mcfr.listeners.BurrowListener;
import net.mcfr.listeners.CommandListener;
import net.mcfr.listeners.LoginListener;
import net.mcfr.listeners.NatureListener;
import net.mcfr.listeners.PlayerListener;
import net.mcfr.locks.LocksImp;
import net.mcfr.locks.LocksService;
import net.mcfr.roleplay.RolePlayImp;
import net.mcfr.roleplay.RolePlayService;
import net.mcfr.time.TimeImp;
import net.mcfr.time.TimeService;
import net.mcfr.utils.McFrPlayer;
import net.mcfr.warp.WarpImp;
import net.mcfr.warp.WarpService;

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

    registerListeners();

    getLogger().info("McFrEssentials Plugin has loaded.");
  }

  @Listener
  public void onPostInit(GamePostInitializationEvent e) {
  }
  
  private void registerListeners() {
    Sponge.getEventManager().registerListeners(this, new BurrowListener());
    Sponge.getEventManager().registerListeners(this, new CommandListener());
    Sponge.getEventManager().registerListeners(this, new LoginListener(this));
    Sponge.getEventManager().registerListeners(this, new NatureListener());
    Sponge.getEventManager().registerListeners(this, new PlayerListener());

    Sponge.getServiceManager().setProvider(this, RolePlayService.class, new RolePlayImp());
    Sponge.getServiceManager().setProvider(this, ExpeditionService.class, new ExpeditionImp());
    Sponge.getServiceManager().setProvider(this, CareService.class, new CareImp());
    Sponge.getServiceManager().setProvider(this, WarpService.class, new WarpImp());
    Sponge.getServiceManager().setProvider(this, HarvestService.class, new HarvestImp());
    Sponge.getServiceManager().setProvider(this, TimeService.class, new TimeImp());
    Sponge.getServiceManager().setProvider(this, LocksService.class, new LocksImp());

    Arrays.stream(Command.values()).map(c -> c.createCommand(this)).filter(o -> o.isPresent()).map(o -> o.get())
        .forEach(c -> Sponge.getCommandManager().register(this, c.getCommandSpec(), c.getAliases()));

    getLogger().info("McFrEssentials Plugin has loaded.");
  }

  @Listener
  public void onServerStart(GameStartedServerEvent event) throws IOException {
    File commandsFile = new File("config/esssentials-config/commands.json");
    if (commandsFile.exists())
      new JsonParser().parse(new JsonReader(new FileReader(commandsFile))).getAsJsonObject().get("commands").getAsJsonArray().forEach(this::planTask);
    
    Optional<TimeService> optTimeService = Sponge.getServiceManager().provide(TimeService.class);
    
    if (optTimeService.isPresent())
      Sponge.getScheduler().createTaskBuilder().execute(() -> optTimeService.get().update()).intervalTicks(1).submit(this);
    
    Sponge.getServer().getWorldProperties("world").get().setGameRule("doDaylightCycle", "false");
    
    TribalWord.loadFromDatabase();
    Sponge.getServiceManager().provide(CareService.class).get().loadFromDatabase();
    Sponge.getServiceManager().provide(ExpeditionService.class).get().loadFromDatabase();
    Sponge.getServiceManager().provide(WarpService.class).get().loadFromDatabase();
    Sponge.getServiceManager().provide(HarvestService.class).get().loadFromDatabase();
    Sponge.getServiceManager().provide(LocksService.class).get().loadFromDatabase();
  }
  
  @Listener
  public void onServerStop(GameStoppingServerEvent event) {
    McFrPlayer.getMcFrPlayers().forEach(McFrPlayer::logSession);
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
    taskBuilder.execute(() -> manager.process(Sponge.getServer().getConsole(), expression)).delay(delay, TimeUnit.SECONDS)
        .interval(interval, TimeUnit.SECONDS).submit(this);
  }
}
