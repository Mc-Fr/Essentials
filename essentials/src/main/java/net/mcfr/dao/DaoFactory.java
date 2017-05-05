package net.mcfr.dao;

import net.mcfr.warp.Warp;

public class DaoFactory {
  public static Dao<Warp> getWarpDao() {
    return new WarpDao();
  }
}
