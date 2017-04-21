package net.mcfr.death;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public interface CareService {
  /**
   * Renvoit la List des CareCenters existantes
   */
  public List<CareCenter> getCenters();

  /**
   * Renvoit la List des factions existantes
   */
  public Map<String, String> getFactionsMap();

  /**
   * Ajoute une zone couverte et l'enregistre en base de données
   * 
   * @return Vrai si la zone a été créée, faux si une zone porte déjà ce nom
   */
  public boolean addCenter(String name, Location<World> location, int radius, String faction);

  /**
   * Détruit la zone couverte au nom renseigné en paramètre et la retire de la base de données
   */
  public void removeCenter(CareCenter center);

  /**
   * Renvoie le CareCenter portant ce nom dans un Optional, ou un Optional vide
   */
  public Optional<CareCenter> getCenterByName(String name);

  /**
   * Charge les zones de couverture depuis la base de données
   */
  public void loadFromDatabase();

  /**
   * Actualise l'état du joueur
   */
  public void actualizePlayerState(Player p);

  /**
   * Effectue le roll de la mort d'un personnage
   */
  public void computeDeath(Player player);

  /**
   * Effectue le respawn d'un joueur dans le centre de soin le plus approprié
   */
  public void respawnPlayer(RespawnPlayerEvent e);

  /**
   * Renvoit le centre de soin le plus à même de récupérer le personnage blessé
   * 
   * @param location
   *          La Location du joueur
   * @return Un Optional vide si pas de centre, ou avec le meilleur centre
   */
  public Optional<CareCenter> getBest(Location<World> location, boolean inRange);
}
