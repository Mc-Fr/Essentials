package net.mcfr.expedition;

import java.util.List;
import java.util.Optional;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public interface ExpeditionService {
  /**
   * Renvoit la list des AuthorizedAreas existantes
   */
  public List<AuthorizedArea> getAreas();
  
  /**
   * Ajoute une zone sécurisée et l'enregistre en base de données
   * @return Vrai si la zone a été créée, faux si une zone porte déjà ce nom
   */
  public boolean addArea(String name, Location<World> location, int radius);
  
  /**
   * Détruit la zone sécurisée au nom renseigné en paramètre et la retire de la base de données
   */
  public void removeArea(AuthorizedArea area);
  
  /**
   * Renvoie l'AuthorizedArea portant ce nom dans un Optional, ou un Optional vide
   */
  public Optional<AuthorizedArea> getAreaByName(String name);
  
  /**
   * Charge les zones d'expédition depuis la base de données
   */
  public void loadFromDatabase();
  
  /**
   * Actualise l'état du joueur
   */
  public void actualizePlayerState(Player p);
  
  /**
   * Retourne l'état correspondant à l'emplacement renseigné en paramètre
   */
  public States getStateAtLocation(Location<World> loc);
  
  /**
   * Retourne l'état le plus sûr entre les deux états envoyés en paramètre
   */
  public States getSafest(States current, States next);
  
  
}
