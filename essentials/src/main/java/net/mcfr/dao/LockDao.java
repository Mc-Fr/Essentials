package net.mcfr.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3i;

import net.mcfr.locks.Lock;
import net.mcfr.utils.McFrConnection;

public class LockDao implements Dao<Lock> {

  @Override
  public List<Lock> getAll() {
    List<Lock> locks = new ArrayList<>();
    
    try (Connection connection = McFrConnection.getConnection()) {
      ResultSet rs = connection.createStatement().executeQuery("select x, y, z, world, code, locked from srv_locks");
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

        Vector3i position = new Vector3i(rs.getInt("x"), rs.getInt("y"), rs.getInt("z"));
        int code = rs.getInt("code");
        boolean locked = rs.getInt("locked") == 0 ? true : false;

        Lock lock = new Lock(position, optWorld.get(), code, locked);
        locks.add(lock);
      }
      rs.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return locks;
  }

  @Override
  public boolean create(Lock o) {
    try (Connection connection = McFrConnection.getConnection()) {
      CallableStatement cs = connection.prepareCall("{ call addLock(?, ?, ?, ?, ?, ?) }");
      Vector3i pos = o.getPosition();
      
      cs.setInt(1, pos.getX());
      cs.setInt(2, pos.getY());
      cs.setInt(3, pos.getZ());
      cs.setString(4, o.getWorld().getName());
      cs.setInt(5, o.getCode());
      cs.setInt(6, o.isLocked() ? 0 : 1);
      
      cs.execute();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public boolean delete(Lock o) {
    try (Connection connection = McFrConnection.getConnection()) {
      CallableStatement cs = connection.prepareCall("{ call removeLock(?, ?, ?, ?) }");
      Vector3i pos = o.getPosition();
      
      cs.setInt(1, pos.getX());
      cs.setInt(2, pos.getY());
      cs.setInt(3, pos.getZ());
      cs.setString(4, o.getWorld().getName());
      
      cs.execute();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }
  
  public boolean changeState(Lock o, boolean locked) {
    try (Connection connection = McFrConnection.getConnection()) {
      CallableStatement cs = connection.prepareCall("{ call changeLockState(?, ?, ?, ?, ?) }");
      Vector3i pos = o.getPosition();
      
      cs.setInt(1, pos.getX());
      cs.setInt(2, pos.getY());
      cs.setInt(3, pos.getZ());
      cs.setString(4, o.getWorld().getName());
      cs.setInt(5, locked ? 0 : 1);
      
      cs.execute();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  @Deprecated
  public boolean update(Lock o) {
    return false;
  }

}
