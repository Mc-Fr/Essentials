package net.mcfr.burrows;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3i;

import net.mcfr.entities.mobs.gender.EntityGendered;
import net.mcfr.utils.McFrConnection;
import net.mcfr.utils.McFrPlayer;

/* TODO :
 * 
 * Aide sur les commandes
 * Règles sur les commandes
 * Commandes cliquables
 */

public class Burrow {
  /**
   * Coefficient de conversion d'une minute en millisecondes.
   */
  private final static long MINUTE_TO_MILLISECONDS = 60000;

  /**
   * Liste des terriers enregistrés.
   */
  private static List<Burrow> burrows = new LinkedList<>();

  /**
   * ID du terrier en base de donnée.
   */
  private int id;

  /**
   * Nom du terrier.
   */
  private String name;

  /**
   * Position du terrier.
   */
  private Location<World> location;

  /**
   * Durée minimale entre une naissance et le dernier événement, en minutes.
   */
  private long delay;

  /**
   * Date du dernier événement, en minutes.
   */
  private long lastEventTime;

  /**
   * Population du terrier.
   */
  private BurrowPopulation population;

  private Burrow(int id, Optional<String> name, Location<World> location, long delay, int maxPopulation, int initMalePopulation,
      int initFemalePopulation, EntityType entityType) {
    this(id, name, location, delay, Calendar.getInstance().getTime().getTime(), maxPopulation, entityType);

    this.population.spawnNewEntities(initMalePopulation, initFemalePopulation);
  }

  private Burrow(int id, Optional<String> name, Location<World> location, long delay, long lastEventTime, int maxPopulation, EntityType entityType) {
    this.id = id;
    this.name = name.orElse("Terrier " + id);
    this.location = location;
    this.delay = delay * MINUTE_TO_MILLISECONDS;
    this.lastEventTime = lastEventTime;
    this.population = new BurrowPopulation(this.location, this.id, maxPopulation, entityType);

    setVisibleForAll();
  }

  /**
   * Mise à jour du terrier.
   */
  private void update() {
    long currentTime = Calendar.getInstance().getTime().getTime();

    this.population.count();

    if (this.population.isEmpty()) {
      removeBurrow();
    }

    if (this.population.hasBeenDeaths()) {
      this.lastEventTime = currentTime;
      this.population.actualize();
    }

    if (this.lastEventTime + this.delay < currentTime) {
      this.population.tryBirth();
      this.lastEventTime = currentTime;
    }

    saveInDatabase();
  }

