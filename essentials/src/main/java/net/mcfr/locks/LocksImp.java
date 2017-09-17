package net.mcfr.locks;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3i;

import net.mcfr.dao.DaoFactory;

public class LocksImp implements LocksService {
  private final List<BlockType> lockableBlocks = new ArrayList<>();
  private final List<BlockType> doors = new ArrayList<>();
  
  List<Lock> locks = new ArrayList<Lock>();

  public LocksImp() {
    doors.add(BlockTypes.ACACIA_DOOR);
    doors.add(BlockTypes.BIRCH_DOOR);
    doors.add(BlockTypes.DARK_OAK_DOOR);
    doors.add(BlockTypes.IRON_DOOR);
    doors.add(BlockTypes.JUNGLE_DOOR);
    doors.add(BlockTypes.SPRUCE_DOOR);
    doors.add(BlockTypes.WOODEN_DOOR);
    
    /*doors.add(Sponge.getGame().getRegistry().getType(BlockType.class, "mcfr_b_i:strong_oak_door").get());
    
    doors.add(Sponge.getGame().getRegistry().getType(BlockType.class, "mcfr_b_i:craftsman_oak_door").get());
    doors.add(Sponge.getGame().getRegistry().getType(BlockType.class, "mcfr_b_i:craftsman_spruce_door").get());
    doors.add(Sponge.getGame().getRegistry().getType(BlockType.class, "mcfr_b_i:craftsman_birch_door").get());
    doors.add(Sponge.getGame().getRegistry().getType(BlockType.class, "mcfr_b_i:craftsman_jungle_door").get());
    doors.add(Sponge.getGame().getRegistry().getType(BlockType.class, "mcfr_b_i:craftsman_acacia_door").get());
    doors.add(Sponge.getGame().getRegistry().getType(BlockType.class, "mcfr_b_i:craftsman_dark_oak_door").get());*/
    
    for (BlockType b : doors)
      lockableBlocks.add(b);
    
    lockableBlocks.add(Sponge.getGame().getRegistry().getType(BlockType.class, "mcfr_b_i:strong_oak_trapdoor").get());
    
    lockableBlocks.add(Sponge.getGame().getRegistry().getType(BlockType.class, "mcfr_b_i:strong_oak_trapdoor").get());
    lockableBlocks.add(Sponge.getGame().getRegistry().getType(BlockType.class, "mcfr_b_i:strong_spruce_trapdoor").get());
    lockableBlocks.add(Sponge.getGame().getRegistry().getType(BlockType.class, "mcfr_b_i:strong_birch_trapdoor").get());
    lockableBlocks.add(Sponge.getGame().getRegistry().getType(BlockType.class, "mcfr_b_i:strong_jungle_trapdoor").get());
    lockableBlocks.add(Sponge.getGame().getRegistry().getType(BlockType.class, "mcfr_b_i:strong_acacia_trapdoor").get());
    lockableBlocks.add(Sponge.getGame().getRegistry().getType(BlockType.class, "mcfr_b_i:strong_dark_oak_trapdoor").get());
    
    lockableBlocks.add(Sponge.getGame().getRegistry().getType(BlockType.class, "mcfr_b_i:craftsman_oak_trapdoor").get());
    lockableBlocks.add(Sponge.getGame().getRegistry().getType(BlockType.class, "mcfr_b_i:craftsman_spruce_trapdoor").get());
    lockableBlocks.add(Sponge.getGame().getRegistry().getType(BlockType.class, "mcfr_b_i:craftsman_birch_trapdoor").get());
    lockableBlocks.add(Sponge.getGame().getRegistry().getType(BlockType.class, "mcfr_b_i:craftsman_jungle_trapdoor").get());
    lockableBlocks.add(Sponge.getGame().getRegistry().getType(BlockType.class, "mcfr_b_i:craftsman_acacia_trapdoor").get());
    lockableBlocks.add(Sponge.getGame().getRegistry().getType(BlockType.class, "mcfr_b_i:craftsman_dark_oak_trapdoor").get());
    
    lockableBlocks.add(BlockTypes.CHEST);
    
    lockableBlocks.add(BlockTypes.IRON_TRAPDOOR);
    lockableBlocks.add(BlockTypes.TRAPDOOR);
    
    lockableBlocks.add(Sponge.getGame().getRegistry().getType(BlockType.class, "mcfr_b_i:little_chest").get());
  }
  
  @Override
  public List<BlockType> getLockableBlocks() {
    return this.lockableBlocks;
  }
  
  @Override
  public List<BlockType> getDoors() {
    return this.doors;
  }
  
  @Override
  public void loadFromDatabase() {
    for (Lock o : DaoFactory.getLockDao().getAll()) {
      if (lockableBlocks.contains(o.getWorld().getBlock(o.getPosition()).getType()))
        locks.add(o);
      else
        DaoFactory.getLockDao().delete(o);
    }
  }

  @Override
  public boolean isLocked(Vector3i pos, World world) {
    return locks.stream().filter(o -> o.isAtLocation(pos, world)).filter(o -> o.isLocked()).findAny().isPresent();
  }

  @Override
  public LockResult switchLock(Vector3i pos, World world, Optional<Integer> code) {
    Optional<Lock> optLock = locks.stream().filter(o -> o.isAtLocation(pos, world)).findAny();

    if (optLock.isPresent()) {
      Lock lock = optLock.get();

      if (code.isPresent() && lock.isCodeCorrect(code.get())) {
        if (lock.isLocked()) {
          lock.unlock();
          DaoFactory.getLockDao().changeState(lock, false);
          return LockResult.UNLOCKED;
        } else {
          lock.lock();
          DaoFactory.getLockDao().changeState(lock, true);
          return LockResult.LOCKED;
        }
      }
      return LockResult.WRONG_CODE;
    }
    return LockResult.NO_LOCK;
  }

  @Override
  public LockResult addLock(Vector3i pos, World world, Optional<Integer> code) {
    if (locks.stream().filter(o -> o.isAtLocation(pos, world)).findAny().isPresent())
      return LockResult.ALREADY_ADDED;

    if (!code.isPresent())
      return LockResult.WRONG_CODE;
    
    Lock lock = new Lock(pos, world, code.get(), false);
    DaoFactory.getLockDao().create(lock);
    locks.add(lock);

    return LockResult.ADDED;
  }

  @Override
  public LockResult removeLock(Vector3i pos, World world) {
    Optional<Lock> optLock = Optional.empty();
    
    for (Lock l : locks) {
      if (l.mustBeDestroyed(pos, world))
        optLock = Optional.of(l);
    }
    
    if (optLock.isPresent()) {
      Lock lock = optLock.get();
      locks.remove(lock);
      DaoFactory.getLockDao().delete(lock);
      
      return LockResult.REMOVED;
    }
    return LockResult.NO_LOCK;
  }

  public enum LockResult {
    NO_LOCK,
    WRONG_CODE,
    LOCKED,
    UNLOCKED,
    ADDED,
    REMOVED,
    ALREADY_ADDED;
  }
}
