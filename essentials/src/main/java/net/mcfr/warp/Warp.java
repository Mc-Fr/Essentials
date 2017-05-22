package net.mcfr.warp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import net.mcfr.dao.DaoFactory;

public class Warp {

  /**
   * Liste des {@code Warp}s.
   */
  private static Map<String, Warp> warps;

  /**
   * Si les {@code Warp}s n'ont jamais été utilisées, les charge depuis la base de données.
   * 
   * @return la liste des {@code Warp}s
   */
  public final static Map<String, Warp> getWarps() {
    if (warps == null) {
      warps = new HashMap<>();
      List<Warp> warpList = DaoFactory.getWarpDao().getAll();
      warpList.forEach(warp -> warps.put(warp.getName(), warp));
    }
    return warps;
  }

  public final static Warp getWarp(String name) {
    return warps.get(name);
  }

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
