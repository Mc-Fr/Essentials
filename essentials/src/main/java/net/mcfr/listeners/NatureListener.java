package net.mcfr.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.SpawnEntityEvent;

public class NatureListener {
  private static final float redFlowerSpawnChance = 1f;
  private static final float yellowFlowerSpawnChance = 0.001f;
  private static final Random rand = new Random();
  private static List<EntityType> forbiddenEntities = new ArrayList<>();

  static {
    forbiddenEntities.add(EntityTypes.BAT);
    forbiddenEntities.add(EntityTypes.BLAZE);
    forbiddenEntities.add(EntityTypes.CAVE_SPIDER);
    forbiddenEntities.add(EntityTypes.CHICKEN);
    forbiddenEntities.add(EntityTypes.COW);
    forbiddenEntities.add(EntityTypes.CREEPER);
    forbiddenEntities.add(EntityTypes.ENDER_DRAGON);
    forbiddenEntities.add(EntityTypes.ENDERMAN);
    forbiddenEntities.add(EntityTypes.GHAST);
    forbiddenEntities.add(EntityTypes.GIANT);
    forbiddenEntities.add(EntityTypes.GUARDIAN);
    forbiddenEntities.add(EntityTypes.HORSE);
    forbiddenEntities.add(EntityTypes.IRON_GOLEM);
    forbiddenEntities.add(EntityTypes.MAGMA_CUBE);
    forbiddenEntities.add(EntityTypes.MUSHROOM_COW);
    forbiddenEntities.add(EntityTypes.OCELOT);
    forbiddenEntities.add(EntityTypes.PIG);
    forbiddenEntities.add(EntityTypes.PIG_ZOMBIE);
    forbiddenEntities.add(EntityTypes.POLAR_BEAR);
    forbiddenEntities.add(EntityTypes.RABBIT);
    forbiddenEntities.add(EntityTypes.SHEEP);
    forbiddenEntities.add(EntityTypes.SILVERFISH);
    forbiddenEntities.add(EntityTypes.SKELETON);
    forbiddenEntities.add(EntityTypes.SLIME);
    forbiddenEntities.add(EntityTypes.SNOWMAN);
    forbiddenEntities.add(EntityTypes.SPIDER);
    forbiddenEntities.add(EntityTypes.SQUID);
    forbiddenEntities.add(EntityTypes.VILLAGER);
    forbiddenEntities.add(EntityTypes.WITCH);
    forbiddenEntities.add(EntityTypes.WITHER);
    forbiddenEntities.add(EntityTypes.WITHER_SKULL);
    forbiddenEntities.add(EntityTypes.WOLF);
    forbiddenEntities.add(EntityTypes.ZOMBIE);
  }

  @Listener
  public void onSpawnEntity(SpawnEntityEvent event) {
    event.setCancelled(event.getEntities().stream().filter(e -> forbiddenEntities.contains(e.getType())).count() != 0);
  }
}
