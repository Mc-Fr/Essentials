package net.mcfr.warp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.mcfr.dao.DaoFactory;

public class WarpImp implements WarpService {
  /**
   * Liste des {@code Warp}s.
   */
  private static Map<String, Warp> warps = new HashMap<>();

  public WarpImp() {
  }
  
  /**
   * Si les {@code Warp}s n'ont jamais été utilisées, les charge depuis la base de données.
   * 
   * @return la liste des {@code Warp}s
   */
  public static Map<String, Warp> getWarps() {
    return warps;
  }
  
  public static Set<String> getWarpNames() {
    return warps.keySet();
  }
  
  @Override
  public void loadFromDatabase() {
    List<Warp> warpList = DaoFactory.getWarpDao().getAll();
    warpList.forEach(warp -> warps.put(warp.getName(), warp));
  }

  @Override
  public Warp getWarp(String name) {
    return warps.get(name);
  }
  
  @Override
  public boolean addWarp(Warp warp) {
    if (DaoFactory.getWarpDao().create(warp)) {
      warps.put(warp.getName(), warp);
      return true;
    }
    return false;
  }
  
  @Override
  public boolean deleteWarp(Warp warp) {
    if (DaoFactory.getWarpDao().delete(warp)) {
      warps.remove(warp.getName());
      return true;
    }
    return false;
  }
}
