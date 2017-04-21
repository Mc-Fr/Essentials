package net.mcfr.services;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;

public interface CareService {

  /**
   * Actualise l'état du joueur
   */
  public void trackPlayer(Player p);

  /**
   * Effectue le roll de la mort d'un personnage
   */
  public void computeDeath(Player player);

  /**
   * Effectue le respawn d'un joueur dans le centre de soin le plus approprié
   */
  public void respawnPlayer(RespawnPlayerEvent e);
}