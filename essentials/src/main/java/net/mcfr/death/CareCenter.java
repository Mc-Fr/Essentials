package net.mcfr.death;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class CareCenter {
  
  private String name;
  private Location<World> location;
  
  public CareCenter(String name, int x, int y, int z) {
    this.name = name;
    this.location = new Location<>(Sponge.getServer().getWorld("world").get(), x, y, z);
  }
  
  public double distance(Location<World> loc) {
    return Math.hypot(loc.getX() - this.location.getX(), loc.getZ() - this.location.getZ());
  }
  
  public String getName() {
    return this.name;
  }
  
  public Location<World> getLocation() {
    return this.location;
  }
  
  @Override
  public String toString() {
    return this.name + " (" + this.location.getBlockX() + ", " + this.location.getBlockY() + ", " + this.location.getBlockZ() + ")";
  }
}
