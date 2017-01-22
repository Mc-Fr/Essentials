package net.mcfr.listeners;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DamageEntityEvent;

import net.mcfr.utils.McFrPlayer;

public class DamageListener {

  @Listener
  public void onEntityDamage(DamageEntityEvent e) {
    Entity ent = e.getTargetEntity();
    if (!(ent instanceof Player))
      return;
    Player player = (Player) ent;
    if (!McFrPlayer.getMcFrPlayer(player).isGod())
      return;
    e.setCancelled(true);
  }

}
