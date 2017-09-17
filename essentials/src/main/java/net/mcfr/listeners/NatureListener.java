package net.mcfr.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.item.inventory.ItemStack;

import net.mcfr.commands.roleplay.KeyCodeCommand;
import net.mcfr.locks.LocksImp.LockResult;
import net.mcfr.locks.LocksService;

public class NatureListener {
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
  public void onBlockBroken(ChangeBlockEvent.Break event) {
    Optional<LocksService> optLocksService = Sponge.getServiceManager().provide(LocksService.class);

    if (optLocksService.isPresent()) {
      LocksService locksService = optLocksService.get();

      for (Transaction<BlockSnapshot> t : event.getTransactions()) {
        if (locksService.getLockableBlocks().contains(t.getOriginal().getState().getType())) {
          LockResult result = locksService.removeLock(t.getOriginal().getPosition(), event.getTargetWorld());
          
          if (result == LockResult.REMOVED) {
            ItemStack lockItem = ItemStack.builder().itemType(KeyCodeCommand.LOCK_ITEM).quantity(1).build();
            Item item = (Item) event.getTargetWorld().createEntity(EntityTypes.ITEM, t.getOriginal().getPosition());
            item.offer(Keys.REPRESENTED_ITEM, lockItem.createSnapshot());
            event.getTargetWorld().spawnEntity(item, event.getCause());
          }
        }
      }

    }
  }

  @Listener
  public void onBlockModified(ChangeBlockEvent.Modify event) {
    Optional<LocksService> optLocksService = Sponge.getServiceManager().provide(LocksService.class);

    if (optLocksService.isPresent()) {
      LocksService locksService = optLocksService.get();

      for (Transaction<BlockSnapshot> t : event.getTransactions()) {
        if (locksService.getLockableBlocks().contains(t.getOriginal().getState().getType())) {
          if (t.getOriginal().getState().getType() != t.getFinal().getState().getType()) {
            LockResult result = locksService.removeLock(t.getOriginal().getPosition(), event.getTargetWorld());
            if (result == LockResult.REMOVED) {
              ItemStack lockItem = ItemStack.builder().itemType(KeyCodeCommand.LOCK_ITEM).quantity(1).build();
              Item item = (Item) event.getTargetWorld().createEntity(EntityTypes.ITEM, t.getOriginal().getPosition());
              item.offer(Keys.REPRESENTED_ITEM, lockItem.createSnapshot());
              event.getTargetWorld().spawnEntity(item, event.getCause());
            }
          }
        }
      }

    }
  }
}
