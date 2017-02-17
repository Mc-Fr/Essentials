package net.mcfr.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.TickBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3i;

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
  
  @Listener
  public void onBlockRandomTick(TickBlockEvent.Random event) {
    BlockSnapshot block = event.getTargetBlock();
    BlockType blockType = block.getExtendedState().getType();
    if (block.getLocation().isPresent()) {
      Location<World> location = block.getLocation().get();
      
      if (blockType.equals(BlockTypes.RED_FLOWER)) {
        if (rand.nextFloat() < redFlowerSpawnChance) {
          World world = location.getExtent();
          int dx = rand.nextInt(11) - 5;
          int dy = -2;
          int dz = rand.nextInt(11) - 5;
          Vector3i currentBlockPos,suppBlockPos;
          boolean isPlaced = false;
          
          while (!isPlaced && dy < 3) {
            currentBlockPos = location.getBlockPosition().add(dx, dy, dz);
            suppBlockPos = currentBlockPos.add(0, 1, 0);
            
            if (world.getBlock(currentBlockPos).getType().equals(BlockTypes.GRASS) && world.getBlock(suppBlockPos).getType().equals(BlockTypes.AIR)) {
              world.setBlock(suppBlockPos, BlockTypes.RED_FLOWER.getDefaultState(), Cause.source(block).build());
              isPlaced = true;
            } else {
              dy++;
            }
          }
        }
      }
    }
  }
}
