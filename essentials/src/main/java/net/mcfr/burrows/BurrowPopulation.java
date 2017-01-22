package net.mcfr.burrows;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

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

import net.mcfr.entities.mobs.gender.EntityGendered;
import net.mcfr.entities.mobs.gender.Genders;

public class BurrowPopulation {
  private final static int MAX_POPULATION = 40;
  private final static float BIRTH_CHANCE = 0.5F;
  private final static Random RAND = new Random();

  private int max;
  private int males;
  private int females;
  private int prevMales;
  private int prevFemales;
  private List<Entity> entities;
  private EntityType entityType;

  public BurrowPopulation(int max, int males, int females, EntityType entityType) {
    this.max = Math.min(max, MAX_POPULATION);
    this.males = Math.min(males, this.max);
    this.females = Math.min(females, this.max - this.males);

    this.prevMales = this.males;
    this.prevFemales = this.females;

    this.entityType = entityType;
    this.entities = new LinkedList<>();
  }

  public void count() {
    this.males = (int) this.entities.stream().filter(e -> ((EntityGendered) e).getGender() == Genders.MALE && !e.isRemoved()).count();
    this.females = (int) this.entities.stream().filter(e -> ((EntityGendered) e).getGender() == Genders.FEMALE && !e.isRemoved()).count();
  }

  public void actualize() {
    this.prevFemales = this.females;
    this.prevMales = this.males;
  }

  public boolean hasBeenDeaths() {
    return (this.prevFemales - this.females + this.prevMales - this.males) > 0;
  }

  public void tryBirth(Location<World> location) {
    if (birthAvailable()) {
      Genders childGender = spawnEntity(Genders.RANDOM, location);

      if (childGender.equals(Genders.MALE)) {
        this.males++;
      } else {
        this.females++;
      }

      actualize();
    }
  }

  private boolean birthAvailable() {

    boolean needsBirth = this.males + this.females < this.max;
    boolean hasMaleAndFemale = this.males > 0 && this.females > 0;
    boolean birthRandom = RAND.nextFloat() < BIRTH_CHANCE;

    return needsBirth && hasMaleAndFemale && birthRandom;
  }

  public boolean isEmpty() {
    return this.males == 0 && this.females == 0;
  }

  public void reset(Location<World> location) {
    int males = this.males;
    int females = this.females;
    this.killAllEntities();
    this.males = males;
    this.females = females;
    this.spawnAllEntities(location);
  }

  private Genders spawnEntity(Genders gender, Location<World> location) {
    World world = location.getExtent();
    Vector3i spawnPosition = this.getPreparedPosition(location);

    Entity entity = world.createEntity(this.entityType, spawnPosition);
    Cause cause = Cause.source(EntitySpawnCause.builder()
        .entity(entity).type(SpawnTypes.PLUGIN).build()).build();

    if (!gender.equals(Genders.RANDOM)) {
      ((EntityGendered) entity).setGender(gender);
    }

    this.entities.add(entity);
    world.spawnEntity(entity, cause);
    return ((EntityGendered) entity).getGender();
  }

  private Vector3i getPreparedPosition(Location<World> location) {
    Vector3i randomPosition = new Vector3i(RAND.nextInt(3) - 1, 0, RAND.nextInt(3) - 1);
    Vector3i horizontalSpawnPosition = location.getBlockPosition().add(randomPosition);
    Vector3i spawnPosition = null;
    for (int i = 0; i < 4 && spawnPosition == null; i++) {
      if (location.getExtent().getBlockType(horizontalSpawnPosition.add(0, i, 0)).equals(BlockTypes.AIR)) {
        if (!location.getExtent().getBlockType(horizontalSpawnPosition.add(0, i - 1, 0)).equals(BlockTypes.AIR)) {
          spawnPosition = horizontalSpawnPosition.add(0, i, 0);
        }
      }
    }
    
    if (spawnPosition == null) {
      spawnPosition = horizontalSpawnPosition;
    }
    
    Optional<Chunk> chunkOpt = location.getExtent().getChunkAtBlock(spawnPosition);
    if (chunkOpt.isPresent() && !chunkOpt.get().isLoaded()) {
      chunkOpt.get().loadChunk(false);
    }
    
    return spawnPosition == null ? horizontalSpawnPosition : spawnPosition;
  }

  public void spawnAllEntities(Location<World> location) {
    for (int i = 0; i < this.males; i++) {
      spawnEntity(Genders.MALE, location);
    }
    for (int i = 0; i < this.females; i++) {
      spawnEntity(Genders.FEMALE, location);
    }
  }

  public void killAllEntities() {
    for (Entity e : this.entities) {
      e.remove();
    }
    this.entities.clear();
    this.males = 0;
    this.females = 0;
  }

  public void setMales(int males, Location<World> location) {
    int females = this.females;
    killAllEntities();
    this.females = females;
    this.males = Integer.min(males, this.max - females);
    spawnAllEntities(location);
  }

  public void setFemales(int females, Location<World> location) {
    int males = this.males;
    killAllEntities();
    this.males = males;
    this.females = Integer.min(females, this.max - males);
    spawnAllEntities(location);
  }

  public void setMax(int max, Location<World> location) {
    this.max = Integer.min(max, MAX_POPULATION);
    float population = this.males + this.females;

    if (population > this.max) {
      this.males = (int) Math.floor(1.0D * this.max / 2.0D);
      this.females = this.max - this.males;
      reset(location);
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
}
