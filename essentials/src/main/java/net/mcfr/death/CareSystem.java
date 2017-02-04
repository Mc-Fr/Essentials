package net.mcfr.death;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import net.mcfr.roleplay.Attributes;
import net.mcfr.roleplay.RolePlayService;
import net.mcfr.roleplay.rollResults.AttributeRollResult;
import net.mcfr.utils.McFrPlayer;

public class CareSystem {
  public static final int CARE_CENTER_RADIUS_EFFECT_AREA = 500;
  private static List<CareCenter> centers;

  private static List<CareCenter> getCenters() {
    if (centers == null) {
      centers = new ArrayList<>();
    }
    return centers;
  }

  @Listener
  public void onServerStart(GameStartedServerEvent event) throws IOException {
    File commandsFile = new File("config/essentials-config/centers.json");
    if (commandsFile.exists()) {
      JsonArray centers = new JsonParser().parse(new JsonReader(new FileReader(commandsFile))).getAsJsonObject().get("centers").getAsJsonArray();

      centers.forEach(c -> {
        JsonObject center = c.getAsJsonObject();
        String name = center.get("name").getAsString();
        World world = Sponge.getServer().getWorld(center.get("world").getAsString()).orElse(null);
        int x = center.get("x").getAsInt();
        int y = center.get("y").getAsInt();
        int z = center.get("z").getAsInt();
        if (world != null) {
          getCenters().add(new CareCenter(name, world, x, y, z));
        }
      });
    }
  }

  /*@Listener
  public void onPlayerMove(MoveEntityEvent e, @First Player player) {
    Optional<CareCenter> centerOpt = getNearest(player.getLocation());
    if (McFrPlayer.getMcFrPlayer(player).isInCareCenterEffectArea() && !centerOpt.isPresent()) {
      player.sendMessage(Text.of(TextColors.YELLOW, "Vous n'êtes plus protégé par aucun centre de soin, prenez garde."));
      McFrPlayer.getMcFrPlayer(player).setInCareCenterEffectArea(centerOpt.isPresent());
    } else if (!McFrPlayer.getMcFrPlayer(player).isInCareCenterEffectArea() && centerOpt.isPresent()) {
      player.sendMessage(Text.of(TextColors.YELLOW, "Un centre de soin est maintenant assez proche en cas de problème."));
      McFrPlayer.getMcFrPlayer(player).setInCareCenterEffectArea(centerOpt.isPresent());
    }
  }*/
  
  @Listener
  public void onPlayerMove(MoveEntityEvent e) {
    if (e.getTargetEntity() instanceof Player) {
      Player player = (Player) e.getTargetEntity();
      Optional<CareCenter> centerOpt = getNearest(player.getLocation());
      if (McFrPlayer.getMcFrPlayer(player).isInCareCenterEffectArea() && !centerOpt.isPresent()) {
        player.sendMessage(Text.of(TextColors.YELLOW, "Vous n'êtes plus protégé par aucun centre de soin, prenez garde."));
        McFrPlayer.getMcFrPlayer(player).setInCareCenterEffectArea(centerOpt.isPresent());
      } else if (!McFrPlayer.getMcFrPlayer(player).isInCareCenterEffectArea() && centerOpt.isPresent()) {
        player.sendMessage(Text.of(TextColors.YELLOW, "Un centre de soin est maintenant assez proche en cas de problème."));
        McFrPlayer.getMcFrPlayer(player).setInCareCenterEffectArea(centerOpt.isPresent());
      }
    }
  }

  @Listener
  public void onPlayerDeath(DestructEntityEvent.Death e) {
    if (e.getTargetEntity() instanceof Player) {
      Player player = (Player) e.getTargetEntity();
      McFrPlayer mcFrPlayer = McFrPlayer.getMcFrPlayer(player);
      if (mcFrPlayer.hasCharacter()) {
        Optional<CareCenter> centerOpt = getNearest(player.getLocation());
        if (centerOpt.isPresent()) {
          AttributeRollResult result = Sponge.getServiceManager().provide(RolePlayService.class).get().attributeRoll(player, Attributes.ENDURANCE,
              computeModifier(mcFrPlayer));
          Text deathMessage = Text.of(String.format("%s fait un jet de %s, score de %d" + (result.getModifier() != 0 ? "(%d)" : ""),
              McFrPlayer.getMcFrPlayer(player).getName(), result.getAttributeName(), result.getScore(), result.getMargin()));
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
  }

  @Listener
  public void onPlayerRespawn(RespawnPlayerEvent e, @First Player p) {
    Optional<CareCenter> centerOpt = getNearest(p.getLocation());
    if (centerOpt.isPresent()) {
      CareCenter center = centerOpt.get();
      Transform<World> transform = e.getToTransform();
      Location<World> location = center.getLocation();
      Location<World> newLocation = new Location<>(location.getExtent(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
      e.setToTransform(transform.setLocation(newLocation));
    }
  }

  public Optional<CareCenter> getNearest(Location<World> loc) {
    return centers.stream().filter(c -> c.getLocation().getExtent().equals(loc.getExtent())).filter(c -> c.distance(loc) < CARE_CENTER_RADIUS_EFFECT_AREA)
        .min((o1, o2) -> Double.compare(o1.distance(loc), o2.distance(loc)));
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

  public static class CareCenter {
    private String name;
    private Location<World> location;

    public CareCenter(String name, World world, int x, int y, int z) {
      this.name = name;
      this.location = new Location<>(world, x, y, z);
    }

    public double distance(Location<World> loc) {
      return Math.hypot(loc.getX() - this.location.getX(), loc.getZ() - this.location.getZ());
    }

    public String getName() {
      return this.name;
    }

    public Location<World> getLocation() {
      return this.location;
    }

    @Override
    public String toString() {
      return this.name + " (" + this.location.getBlockX() + ", " + this.location.getBlockY() + ", " + this.location.getBlockZ() + ")";
    }
  }
}