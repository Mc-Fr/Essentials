package net.mcfr.burrows;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

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
  private List<UUID> maleEntityIds;
  private List<UUID> femaleEntityIds;
  private EntityType entityType;
  private int burrowId;

  public BurrowPopulation(Location<World> location, int burrowId, int max, int males, int females, EntityType entityType) {
    this.location = location;
    this.burrowId = burrowId;
    
    this.max = Math.min(max, MAX_POPULATION);
    this.males = Math.min(males, this.max);
    this.females = Math.min(females, this.max - this.males);

    this.prevMales = this.males;
    this.prevFemales = this.females;

    this.entityType = entityType;
    this.maleEntityIds = new LinkedList<>();
    this.femaleEntityIds = new LinkedList<>();
  }
  
  public void removeEntity(UUID id) {
    this.maleEntityIds.remove(id);
    this.femaleEntityIds.remove(id);
  }
  
  public void count() {
    this.males = this.maleEntityIds.size();
    this.females = this.femaleEntityIds.size();
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
      this.count();
      this.actualize();
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
    int males = this.males;
    int females = this.females;
    this.killAllEntities();
    this.males = males;
    this.females = females;
    this.spawnAllEntities();
  }

  private Genders spawnEntity(Genders gender) {
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
    
    if (((EntityGendered) entity).getGender() == Genders.MALE) {
      this.maleEntityIds.add(entity.getUniqueId());
      return Genders.MALE;
    } else {
      this.femaleEntityIds.add(entity.getUniqueId());
      return Genders.FEMALE;
    }
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
    for (UUID i : this.maleEntityIds) {
      Optional<Entity> optEntity = this.location.getExtent().getEntity(i);
      if (optEntity.isPresent()) {
        optEntity.get().remove();
      }
    }
    for (UUID i : this.femaleEntityIds) {
      Optional<Entity> optEntity = this.location.getExtent().getEntity(i);
      if (optEntity.isPresent()) {
        optEntity.get().remove();
      }
    }
    this.maleEntityIds.clear();
    this.femaleEntityIds.clear();
    this.males = 0;
    this.females = 0;
  }

  public void setMales(int males) {
    int females = this.females;
    killAllEntities();
    this.females = females;
    this.males = Integer.min(males, this.max - females);
    spawnAllEntities();
  }

  public void setFemales(int females) {
    int males = this.males;
    killAllEntities();
    this.males = males;
    this.females = Integer.min(females, this.max - males);
    spawnAllEntities();
  }

  public void setMax(int max) {
    this.max = Integer.min(max, MAX_POPULATION);
    float population = this.males + this.females;

    if (population > this.max) {
      this.males = (int) Math.floor(1.0D * this.max / 2.0D);
      this.females = this.max - this.males;
      reset();
    }
  }

  public int getMax() {
    return this.max;
  }

  public int getMales() {
    return this.males;
  }

  public int getFemales() {
    return this.females;
  }

  public String getEntityName() {
    return this.entityType.getName();
  }

  public void setLocation(Location<World> location) {
    this.location = location;
  }
}
