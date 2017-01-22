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
import net.mcfr.roleplay.rollResults.RollResult;
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
        int x = center.get("x").getAsInt();
        int y = center.get("y").getAsInt();
        int z = center.get("z").getAsInt();
        getCenters().add(new CareCenter(name, x, y, z));
      });
    }
  }

  @Listener
  public void onPlayerMove(MoveEntityEvent e, @First Player player) {
    Optional<CareCenter> centerOpt = getNearest(player.getLocation());
    if (McFrPlayer.getMcFrPlayer(player).isInCareCenterEffectArea() && !centerOpt.isPresent()) {
      player.sendMessage(Text.of(TextColors.YELLOW, "Le centre de soin est loin à présent, mieux vaut faire attention."));
      McFrPlayer.getMcFrPlayer(player).setInCareCenterEffectArea(centerOpt.isPresent());
    } else if (!McFrPlayer.getMcFrPlayer(player).isInCareCenterEffectArea() && centerOpt.isPresent()) {
      player.sendMessage(Text.of(TextColors.YELLOW, "Un centre de soin est maintenant assez proche en cas de problème."));
      McFrPlayer.getMcFrPlayer(player).setInCareCenterEffectArea(centerOpt.isPresent());
    }
  }

  @Listener
  public void onPlayerDeath(DestructEntityEvent.Death e) {
    if (e.getTargetEntity() instanceof Player) {
      Player player = (Player) e.getTargetEntity();
      Optional<CareCenter> centerOpt = getNearest(player.getLocation());
      if (centerOpt.isPresent()) {
        RollResult result = Sponge.getServiceManager().provide(RolePlayService.class).get().attributeRoll(player, Attributes.ENDURANCE,
            computeModifier(player));
        switch (result.getResult()) {
        case CRITICAL_SUCCESS:
          player.sendMessage(Text.of(TextColors.GREEN, "Vous n'avez aucune séquelle, tout juste quelques cicatrices."));
          break;
        case SUCCESS:
          player.sendMessage(Text.of(TextColors.DARK_GREEN, "Vous gardez les marques de votre accident, mais d'ici quelques jours, tout ira mieux."));
          break;
        case FAILURE:
          McFrPlayer.getMcFrPlayer(player).incrementNumberOfDeaths();
          player.sendMessage(Text.of(TextColors.DARK_RED,
              "Malgré les soins, vous gardez une séquelle de votre accident, celle-ci sera handicapante pendant les semaines à venir."));
          break;
        case CRITICAL_FAILURE:
          McFrPlayer.getMcFrPlayer(player).incrementNumberOfDeaths();
          player.sendMessage(Text.of(TextColors.RED,
              "Malgré les soins, vous gardez une séquelle importante de votre accident, celle-ci sera handicapante pendant les mois à venir."));
          break;
        }
      } else {
        McFrPlayer.getMcFrPlayer(player).killCharacter("");
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

  private Optional<CareCenter> getNearest(Location<World> loc) {
    return centers.stream().filter(c -> c.distance(loc) < CARE_CENTER_RADIUS_EFFECT_AREA)
        .min((o1, o2) -> Double.compare(o1.distance(loc), o2.distance(loc)));
  }

  private int computeModifier(Player player) {
    int modifier = 0;
    McFrPlayer mcfrPlayer = McFrPlayer.getMcFrPlayer(player);
    modifier += mcfrPlayer.hasTrait("difficile_a_tuer") ? 1 : 0;
    modifier += mcfrPlayer.hasTrait("guerison_rapide_naturelle") ? 2 : 0;
    modifier += mcfrPlayer.hasTrait("recuperation") ? 3 : 0;
    modifier += mcfrPlayer.hasTrait("guerison_rapide_surnaturelle") ? 5 : 0;

    modifier -= mcfrPlayer.hasTrait("facile_a_tuer") ? 1 : 0;
    modifier -= mcfrPlayer.hasTrait("guerison_lente") ? 2 : 0;
    modifier -= mcfrPlayer.hasTrait("hemophile") ? 6 : 0;

    modifier -= mcfrPlayer.getNumberOfDeaths();

    return modifier;
  }
}