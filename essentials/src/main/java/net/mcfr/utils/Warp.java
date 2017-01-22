package net.mcfr.utils;

import java.util.Map;

import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.Extent;

public class Warp {
  private static Map<String, Warp> warps;

  private String name;
  private Location<Extent> location;
  private boolean lock;

  public Warp(String name, Extent extent, int x, int y, int z) {
    this.name = name;
    this.location = new Location<>(extent, x, y, z);
    this.lock = false;
  }

  public String getName() {
    return this.name;
  }

  public String getWorldName() {
    return ((World) this.location.getExtent()).getName();
  }

  public int getX() {
    return this.location.getBlockX();
  }

  public int getY() {
    return this.location.getBlockY();
  }

  public int getZ() {
    return this.location.getBlockZ();
  }

  public boolean isLocked() {
    return this.lock;
  }
}
