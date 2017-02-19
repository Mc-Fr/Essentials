package net.mcfr.expedition;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSources;
import org.spongepowered.api.event.entity.MoveEntityEvent;
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

import net.mcfr.utils.McFrPlayer;

public class ExpeditionSystem {
  private static final int RADIUS_DELTA = 10;
  private static List<AuthorizedArea> areas = new ArrayList<>();
  private static States current;

  @Listener
  public void onServerStart(GameStartedServerEvent event) throws IOException {
    File commandsFile = new File("config/essentials-config/areas.json");
    if (commandsFile.exists()) {
      JsonArray areasJson = new JsonParser().parse(new JsonReader(new FileReader(commandsFile))).getAsJsonObject().get("areas").getAsJsonArray();

      areasJson.forEach(a -> {
        JsonObject area = a.getAsJsonObject();
        String name = area.get("name").getAsString();
        int x = area.get("x").getAsInt();
        int y = area.get("y").getAsInt();
        int z = area.get("z").getAsInt();
        int r = area.get("radius").getAsInt();
        areas.add(new AuthorizedArea(name, x, y, z, r));
      });
    }
  }

  @Listener
  public void onPlayerMove(MoveEntityEvent e, @First Player p) {
    if (p != null) {
      McFrPlayer player = McFrPlayer.getMcFrPlayer(p);
      States prevState = player.getExpeditionState();
      States nextState = getNextState(p.getLocation());

      if (!player.isAuthorizedToLeaveArea() && !p.hasPermission("essentials.leavearea")
          && p.getWorld().equals(Sponge.getServer().getWorld("world").get())) {
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

          p.sendMessage(nextState.dangerMessage);
        } else if (nextState.ordinal() < prevState.ordinal()) {
          p.sendMessage(nextState.safeMessage);
        }
      }

      player.setExpeditionState(nextState);
    }
  }

  public States getNextState(Location<World> loc) {
    current = States.KILL;

    areas.forEach(a -> {
      double distance = a.distance(loc);
      int radius = a.radius;

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

    return current;
  }

  private States getWeakest(States current, States next) {
    return current.ordinal() > next.ordinal() ? next : current;
  }

  public static class AuthorizedArea {
    private String name;
    private Location<World> location;
    private int radius;

    public AuthorizedArea(String name, int x, int y, int z, int r) {
      this.name = name;
      this.location = new Location<>(Sponge.getServer().getWorld("world").get(), x, y, z);
      this.radius = r;
    }

    public double distance(Location<World> loc) {
      return Math.hypot(loc.getX() - this.location.getX(), loc.getZ() - this.location.getZ());
    }

    @Override
    public String toString() {
      return this.name + " (" + this.location.getBlockX() + ", " + this.location.getBlockY() + ", " + this.location.getBlockZ() + ", rayon : "
          + this.radius + ")";
    }
  }

  public enum States {
    // #f:0
    IN_AREA( 
        Text.of(TextColors.YELLOW, "Vous vous sentez enfin débarassé de ce qui vous suivait.")),
    ADVERT( 
        Text.of(TextColors.YELLOW, "Vous n'êtes toujours pas en sécurité..."),
        Text.of(TextColors.YELLOW, "Vous sentez une présence qui vous épie... Mieux vaut faire demi-tour.")),
    HURT1(
        Text.of(TextColors.YELLOW, "La présence est toujours là, elle vous observe partir."),
        Text.of(TextColors.DARK_RED, "Une flèchette vient se planter dans votre bras ! Retournez vite sur vos pas !")),
    HURT2(
        Text.of(TextColors.YELLOW, "Vous entendez des bruits de pas derrière vous pendant que vous faites demi-tour."),
        Text.of(TextColors.DARK_RED, "Une autre fléchette touche votre jambe ! Fuyez !")),
    HURT3(
        Text.of(TextColors.YELLOW, ""),
        Text.of(TextColors.DARK_RED, "Encore une ! Dans le torse cette fois-ci. Vous allez y passer !")),
    KILL;
    // #f:1

    private final Text safeMessage;
    private final Text dangerMessage;

    private States(Text safeMessage, Text dangerMessage) {
      this.safeMessage = safeMessage;
      this.dangerMessage = dangerMessage;
    }

    private States(Text safeMessage) {
      this(safeMessage, Text.of(""));
    }

    private States() {
      this(Text.of(""));
    }
  }
}
