package net.mcfr.commands.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.google.common.base.Preconditions;

import net.mcfr.Essentials;

public abstract class AbstractCommand implements CommandExecutor {

  public static final Text ONLY_PLAYERS_COMMAND = Text.of(TextColors.RED, "Seuls les joueurs peuvent utiliser cette commande !");

  private final Essentials plugin;

  public AbstractCommand(Essentials plugin) {
    this.plugin = plugin;
  }

  protected Essentials getPlugin() {
    return this.plugin;
  }

  protected final Collection<Player> getOnlinePlayers() {
    return Sponge.getServer().getOnlinePlayers();
  }

  protected final Logger getLogger() {
    return getPlugin().getLogger();
  }

  protected final Map<List<String>, CommandCallable> getChildrenList(AbstractCommand... commands) {
    Preconditions.checkArgument(commands.length > 0, "Il faut au moins un exécuteur de commande.");
    return Arrays.asList(commands).stream().collect(Collectors.toMap(a -> Arrays.asList(a.getAliases()), AbstractCommand::getCommandSpec));
  }

  public abstract CommandSpec getCommandSpec();

  public abstract String[] getAliases();

}
