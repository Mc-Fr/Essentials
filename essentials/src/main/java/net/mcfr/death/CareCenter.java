package net.mcfr.death;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import net.mcfr.expedition.AuthorizedArea;
import net.mcfr.utils.McFrConnection;

public class CareCenter extends AuthorizedArea {
  private String faction;

  public CareCenter(String name, Location<World> loc, int r, String faction) {
    super(name, loc, r);
    this.faction = faction;
  }
  
  public String getFaction() {
    return this.faction;
  }

  @Override
  public void registerInDatabase() {
    try (Connection connection = McFrConnection.getConnection()) {
      PreparedStatement registerCenter = connection.prepareStatement("INSERT INTO srv_carecenters(name, x, y, z, radius, world, faction) VALUES (?,?,?,?,?,?,?)");

      registerCenter.setString(1, this.getName());
      registerCenter.setInt(2, this.getLocation().getBlockX());
      registerCenter.setInt(3, this.getLocation().getBlockY());
      registerCenter.setInt(4, this.getLocation().getBlockZ());
      registerCenter.setInt(5, this.getRadius());
      registerCenter.setString(6, this.getLocation().getExtent().getName());
      registerCenter.setString(7, this.faction);

      registerCenter.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void removeFromDatabase() {
    try (Connection connection = McFrConnection.getConnection()) {
      PreparedStatement removeCenter = connection.prepareStatement("DELETE FROM srv_carecenters WHERE name = ?");

      removeCenter.setString(1, this.getName());

      removeCenter.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public String toString() {
    return this.getName() + " (" + this.getLocation().getExtent().getName() + " ; " + this.getLocation().getBlockX() + ", " + this.getLocation().getBlockY() + ", "
        + this.getLocation().getBlockZ() + ", rayon : " + this.getRadius() + ", faction : " + this.faction + ")";
  }
}
