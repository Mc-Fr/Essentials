package net.mcfr.death;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import net.mcfr.roleplay.Attributes;
import net.mcfr.roleplay.RolePlayService;
import net.mcfr.roleplay.rollResults.AttributeRollResult;
import net.mcfr.utils.McFrConnection;
import net.mcfr.utils.McFrPlayer;

public class CareImp implements CareService {
  private List<CareCenter> careCenters = new ArrayList<>();
  private List<String> factions = new ArrayList<>();

  public CareImp() {
    loadFromDatabase();
  }

  @Override
  public List<CareCenter> getCenters() {
    return this.careCenters;
  }
  
  @Override
  public Map<String, String> getFactionsMap() {
    Map<String, String> result = new HashMap<>();
    for (String f : factions) {
      result.put(f, f);
    }
    return result;
  }

  @Override
  public boolean addCenter(String name, Location<World> location, int radius, String faction) {
    if (!getCenterByName(name).isPresent() && factions.contains(faction)) {
      CareCenter newCenter = new CareCenter(name, location, radius, faction);
      careCenters.add(newCenter);
      newCenter.registerInDatabase();
      return true;
    }
    return false;
  }

  @Override
  public void removeCenter(CareCenter center) {
    careCenters.remove(center);
    center.removeFromDatabase();
  }

  @Override
  public Optional<CareCenter> getCenterByName(String name) {
    return careCenters.stream().filter(c -> c.getName().equals(name)).findFirst();
  }

  @Override
  public void loadFromDatabase() {
    try (Connection connection = McFrConnection.getConnection()) {
      ResultSet centersData = connection.prepareStatement("SELECT name,x,y,z,radius,world,faction FROM srv_carecenters").executeQuery();
      ResultSet factionsData = connection.prepareStatement("SELECT name FROM srv_factions").executeQuery();

      while (factionsData.next()) {
        factions.add(factionsData.getString(1));
      }

      while (centersData.next()) {
        Optional<World> optWorld = Sponge.getServer().getWorld(centersData.getString(6));
        if (optWorld.isPresent()) {
          careCenters.add(new CareCenter(centersData.getString(1),
              new Location<World>(optWorld.get(), centersData.getInt(2), centersData.getInt(3), centersData.getInt(4)), centersData.getInt(5),
              centersData.getString(7)));
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void actualizePlayerState(Player player) {
    Location<World> location = player.getLocation();

    boolean wasInCenterArea = McFrPlayer.getMcFrPlayer(player).isInCareCenterEffectArea();
    boolean isInCenterArea = false;
    boolean careCentersInWorld = false;

    for (CareCenter careCenter : careCenters) {
      if (careCenter.getLocation().getExtent().equals(location.getExtent())) {
        careCentersInWorld = true;
        if (careCenter.distance(location) < careCenter.getRadius()) {
          isInCenterArea = true;
          break;
        }
      }
    }

    if (!careCentersInWorld) {
      isInCenterArea = true;
    }

    if (wasInCenterArea && !isInCenterArea) {
      player.sendMessage(Text.of(TextColors.YELLOW, "Le centre de soin est loin à présent, mieux vaut faire attention."));
      McFrPlayer.getMcFrPlayer(player).setInCareCenterEffectArea(false);
    } else if (!wasInCenterArea && isInCenterArea) {
      player.sendMessage(Text.of(TextColors.YELLOW, "Un centre de soin est maintenant assez proche en cas de problème."));
      McFrPlayer.getMcFrPlayer(player).setInCareCenterEffectArea(true);
    }
  }

  @Override
  public void computeDeath(Player player) {
    McFrPlayer mcFrPlayer = McFrPlayer.getMcFrPlayer(player);
    if (mcFrPlayer.hasCharacter()) {
      Optional<CareCenter> centerOpt = getBest(player.getLocation(), true);
      if (centerOpt.isPresent()) {
        AttributeRollResult result = Sponge.getServiceManager().provide(RolePlayService.class).get().attributeRoll(player, Attributes.ENDURANCE,
            computeModifier(mcFrPlayer));
        Text deathMessage = Text.of(TextColors.YELLOW, String.format("%s fait un jet de %s, score de %d" + (result.getModifier() != 0 ? "(%d)" : ""),
            McFrPlayer.getMcFrPlayer(player).getName(), result.getAttribute().getName(), result.getScore(), result.getMargin()));
        switch (result.getResult()) {
        case CRITICAL_SUCCESS:
          deathMessage.concat(Text.of(TextColors.GREEN, "Vous n'avez aucune séquelle, tout juste quelques cicatrices."));
          break;
        case SUCCESS:
          deathMessage
              .concat(Text.of(TextColors.DARK_GREEN, "Vous gardez les marques de votre accident, mais d'ici quelques jours, tout ira mieux."));
          break;
        case FAILURE:
          mcFrPlayer.incrementNumberOfDeaths();
          deathMessage.concat(Text.of(TextColors.DARK_RED,
              "Malgré les soins, vous gardez une séquelle de votre accident, celle-ci sera handicapante pendant les semaines à venir."));
          break;
        case CRITICAL_FAILURE:
          mcFrPlayer.incrementNumberOfDeaths();
          deathMessage.concat(Text.of(TextColors.RED,
              "Malgré les soins, vous gardez une séquelle importante de votre accident, celle-ci sera handicapante pendant les mois à venir."));
          break;
        }
        player.sendMessage(deathMessage);
      } else {
        McFrPlayer.getMcFrPlayer(player).killCharacter("Trop éloigné d'un centre de soin, vous mourrez sur place.");
      }
    }
  }
  
  @Override
  public void respawnPlayer(RespawnPlayerEvent e) {
    Optional<CareCenter> centerOpt = getBest(e.getTargetEntity().getLocation(), false);
    if (centerOpt.isPresent()) {
      CareCenter center = centerOpt.get();
      Transform<World> transform = e.getToTransform();
      Location<World> location = center.getLocation();
      Location<World> newLocation = new Location<>(location.getExtent(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
      e.setToTransform(transform.setLocation(newLocation));
    }
  }
  
  @Override
  public Optional<CareCenter> getBest(Location<World> location, boolean inRange) {
    double currentValue = -1;
    Optional<CareCenter> optCareCenter = Optional.empty();
    
    for (CareCenter c : careCenters) {
      if (c.getLocation().getExtent().equals(location.getExtent())) {
        double value = c.distance(location) / c.getRadius();
        if ((!inRange || value < 1) && value > currentValue) {
          currentValue = value;
          optCareCenter = Optional.of(c);
        }
      }
    }
    
    return optCareCenter;
  }
  
  private int computeModifier(McFrPlayer player) {
    int modifier = 0;
    modifier += player.hasTrait("difficile_a_tuer") ? 1 : 0;
    modifier += player.hasTrait("guerison_rapide_naturelle") ? 2 : 0;
    modifier += player.hasTrait("recuperation") ? 3 : 0;
    modifier += player.hasTrait("guerison_rapide_surnaturelle") ? 5 : 0;

    modifier -= player.hasTrait("facile_a_tuer") ? 1 : 0;
    modifier -= player.hasTrait("guerison_lente") ? 2 : 0;
    modifier -= player.hasTrait("hemophile") ? 6 : 0;

    modifier -= player.getNumberOfDeaths();

    return modifier;
  }
}
