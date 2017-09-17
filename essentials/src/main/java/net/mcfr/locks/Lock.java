package net.mcfr.locks;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.PortionTypes;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3i;

public class Lock {
  private Vector3i position;
  private World world;
  private int code;
  private boolean isLocked;

  public Lock(Vector3i pos, World w, int c, boolean l) {
    this.position = pos;
    this.world = w;
    this.code = c;
    this.isLocked = l;
  }

  public boolean isCodeCorrect(int c) {
    return c == this.code;
  }

  public boolean isAtLocation(Vector3i pos, World w) {
    if (!this.world.equals(w))
      return false;
    
    BlockState lockBlock = this.world.getBlock(this.position);
    
    if (Sponge.getServiceManager().provide(LocksService.class).get().getDoors().contains(lockBlock.getType()) && pos.getX() == this.position.getX()
        && pos.getZ() == this.position.getZ()) {
      if (lockBlock.get(Keys.PORTION_TYPE).get() == PortionTypes.BOTTOM)
        return (pos.getY() == this.position.getY() || pos.getY() == this.position.getY() + 1);
      else
        return (pos.getY() == this.position.getY() || pos.getY() == this.position.getY() - 1);
    } else if (lockBlock.getType() == BlockTypes.CHEST && this.world.getBlock(pos).getType() == BlockTypes.CHEST && pos.getY() == this.position.getY()) {
      if (pos.getX() == this.position.getX())
        return (pos.getZ() == this.position.getZ() || pos.getZ() == this.position.getZ() + 1 || pos.getZ() == this.position.getZ() - 1);
      else if (pos.getZ() == this.position.getZ())
        return (pos.getX() == this.position.getX() || pos.getX() == this.position.getX() + 1 || pos.getX() == this.position.getX() - 1);
    } else {
      return this.position.equals(pos);
    }
    
    return false;
  }
  
  public boolean mustBeDestroyed(Vector3i pos, World w) {
    return (pos.equals(this.position) && this.world.equals(w));
  }

  public void lock() {
    this.isLocked = true;
  }

  public void unlock() {
    this.isLocked = false;
  }

  public int getCode() {
    return this.code;
  }

  public boolean isLocked() {
    return this.isLocked;
  }

  public Vector3i getPosition() {
    return this.position;
  }

  public World getWorld() {
    return this.world;
  }
}
