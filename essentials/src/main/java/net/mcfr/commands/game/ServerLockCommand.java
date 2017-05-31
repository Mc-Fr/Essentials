package net.mcfr.commands.game;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import net.mcfr.Essentials;
import net.mcfr.commands.AbstractCommand;

public class ServerLockCommand extends AbstractCommand {

  public ServerLockCommand(Essentials plugin) {
    super(plugin);
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    getPlugin().toggleServerLock();
    src.sendMessage(Text.of(TextColors.YELLOW, String.format("Le serveur est %sverrouillé.", getPlugin().isServerLocked() ? "" : "dé")));
    return CommandResult.success();
  }

  @Override
  public CommandSpec getCommandSpec() {
    //#f:0
    return CommandSpec.builder()
            .description(Text.of("Bloque/Débloque l'accès au serveur pour les joueurs."))
            .permission("essentials.command.serverlock")
            .executor(this)
            .build();
    //#f:1
  }

  @Override
  public String[] getAliases() {
    return new String[] { "serverlock" };
  }

}
