package net.mcfr.dao;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import net.mcfr.utils.McFrConnection;
import net.mcfr.warp.Warp;

public class WarpDao implements Dao<Warp> {

  @Override
  public List<Warp> getAll() {
    List<Warp> warps = new ArrayList<>();
    try {
      ResultSet rs = McFrConnection.getConnection().createStatement().executeQuery("select name, world, x, y, z, locked from warp");
      List<String> unknownWorlds = new ArrayList<>();
      while (rs.next()) {
        String worldName = rs.getString("world");
        if (unknownWorlds.contains(worldName))
          continue;
        Optional<World> optWorld = Sponge.getServer().getWorld(worldName);
        if (!optWorld.isPresent()) {
          System.out.println("Le monde " + rs.getString("world") + " n'existe pas.");
          unknownWorlds.add(worldName);
          Sponge.getServer().getWorlds().forEach(w -> System.out.println(w.getName()));
          continue;
        }
        Location<World> loc = new Location<>(optWorld.get(), rs.getInt("x"), rs.getInt("y"), rs.getInt("z"));
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
      cs.execute();
      return true;
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
      cs.execute();
      return true;
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
      cs.execute();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }
}