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

import com.flowpowered.math.vector.Vector3d;
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
  private final static long M_TO_MS = 60000;

  /**
   * Liste de tous les terriers enregistrés
   */
  private static List<Burrow> burrows = new LinkedList<>();

  /**
   * ID du terrier en base de donnée
   */
  private int id;

  /**
   * Nom du terrier, pour les utilisateurs uniquement
   */
  private String name;

  /**
   * Endroit où se situe le terrier
   */
  private Location<World> location;

  /**
   * Intervale de temps entre une naissance et le dernier événement, en minutes
   */
  private long delay;

  /**
   * Date du dernier événement, en minutes
   */
  private long lastEventTime;

  /**
   * Population du terrier
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
    this.delay = delay * M_TO_MS;
    this.lastEventTime = lastEventTime;
    this.population = new BurrowPopulation(this.location, this.id, maxPopulation, entityType);

    setVisibleForAll();
  }

  private void update() {
    long currentTime = Calendar.getInstance().getTime().getTime();

    this.population.count();

    if (this.population.isEmpty()) {
      removeBurrow(this);
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

  private void registerInDatabase() {
    try {
      PreparedStatement insertQuery = McFrConnection.getServerConnection().getConnection().prepareStatement(
          "INSERT INTO Burrow(id, name, world, timer, maxPopulation, entityType, lastEventTime`, `x`, `y`, `z`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

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

  private void deleteFromDatabase() {
    try {
      PreparedStatement deleteQuery = McFrConnection.getServerConnection().getConnection()
          .prepareStatement("UPDATE Burrow SET dead = 1 WHERE id = ?");

      deleteQuery.setInt(1, this.id);
      deleteQuery.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private void saveInDatabase() {
    try {
      PreparedStatement updateQuery = McFrConnection.getServerConnection().getConnection()
          .prepareStatement("UPDATE Burrow SET name = ?, timer = ?, maxPopulation = ?, lastEventTime = ?, x = ?, y = ?, z = ? WHERE id = ?");

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

  public void reset() {
    this.population.reset();
  }

  private Location<World> getLocation() {
    return this.location;
  }

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

  public String getFormatedDelay() {
    long minutes = this.delay / M_TO_MS;
    long hours = (long) Math.floor(minutes / 60.0F);
    minutes = minutes % 60;
    return hours + "h" + minutes + "m";
  }

  public String getEntityName() {
    return this.population.getEntityName();
  }

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
    this.delay = delay * M_TO_MS;
    saveInDatabase();
  }

  public void setLocation(Location<World> location) {
    Vector3i prevPosition = this.location.getBlockPosition();
    this.location = location;
    this.population.setLocation(location);
    saveInDatabase();
    moveDisplay(prevPosition);
  }

  public void setPosition(Vector3d position) {
    setLocation(new Location<>(this.location.getExtent(), position));
  }

  public void setMaximumPopulation(int maxPopulation) {
    this.population.setMax(maxPopulation);
    saveInDatabase();
  }

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

  private void setInvisible(Player player) {
    player.resetBlockChange(this.location.getBlockPosition());
  }

  private void moveDisplay(Vector3i prevPosition) {
    Sponge.getServer().getOnlinePlayers().stream().filter(p -> McFrPlayer.getMcFrPlayer(p).seesBurrows()).forEach(p -> {
      p.resetBlockChange(prevPosition);
      setVisible(p);
    });
  }

  private void setVisibleForAll() {
    Sponge.getServer().getOnlinePlayers().stream().filter(p -> McFrPlayer.getMcFrPlayer(p).seesBurrows()).forEach(p -> setVisible(p));
  }

  private void setInvisibleForAll() {
    Sponge.getServer().getOnlinePlayers().stream().filter(p -> McFrPlayer.getMcFrPlayer(p).seesBurrows()).forEach(p -> setInvisible(p));
  }

  public static Optional<Burrow> createBurrow(int id, Optional<String> name, Location<World> location, long delay, int maxPopulation,
      int initMalePopulation, int initFemalePopulation, Optional<EntityType> entityType) {

    if (entityType.isPresent()) {
      Burrow newBurrow = new Burrow(id, name, location, delay, maxPopulation, initMalePopulation, initFemalePopulation, entityType.get());
      burrows.add(newBurrow);
      newBurrow.registerInDatabase();
      return Optional.of(newBurrow);
    }
    return Optional.empty();
  }

  public static Optional<Burrow> createBurrow(Optional<String> name, Location<World> location, long delay, int maxPopulation, int initMalePopulation,
      int initFemalePopulation, Class<? extends EntityGendered> entityClass) {
    Optional<EntityType> entityType = Sponge.getGame().getRegistry().getAllOf(EntityType.class).stream()
        .filter(e -> e.getEntityClass().equals(entityClass)).findAny();
    return createBurrow(getUnusedId(), name, location, delay, maxPopulation, initMalePopulation, initFemalePopulation, entityType);
  }

  public static void loadBurrow(int id, Optional<String> name, Location<World> location, long delay, long lastEventTime, int maxPopulation,
      Optional<EntityType> entityType) {
    if (entityType.isPresent()) {
      Burrow loadedBurrow = new Burrow(id, name, location, delay, lastEventTime, maxPopulation, entityType.get());
      burrows.add(loadedBurrow);
    }
  }

  public static void removeBurrow(Burrow burrow) {
    Sponge.getServer().getOnlinePlayers().stream().filter(p -> McFrPlayer.getMcFrPlayer(p).getSelectedBurrow().orElse(null) == burrow)
        .forEach(p -> McFrPlayer.getMcFrPlayer(p).unselectBurrow());
    burrow.setInvisibleForAll();

    burrows.remove(burrow);
    burrow.getPopulation().killAllEntities();
    burrow.deleteFromDatabase();
  }

  public static Optional<Burrow> removeBurrow(Optional<Burrow> burrowOpt) {
    burrowOpt.ifPresent(Burrow::removeBurrow);
    return burrowOpt;
  }

  public static Optional<Burrow> getNearestBurrow(Location<World> searchLocation) {
    return burrows.stream().filter(b -> b.getLocation().getExtent().equals(searchLocation.getExtent()))
        .min((o1, o2) -> Double.compare(o1.distance(searchLocation), o2.distance(searchLocation)));
  }

  public static Optional<Burrow> getBurrowByName(String name) {
    return burrows.stream().filter(b -> b.getName().equals(name)).findFirst();
  }

  public static Optional<Burrow> getBurrowById(int id) {
    return burrows.stream().filter(b -> b.getId() == id).findFirst();
  }

  public static List<Burrow> getAll() {
    return burrows;
  }

  public static boolean isBurrowAlive(Burrow burrow) {
    return burrows.contains(burrow);
  }

  public static void updateBurrows() {
    burrows.forEach(Burrow::update);
  }

  public static int getUnusedId() {
    int id = 0;
    try {
      ResultSet idData = McFrConnection.getServerConnection().executeQuery("SELECT MAX(id)+1 FROM Burrow"); // TODO A dégager
      if (idData.next()) {
        id = idData.getInt(1);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return id;
  }

  public static void setAllVisible(Player player) {
    burrows.forEach(b -> b.setVisible(player));
  }

  public static void setAllInvisible(Player player) {
    burrows.forEach(b -> b.setInvisible(player));
  }

  public static String loadFromDatabase() {
    try {
      ResultSet burrowData = McFrConnection.getServerConnection()
          .executeQuery("SELECT id, name, timer, maxPopulation, entity, lastEvent, world, x, y, z FROM AliveBurrows");
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

          loadBurrow(id, Optional.of(burrowData.getString("name")), location, burrowData.getLong("timer") / M_TO_MS, burrowData.getLong("lastEvent"),
              burrowData.getInt("maxPopulation"), Optional.of(entityType));
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

  public static void save() {
    burrows.forEach(Burrow::saveInDatabase);
  }
}
