package net.mcfr.expedition;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import net.mcfr.utils.McFrConnection;

public class AuthorizedArea {
  private String name;
  private Location<World> location;
  private int radius;

  public AuthorizedArea(String name, Location<World> loc, int r) {
    this.name = name;
    this.location = loc;
    this.radius = r;
  }

  public double distance(Location<World> loc) {
    return Math.hypot(loc.getX() - this.location.getX(), loc.getZ() - this.location.getZ());
  }

  public String getName() {
    return this.name;
  }

  public int getRadius() {
    return this.radius;
  }

  public World getExtent() {
    return this.location.getExtent();
  }

  public void registerInDatabase() {
    try (Connection connection = McFrConnection.getConnection()) {
      PreparedStatement registerArea = connection.prepareStatement("INSERT INTO srv_safeareas(name, x, y, z, radius, world) VALUES (?,?,?,?,?,?)");

      registerArea.setString(1, this.name);
      registerArea.setInt(2, this.location.getBlockX());
      registerArea.setInt(3, this.location.getBlockY());
      registerArea.setInt(4, this.location.getBlockZ());
      registerArea.setInt(5, this.radius);
      registerArea.setString(6, this.location.getExtent().getName());

      registerArea.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void removeFromDatabase() {
    try (Connection connection = McFrConnection.getConnection()) {
      PreparedStatement removeArea = connection.prepareStatement("DELETE FROM src_safeareas WHERE name = ?");

      removeArea.setString(1, this.name);

      removeArea.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public String toString() {
    return this.name + " (" + this.location.getExtent().getName() + " ; " + this.location.getBlockX() + ", " + this.location.getBlockY() + ", "
        + this.location.getBlockZ() + ", rayon : " + this.radius + ")";
  }
}
