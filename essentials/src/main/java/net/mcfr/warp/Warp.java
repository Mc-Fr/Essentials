package net.mcfr.warp;

import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class Warp {
  private String name;
  private Location<World> location;
  private boolean locked;

  public Warp(String name, Location<World> location) {
    this.name = name;
    this.location = location;
    this.locked = false;
  }

  public String getName() {
    return this.name;
  }

  public Location<World> getLocation() {
    return this.location;
  }

  public boolean isLocked() {
    return this.locked;
  }

  public void setLocked(boolean locked) {
    this.locked = locked;
  }

  public String getPermission() {
    return "essentials.warp" + (isLocked() ? "." + getName() : "");
  }
}
