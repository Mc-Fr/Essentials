package net.mcfr.burrows;

import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.spawn.EntitySpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3i;

import net.mcfr.entities.mobs.EntityBurrowed;
import net.mcfr.entities.mobs.gender.EntityGendered;
import net.mcfr.entities.mobs.gender.Genders;
import net.mcfr.listeners.BurrowListener;

public class BurrowPopulation {
  /**
   * Population maximale que les terriers peuvent posséder
   */
  private final static int MAX_POPULATION = 40;
  /**
   * Probabilité qu'une naissance ait lieu si toutes les conditions sont réunies
   */
  private final static float BIRTH_CHANCE = 0.8F;
  /**
   * Probabilité qu'une naissance ait lieu si toutes les conditions sont réunies mais que le terrier n'est pas mixte
   */
  private final static float BIRTH_CHANCE_NOMIX = 0.15F;
  private final static Random RAND = new Random();

  /**
   * Emplacement du terrier
   */
  private Location<World> location;
  /**
   * Population maximale du terrier
   */
  private int max;
  /**
   * Nombre de créatures mâles dans le terrier
   */
  private int males;
  /**
   * Nombre de créatures femelles dans le terrier
   */
  private int females;
  /**
   * Nombre de créatures mâles dans le terrier à la dernière vérification
   */
  private int prevMales;
  /**
   * Nombre de créatures femelles dans le terrier à la dernière vérification
   */
  private int prevFemales;
  /**
   * Type d'entités qui composent la population du terrier
   */
  private EntityType entityType;
  /**
   * Identifiant du terrier qui possède cette population
   */
  private int burrowId;

  public BurrowPopulation(Location<World> location, int burrowId, int max, EntityType entityType) {    
    this.location = location;
    this.burrowId = burrowId;
    
    this.max = Math.min(max, MAX_POPULATION);
    this.entityType = entityType;
    
    count();
    actualize();
  }
  
  /**
   * Fais apparaître les membres d'une nouvelle population
   * @param males Mâles dans la population
   * @param females Femelles dans la population
   */
  public void spawnNewEntities(int males, int females) {
    this.males = Math.min(males, this.max);
    this.females = Math.min(females, this.max - this.males);
    
    spawnAllEntities();
  }
  
  /**
   * Fait le compte des créatures mâles et femelles dans le terrier
   */
  public void count() {
    this.males = 0;
    this.females = 0;
    
    getEntities().forEach(e -> {
      if (((EntityGendered) e).getGender() == Genders.MALE) {
        this.males ++;
      } else {
        this.females ++;
      }
    });
  }
  
  /**
   * @return Stream sur les entités appartenant à ce terrier
   */
  public Stream<Entity> getEntities() {
    BurrowListener.loadAllOccupiedChunks(this);
    
    Stream<Entity> stream = Stream.empty();
    Optional<World> optWorld = Sponge.getServer().getWorld(location.getExtent().getName());
    
    if (optWorld.isPresent()) {
      stream = optWorld.get().getEntities(e -> e instanceof EntityBurrowed).stream().filter(e -> ((EntityBurrowed)e).getBurrow() == this.burrowId);
    }
    
    return stream;
  }
  
  public int getId() {
    return this.burrowId;
  }
  
  /**
   * @return Le monde dans lequel le terrier se trouve
   */
  public World getWorld() {
    return this.location.getExtent();
  }

  /**
   * Fais passer les valeurs actuelles de démographie dans les champs prevFemales et prevMales.
   */
  public void actualize() {
    this.prevFemales = this.females;
    this.prevMales = this.males;
  }

  /**
   * @return Vrai si il y a eu des morts depuis la dernière actualisation de la population
   */
  public boolean hasBeenDeaths() {
    return (this.prevFemales - this.females + this.prevMales - this.males) > 0;
  }

  /**
   * Tente de réaliser une naissance si toutes les conditions sont réunies.
   */
  public void tryBirth() {
    if (birthAvailable()) {
      spawnEntity(Genders.RANDOM);
      count();
      actualize();
    }
  }

  /**
   * @return Vrai si la naissance peut et doit avoir lieu, faux sinon
   */
  private boolean birthAvailable() {

    boolean needsBirth = this.males + this.females < this.max;
    boolean hasMaleAndFemale = (this.males > 0 && this.females > 0) || (RAND.nextFloat() < BIRTH_CHANCE_NOMIX);
    boolean birthRandom = RAND.nextFloat() < BIRTH_CHANCE;

    return needsBirth && hasMaleAndFemale && birthRandom;
  }

  /**
   * @return Vrai si la population du terrier est à zéro, faux sinon
   */
  public boolean isEmpty() {
    return this.males == 0 && this.females == 0;
  }

