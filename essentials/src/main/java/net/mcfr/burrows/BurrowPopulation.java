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

public class BurrowPopulation {
  private final static int MAX_POPULATION = 40;
  private final static float BIRTH_CHANCE = 0.8F;
  private final static float BIRTH_CHANCE_NOMIX = 0.15F;
  private final static Random RAND = new Random();

  private Location<World> location;
  private int max;
  private int males;
  private int females;
  private int prevMales;
  private int prevFemales;
  private EntityType entityType;
  private int burrowId;

  public BurrowPopulation(Location<World> location, int burrowId, int max, EntityType entityType) {    
    this.location = location;
    this.burrowId = burrowId;
    
    this.max = Math.min(max, MAX_POPULATION);
    this.entityType = entityType;
    
    count();
    actualize();
  }
  
  public void spawnNewEntities(int males, int females) {
    this.males = Math.min(males, this.max);
    this.females = Math.min(females, this.max - this.males);
    
    spawnAllEntities();
  }
  
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
  
  public World getWorld() {
    return this.location.getExtent();
  }

  public void actualize() {
    this.prevFemales = this.females;
    this.prevMales = this.males;
  }

  public boolean hasBeenDeaths() {
    return (this.prevFemales - this.females + this.prevMales - this.males) > 0;
  }

  public void tryBirth() {
    if (birthAvailable()) {
      spawnEntity(Genders.RANDOM);
      count();
      actualize();
    }
  }

  private boolean birthAvailable() {

    boolean needsBirth = this.males + this.females < this.max;
    boolean hasMaleAndFemale = (this.males > 0 && this.females > 0) || (RAND.nextFloat() < BIRTH_CHANCE_NOMIX);
    boolean birthRandom = RAND.nextFloat() < BIRTH_CHANCE;

    return needsBirth && hasMaleAndFemale && birthRandom;
  }

  public boolean isEmpty() {
    return this.males == 0 && this.females == 0;
  }

  public void reset() {
    getEntities().forEach(e -> {
      e.setLocationSafely(this.location);
      ((EntityBurrowed)e).setBurrow(this.burrowId, this.location.getBlockX(), this.location.getBlockY(), this.location.getBlockZ());
    });
  }

  private void spawnEntity(Genders gender) {
    World world = this.location.getExtent();
    Vector3i spawnPosition = this.getPreparedPosition();

    Entity entity = world.createEntity(this.entityType, spawnPosition);
    Cause cause = Cause.source(EntitySpawnCause.builder()
        .entity(entity).type(SpawnTypes.BREEDING).build()).build();
    
    if (!gender.equals(Genders.RANDOM)) {
      ((EntityGendered) entity).setGender(gender);
    }
    
    ((EntityBurrowed) entity).setBurrow(this.burrowId, this.location.getBlockX(), this.location.getBlockY(), this.location.getBlockZ());

    world.spawnEntity(entity, cause);
  }

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

  public void spawnAllEntities() {
    for (int i = 0; i < this.males; i++) {
      spawnEntity(Genders.MALE);
    }
    for (int i = 0; i < this.females; i++) {
      spawnEntity(Genders.FEMALE);
    }
  }

  public void killAllEntities() {
    getEntities().forEach(e -> ((EntityBurrowed)e).setToRemove(true));
    this.males = 0;
    this.females = 0;
  }

  public void setMales(int males) {
    count();
    int females = this.females;
    killAllEntities();
    this.females = females;
    this.males = Integer.min(males, this.max - females);
    spawnAllEntities();
  }

  public void setFemales(int females) {
    count();
    int males = this.males;
    killAllEntities();
    this.males = males;
    this.females = Integer.min(females, this.max - males);
    spawnAllEntities();
  }

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
