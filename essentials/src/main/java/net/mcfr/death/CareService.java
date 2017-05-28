package net.mcfr.death;

import org.spongepowered.api.entity.living.player.Player;

public interface CareService {

  /**
   * Vérifie si un joueur est protégé par un centre de soin.
   */
  public boolean isInProtectedArea(Player player);

  /**
   * Gère la mort du joueur.
   */
  public void manageDeath(Player player);

  /**
   * Gère la ré-apparition du joueur après sa mort.
   */
  public void manageRespawn(Player player);
  
  public void loadFromDatabase();
}