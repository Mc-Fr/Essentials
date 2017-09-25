package net.mcfr.locks;

import java.util.List;
import java.util.Optional;

import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3i;

import net.mcfr.locks.LocksImp.LockResult;

public interface LocksService {
  public void loadFromDatabase();
  
  public List<BlockType> getLockableBlocks();
  
  public List<BlockType> getDoors();
  
  public boolean isLocked(Vector3i pos, World world);
  
  public Optional<Lock> getLock(Vector3i pos, World world);
  
  public LockResult switchLock(Vector3i pos, World world, Optional<Integer> code);
  
  public LockResult addLock(Vector3i pos, World world, Optional<Integer> code);
  
  public LockResult removeLock(Vector3i pos, World world);
}
