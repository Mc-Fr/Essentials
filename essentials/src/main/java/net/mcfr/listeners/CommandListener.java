package net.mcfr.listeners;

import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.command.SendCommandEvent;

import net.mcfr.utils.McFrPlayer;

public class CommandListener {

  @Listener
  public void onSentCommand(SendCommandEvent e) {
    if (e.getCommand().contains("tp")) {
      registerPositionForBack(e);
    }
  }

  private void registerPositionForBack(SendCommandEvent e) {
    String[] args = e.getArguments().split(" ");

    Optional<Player> teleportedPlayer;
    if (args.length == 1 || args.length == 3 || args.length == 5) {

      Optional<CommandSource> source = e.getCause().<CommandSource>get(NamedCause.SOURCE, CommandSource.class);
      if (source.isPresent() && source.get() instanceof Player) {
        teleportedPlayer = Optional.of((Player) source.get());
      } else {
        teleportedPlayer = Optional.empty();
      }
    } else {
      teleportedPlayer = Sponge.getServer().getPlayer(args[0]);
    }

    if (teleportedPlayer.isPresent()) {
      McFrPlayer.getMcFrPlayer(teleportedPlayer.get()).setPreviousLocation(teleportedPlayer.get().getLocation());
    }
  }
}