  /**
   * Enregistre le terrier en base de données
   */
  private void registerInDatabase() {
    try (PreparedStatement insertQuery = McFrConnection.getServerConnection().prepareStatement(
          "INSERT INTO Burrow(id, name, world, timer, maxPopulation, entityType, lastEventTime`, `x`, `y`, `z`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")){
      insertQuery.setInt(1, this.id);
      insertQuery.setString(2, this.name);
      insertQuery.setString(3, this.location.getExtent().getName());
      insertQuery.setLong(4, this.delay);
      insertQuery.setInt(5, this.population.getMax());
      insertQuery.setString(6, this.population.getEntityName());
      insertQuery.setLong(7, this.lastEventTime);
      insertQuery.setDouble(8, this.location.getX());
      insertQuery.setDouble(9, this.location.getY());
      insertQuery.setDouble(10, this.location.getZ());

      insertQuery.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Marque un terrier comme détruit en base de données
   */
  private void deleteFromDatabase() {
    try (PreparedStatement deleteQuery = McFrConnection.getServerConnection()
        .prepareStatement("UPDATE Burrow SET dead = 1 WHERE id = ?")){

      deleteQuery.setInt(1, this.id);
      deleteQuery.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Sauvegarde le nouvel état du terrier en base de données
   */
  private void saveInDatabase() {
    try (PreparedStatement updateQuery = McFrConnection.getServerConnection()
          .prepareStatement("UPDATE Burrow SET name = ?, timer = ?, maxPopulation = ?, lastEventTime = ?, x = ?, y = ?, z = ? WHERE id = ?")){

      updateQuery.setString(1, this.name);
      updateQuery.setLong(2, this.delay);
      updateQuery.setInt(3, this.population.getMax());
      updateQuery.setLong(4, this.lastEventTime);
      updateQuery.setDouble(5, this.location.getX());
      updateQuery.setDouble(6, this.location.getY());
      updateQuery.setDouble(7, this.location.getZ());
      updateQuery.setInt(8, this.id);

      updateQuery.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Reset la population du terrier
   */
  public void reset() {
    this.population.reset();
  }
  private Location<World> getLocation() {
    return this.location;
  }

  /**
   * Calcule la distance euclidienne entre le terrier et une position donnée.
   * @param location Position de référence
   * @return Distance entre la position de référence et le terrier
   */
  private double distance(Location<World> location) {
    return getLocation().getPosition().distance(location.getPosition());
  }

  public String getName() {
    return this.name;
  }

  public int getId() {
    return this.id;
  }

  public BurrowPopulation getPopulation() {
    return this.population;
  }

  /**
   * @return Chaîne de caractère formatée indiquant le délai du terrier
   */
  public String getFormatedDelay() {
    long minutes = this.delay / MINUTE_TO_MILLISECONDS;
    long hours = (long) Math.floor(minutes / 60.0F);
    minutes = minutes % 60;
    return hours + "h" + minutes + "m";
  }

  /**
   * @return Nom de l'entité que le terrier fait spawner
   */
  public String getEntityName() {
    return this.population.getEntityName();
  }

  /**
   * @return Chaîne de caractère formatée indiquant la position du terrier
   */
  public String getFormatedPosition() {
    return this.location.getBlockX() + " " + (this.location.getBlockY() + 1) + " " + this.location.getBlockZ();
  }

  public void setName(String name) {
    this.name = name;
    saveInDatabase();
  }

  public void setMalePopulation(int newMalePopulation) {
    this.population.setMales(newMalePopulation);
  }

  public void setFemalePopulation(int newFemalePopulation) {
    this.population.setFemales(newFemalePopulation);
  }

  /**
   * Règle le temps d'intervale entre une naissance et le dernier événement
   * 
   * @param delay
   *          En minutes
   */
  public void setDelay(long delay) {
    this.delay = delay * MINUTE_TO_MILLISECONDS;
    saveInDatabase();
  }

  /**
   * Déplace le terrier à une nouvelle position.
   * @param location Nouvel emplacement
   */
  public void setLocation(Location<World> location) {
    Vector3i prevPosition = this.location.getBlockPosition();
    this.location = location;
    this.population.setLocation(location);
    saveInDatabase();
    moveDisplay(prevPosition);
  }

  public void setMaximumPopulation(int maxPopulation) {
    this.population.setMax(maxPopulation);
    saveInDatabase();
  }

  /**
   * Rend le terrier visible pour un joueur.
   * @param player Joueur pour lequel le terrier doit être visible
   */
  public void setVisible(Player player) {
    BlockState blockState;
    Optional<Burrow> burrowOpt = McFrPlayer.getMcFrPlayer(player).getSelectedBurrow();

    if (burrowOpt.isPresent() && burrowOpt.get().equals(this)) {
      blockState = BlockTypes.BEACON.getDefaultState();
    } else {
      blockState = BlockTypes.MOB_SPAWNER.getDefaultState();
    }

    player.sendBlockChange(this.location.getBlockPosition(), blockState);

  }

  /**
   * Rend le terrier invisible pour un joueur
   * @param player Joueur pour lequel le terrier doit être invisible
   */
  private void setInvisible(Player player) {
    player.resetBlockChange(this.location.getBlockPosition());
  }

  /**
   * Met à jour la visualisation d'un terrier pour tous les joueurs pouvant le voir
   * @param prevPosition Ancien emplacement du terrier
   */
  private void moveDisplay(Vector3i prevPosition) {
    Sponge.getServer().getOnlinePlayers().stream().filter(p -> McFrPlayer.getMcFrPlayer(p).seesBurrows()).forEach(p -> {
      p.resetBlockChange(prevPosition);
      setVisible(p);
    });
  }

  /**
   * Rend le terrier visible pour tous les joueurs qui voient les terriers
   */
  private void setVisibleForAll() {
    Sponge.getServer().getOnlinePlayers().stream().filter(p -> McFrPlayer.getMcFrPlayer(p).seesBurrows()).forEach(p -> setVisible(p));
  }

  /**
   * Rend le terrier invisible pour tous les joueurs qui voient les terriers
   */
  private void setInvisibleForAll() {
    Sponge.getServer().getOnlinePlayers().stream().filter(p -> McFrPlayer.getMcFrPlayer(p).seesBurrows()).forEach(p -> setInvisible(p));
  }

  /**
   * Crée un nouveau terrier avec les paramètes spécifiés. Le type de l'entité spécifié peut ne pas exister, auquel cas le terrier n'est pas créé et la méthode renvoie un optionnel vide.
   * @param id
   * @param name
   * @param location
   * @param delay
   * @param maxPopulation
   * @param initMalePopulation
   * @param initFemalePopulation
   * @param entityType Optionnel contenant le type d'entité, ou vide
   * @return Optionnel contenant le terrier créé, ou vide si pas de terrier créé
   */
  private static Optional<Burrow> createBurrow(int id, Optional<String> name, Location<World> location, long delay, int maxPopulation,
      int initMalePopulation, int initFemalePopulation, Optional<EntityType> entityType) {

    if (entityType.isPresent()) {
      Burrow newBurrow = new Burrow(id, name, location, delay, maxPopulation, initMalePopulation, initFemalePopulation, entityType.get());
      burrows.add(newBurrow);
      newBurrow.registerInDatabase();
      return Optional.of(newBurrow);
    }
    return Optional.empty();
  }

  /**
   * Cherche le type d'entité correspondant à la classe d'entité fournie, puis crée le terrier avec les paramètres renseignés.
   * Si le type d'entité n'est pas reconnu, le terrier n'est pas créé et un optionel vide est retourné.
   * @param name
   * @param location
   * @param delay
   * @param maxPopulation
   * @param initMalePopulation
   * @param initFemalePopulation
   * @param entityClass
   * @return Optionnel contenant le terrier créé, ou vide si pas de terrier créé
   */
  public static Optional<Burrow> createBurrow(Optional<String> name, Location<World> location, long delay, int maxPopulation, int initMalePopulation,
      int initFemalePopulation, Class<? extends EntityGendered> entityClass) {
    Optional<EntityType> entityType = Sponge.getGame().getRegistry().getAllOf(EntityType.class).stream()
        .filter(e -> e.getEntityClass().equals(entityClass)).findAny();
    return createBurrow(getUnusedId(), name, location, delay, maxPopulation, initMalePopulation, initFemalePopulation, entityType);
  }

  /**
   * Crée un terrier qui était présent en base de données.
   * @param id
   * @param name
   * @param location
   * @param delay
   * @param lastEventTime
   * @param maxPopulation
   * @param entityType
   */
  private static void loadBurrow(int id, Optional<String> name, Location<World> location, long delay, long lastEventTime, int maxPopulation,
      Optional<EntityType> entityType) {
    if (entityType.isPresent()) {
      Burrow loadedBurrow = new Burrow(id, name, location, delay, lastEventTime, maxPopulation, entityType.get());
      burrows.add(loadedBurrow);
    }
  }

  /**
   * Détruit un terrier, les entités qui le composent et le marque comme détruit en base de données.
   */
  public void removeBurrow() {
    Sponge.getServer().getOnlinePlayers().stream().filter(p -> McFrPlayer.getMcFrPlayer(p).getSelectedBurrow().orElse(null) == this)
        .forEach(p -> McFrPlayer.getMcFrPlayer(p).unselectBurrow());
    setInvisibleForAll();

    burrows.remove(this);
    getPopulation().killAllEntities();
    deleteFromDatabase();
  }

  /**
   * Renvoit un optionnel contenant s'il existe le terrier le plus proche de l'emplacement indiqué.
   * @param searchLocation Emplacement de référence
   * @return Optionnel contenant le terrier le plus proche du lieu de référence s'il existe, vide sinon
   */
  public static Optional<Burrow> getNearestBurrow(Location<World> searchLocation) {
    return burrows.stream().filter(b -> b.getLocation().getExtent().equals(searchLocation.getExtent()))
        .min((o1, o2) -> Double.compare(o1.distance(searchLocation), o2.distance(searchLocation)));
  }

  /**
   * @param name Nom du terrier à chercher
   * @return Optionnel contenant le premier terrier dont le nom correpond au nom du terrier à chercher, vide s'il n'existe pas
   */
  public static Optional<Burrow> getBurrowByName(String name) {
    return burrows.stream().filter(b -> b.getName().equals(name)).findFirst();
  }

  /**
   * @param id Numéro d'identification à chercher
   * @return Optionnel contenant le premier terrier dont l'identifiant est celui spécifié, vide s'il n'existe pas
   */
  public static Optional<Burrow> getBurrowById(int id) {
    return burrows.stream().filter(b -> b.getId() == id).findFirst();
  }

  /**
   * @return La liste de tous les terriers
   */
  public static List<Burrow> getAll() {
    return burrows;
  }

  /**
   * @return Vrai si le terrier est toujours mis à jour par le système, faux sinon
   */
  public boolean isBurrowAlive() {
    return burrows.contains(this);
  }

  /**
   * Lance la mise à jour de tous les terriers actifs.
   */
  public static void updateBurrows() {
    burrows.forEach(Burrow::update);
  }

  /**
   * @return Le plus petit entier non utilisé par les identifiants des terriers existants
   */
  public static int getUnusedId() {
    int id = 0;
    try (PreparedStatement getUnusedId = McFrConnection.getServerConnection().prepareStatement("SELECT MAX(id)+1 FROM Burrow")) {
      ResultSet idData = getUnusedId.executeQuery();
      
      if (idData.next()) {
        id = idData.getInt(1);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return id;
  }

  /**
   * Rend tous les terriers visibles pour un joueur.
   * @param player Joueur pour lequel tous les terriers doivent être rendus visibles
   */
  public static void setAllVisible(Player player) {
    burrows.forEach(b -> b.setVisible(player));
  }

  /**
   * Rend tous les terriers invisibles pour un joueur.
   * @param player Joueur pour lequel tous les terriers doivent être rendus invisibles
   */
  public static void setAllInvisible(Player player) {
    burrows.forEach(b -> b.setInvisible(player));
  }

  /**
   * Charge les terriers contenus en base de données.
   * @return Chaîne de caractère indiquant le nombre de terriers chargés et actifs
   */
  public static String loadFromDatabase() {
    try (PreparedStatement getBurrows = McFrConnection.getServerConnection()
        .prepareStatement("SELECT id, name, timer, maxPopulation, entity, lastEvent, world, x, y, z FROM AliveBurrows")){
      ResultSet burrowData = getBurrows.executeQuery();
      Location<World> location;
      String worldName;
      String entityTypeName;
      EntityType entityType = null;
      int count = 0;

      while (burrowData.next()) {
        int id = burrowData.getInt("id");
        if (burrows.stream().filter(b -> b.getId() == id).count() == 0) {
          worldName = burrowData.getString("world");
          location = new Location<>(Sponge.getServer().getWorld(worldName).get(), burrowData.getDouble("x"), burrowData.getDouble("y"),
              burrowData.getDouble("z"));

          entityTypeName = burrowData.getString("entity");
          for (EntityType eT : Sponge.getGame().getRegistry().getAllOf(EntityType.class)) {
            if (eT.getName().equals(entityTypeName)) {
              entityType = eT;
            }
          }

          loadBurrow(id, Optional.of(burrowData.getString("name")), location, burrowData.getLong("timer") / MINUTE_TO_MILLISECONDS,
              burrowData.getLong("lastEvent"), burrowData.getInt("maxPopulation"), Optional.of(entityType));
          count++;
        }
      }
      burrowData.close();
      return "Chargement de " + count + " terriers depuis la BDD. Total de terriers : " + burrows.size() + ".";
    } catch (SQLException e) {
      e.printStackTrace();
      return "Le chargement des terriers a rencontré une erreur.";
    }
  }

  /**
   * Sauvegarde tous les terriers en base de données
   */
  public static void save() {
    burrows.forEach(Burrow::saveInDatabase);
  }
}
