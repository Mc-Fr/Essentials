package net.mcfr.dao;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import net.mcfr.utils.McFrConnection;
import net.mcfr.warp.Warp;

public class WarpDao extends Dao<Warp> {

  @Override
  public List<Warp> getAll() {
    List<Warp> warps = new ArrayList<>();
    try {
      ResultSet rs = McFrConnection.getConnection().createStatement().executeQuery("select name, world, x, y, z, locked from warp");
      while (rs.next()) {
        Location<World> loc = new Location<>(Sponge.getServer().getWorld(rs.getString("world")).get(), rs.getInt("x"), rs.getInt("y"),
            rs.getInt("z"));
        Warp warp = new Warp(rs.getString("warp"), loc);
        warp.setLocked(rs.getBoolean("locked"));
        warps.add(warp);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return warps;
  }

  @Override
  public boolean create(Warp o) {
    try {
      CallableStatement cs = McFrConnection.getConnection().prepareCall("{ call create_warp(?, ?, ?, ?, ?) }");
      cs.setString(1, o.getName());
      Location<World> loc = o.getLocation();
      cs.setString(2, loc.getExtent().getName());
      cs.setInt(3, loc.getBlockX());
      cs.setInt(4, loc.getBlockY());
      cs.setInt(5, loc.getBlockZ());
      return cs.execute();
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public boolean delete(Warp o) {
    try {
      CallableStatement cs = McFrConnection.getConnection().prepareCall("{ call delete_warp(?) }");
      cs.setString(1, o.getName());
      return cs.execute();
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public boolean update(Warp o) {
    try {
      CallableStatement cs = McFrConnection.getConnection().prepareCall("{ call set_lock_warp(?, ?) }");
      cs.setString(1, o.getName());
      cs.setBoolean(2, o.isLocked());
      return cs.execute();
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

}
