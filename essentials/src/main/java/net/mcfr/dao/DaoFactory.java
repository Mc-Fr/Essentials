package net.mcfr.dao;

import net.mcfr.warp.Warp;

public class DaoFactory {
  /**
   * @return un DAO pour gérer les {@code Warp}s.
   */
  public static Dao<Warp> getWarpDao() {
    return new WarpDao();
  }
  
  /**
   * @return un DAO pour gérer le {@code HarvestService}.
   */
  public static HarvestDao getHarvestDao() {
    return new HarvestDao();
  }
}