  /**
   * Téléporte toutes les entités appartenant au terrier sur l'emplacement du terrier.
   * Paramètre le lieu de repos des créatures comme l'emplacement du terrier : elles y retourneront une fois la nuit tombée.
   */
  public void reset() {
    getEntities().forEach(e -> {
      e.setLocationSafely(this.location);
      ((EntityBurrowed)e).setBurrow(this.burrowId, this.location.getBlockX(), this.location.getBlockY(), this.location.getBlockZ());
    });
  }

  /**
   * Fait apparaître une entité appartenant au terrier du genre indiqué.
   * @param gender Genre de l'entité
   */
  private void spawnEntity(Genders gender) {
    World world = this.location.getExtent();
    Vector3i spawnPosition = this.getPreparedPosition();

    Entity entity = world.createEntity(this.entityType, spawnPosition);
    Cause cause = Cause.source(EntitySpawnCause.builder()
        .entity(entity).type(SpawnTypes.PLUGIN).build()).build();
    
    if (!gender.equals(Genders.RANDOM)) {
      ((EntityGendered) entity).setGender(gender);
    }
    
    ((EntityBurrowed) entity).setBurrow(this.burrowId, this.location.getBlockX(), this.location.getBlockY(), this.location.getBlockZ());

    world.spawnEntity(entity, cause);
  }

  /**
   * @return Une position de spawn sécurisée et proche de l'emplacement de terrier
   */
  private Vector3i getPreparedPosition() {
    Vector3i randomPosition = new Vector3i(RAND.nextInt(3) - 1, 0, RAND.nextInt(3) - 1);
    Vector3i horizontalSpawnPosition = this.location.getBlockPosition().add(randomPosition);
    Vector3i spawnPosition = null;
    for (int i = 0; i < 4 && spawnPosition == null; i++) {
      if (this.location.getExtent().getBlockType(horizontalSpawnPosition.add(0, i, 0)).equals(BlockTypes.AIR)) {
        if (!this.location.getExtent().getBlockType(horizontalSpawnPosition.add(0, i - 1, 0)).equals(BlockTypes.AIR)) {
          spawnPosition = horizontalSpawnPosition.add(0, i, 0);
        }
      }
    }
    
    if (spawnPosition == null) {
      spawnPosition = horizontalSpawnPosition;
    }
    
    Optional<Chunk> chunkOpt = this.location.getExtent().getChunkAtBlock(spawnPosition);
    if (chunkOpt.isPresent() && !chunkOpt.get().isLoaded()) {
      chunkOpt.get().loadChunk(false);
    }
    
    return spawnPosition;
  }

  /**
   * Fais apparaître autant d'entités de chaque genre que ce que le terrier doit contenir.
   */
  public void spawnAllEntities() {
    for (int i = 0; i < this.males; i++) {
      spawnEntity(Genders.MALE);
    }
    for (int i = 0; i < this.females; i++) {
      spawnEntity(Genders.FEMALE);
    }
  }

  /**
   * Fais disparaître toutes les entités appartenant au terrier.
   */
  public void killAllEntities() {
    getEntities().forEach(e -> ((EntityBurrowed)e).setToRemove(true));
    this.males = 0;
    this.females = 0;
  }

  /**
   * Change la population de mâles dans le terrier, sous réserve de place. Si la quantité renseignée est plus petite que la quantité actuelle, les entités sont toutes tuées puis recréées.
   * @param males Nombre de mâles à atteindre
   */
  public void setMales(int males) {
    count();
    int females = this.females;
    killAllEntities();
    this.females = females;
    this.males = Integer.min(males, this.max - females);
    spawnAllEntities();
  }

  /**
   * Change la population de femelles dans le terrier, sous réserve de place. Si la quantité renseignée est plus petite que la quantité actuelle, les entités sont toutes tuées puis recréées.
   * @param females Nombre de femelles à atteindre
   */
  public void setFemales(int females) {
    count();
    int males = this.males;
    killAllEntities();
    this.males = males;
    this.females = Integer.min(females, this.max - males);
    spawnAllEntities();
  }

  /**
   * Change la population maximum du terrier.
   * Si la population actuelle dépasse la valeur renseignée, toutes les entités sont tuées puis une quantité équivalente de mâles et de femelles sont recréés.
   * @param max Population maximale de la tribu
   */
  public void setMax(int max) {
    count();
    this.max = Integer.min(max, MAX_POPULATION);
    float population = this.males + this.females;

    if (population > this.max) {
      killAllEntities();
      this.males = (int) Math.floor(1.0D * this.max / 2.0D);
      this.females = this.max - this.males;
      spawnAllEntities();
    }
  }

  public int getMax() {
    return this.max;
  }

  public int getMales() {
    count();
    return this.males;
  }

  public int getFemales() {
    count();
    return this.females;
  }

  public String getEntityName() {
    return this.entityType.getName();
  }

  public void setLocation(Location<World> location) {
    this.location = location;
  }
}
