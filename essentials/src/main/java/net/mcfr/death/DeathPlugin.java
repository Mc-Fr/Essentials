package net.mcfr.death;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;

import net.mcfr.services.CareService;

@Plugin(id = "death", name = "Death", version = "1.0")
public class DeathPlugin {
  private final CareImp care = new CareImp();

  @Listener
  public void onInit(GameInitializationEvent e) {
    Sponge.getServiceManager().setProvider(this, CareService.class, this.care);

    CareCenterCommand cmd = new CareCenterCommand();
    Sponge.getCommandManager().register(this, cmd.getCommandSpec(), cmd.getAliases());
  }

  @Listener
  public void onPlayerMove(MoveEntityEvent e, @First Player p) {
    this.care.trackPlayer(p);
  }
}
