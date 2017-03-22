package net.mcfr.expedition;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSources;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import net.mcfr.utils.McFrConnection;
import net.mcfr.utils.McFrPlayer;

public class ExpeditionImp implements ExpeditionService {
  private final int RADIUS_DELTA = 10;
  private List<AuthorizedArea> areas = new ArrayList<>();
  private States current;

  public ExpeditionImp() {
    loadFromDatabase();
  }

  @Override
  public List<AuthorizedArea> getAreas() {
    return this.areas;
  }
  
  @Override
  public boolean addArea(String name, Location<World> location, int radius) {
    if (!areas.stream().filter(a -> a.getName().equals(name)).findAny().isPresent()) {
      AuthorizedArea newArea = new AuthorizedArea(name, location, radius);
      areas.add(newArea);
      newArea.registerInDatabase();
      return true;
    }
    return false;
  }

  @Override
  public void removeArea(AuthorizedArea area) {
    areas.remove(area);
    area.removeFromDatabase();
  }
  
  @Override
  public Optional<AuthorizedArea> getAreaByName(String name) {
    return areas.stream().filter(a -> a.getName().equals(name)).findFirst();
  }

  @Override
  public void loadFromDatabase() {
    try (Connection connection = McFrConnection.getConnection()) {
      ResultSet areasData = connection.prepareStatement("SELECT name,x,y,z,radius,world FROM srv_safeareas").executeQuery();

      while (areasData.next()) {
        System.out.println(areasData.getString(6));
        Optional<World> optWorld = Sponge.getServer().getWorld(areasData.getString(6));
        if (optWorld.isPresent()) {
          System.out.println(areasData.getString(1));
          areas.add(new AuthorizedArea(areasData.getString(1),
              new Location<World>(optWorld.get(), areasData.getInt(2), areasData.getInt(3), areasData.getInt(4)), areasData.getInt(5)));
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void actualizePlayerState(Player p) {
    McFrPlayer player = McFrPlayer.getMcFrPlayer(p);
    States prevState = player.getExpeditionState();
    States nextState = getNextState(p.getLocation());

    if (!p.hasPermission("essentials.leavearea") && !p.hasPermission("essentials.freefromareas")) {
      if (nextState.ordinal() > prevState.ordinal()) {
        switch (nextState) {
        case ADVERT:
          break;
        case HURT1:
        case HURT2:
        case HURT3:
          p.damage(Math.min(7.0F, p.health().get()), DamageSources.GENERIC);
          break;
        case KILL:
          p.damage(500.0F, DamageSources.GENERIC);
          p.damage(500.0F, DamageSources.GENERIC);
          player.killCharacter("Pris en embuscade par des indigènes, vous êtes abattu sur le champ.");
          break;
        default:
          break;
        }

        p.sendMessage(nextState.getDangerMessage());
      } else if (nextState.ordinal() < prevState.ordinal()) {
        p.sendMessage(nextState.getSafeMessage());
      }
    }

    player.setExpeditionState(nextState);
  }

  @Override
  public States getNextState(Location<World> loc) {
    current = States.TO_COMPUTE;

    areas.stream().filter(a -> a.getExtent().equals(loc.getExtent())).forEach(a -> {
      if (current.equals(States.TO_COMPUTE)) {
        current = States.KILL;
      }

      double distance = a.distance(loc);
      int radius = a.getRadius();

      if (distance < radius) {
        current = getWeakest(current, States.IN_AREA);
      } else if (distance < radius + RADIUS_DELTA) {
        current = getWeakest(current, States.ADVERT);
      } else if (distance < radius + 2 * RADIUS_DELTA) {
        current = getWeakest(current, States.HURT1);
      } else if (distance < radius + 3 * RADIUS_DELTA) {
        current = getWeakest(current, States.HURT2);
      } else if (distance < radius + 4 * RADIUS_DELTA) {
        current = getWeakest(current, States.HURT3);
      }
    });

    return (current.equals(States.TO_COMPUTE) ? States.IN_AREA : current);
  }

  @Override
  public States getWeakest(States current, States next) {
    return current.ordinal() > next.ordinal() ? next : current;
  }
}
