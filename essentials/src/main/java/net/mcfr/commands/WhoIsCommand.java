package net.mcfr.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import net.mcfr.Essentials;
import net.mcfr.commands.utils.AbstractCommand;
import net.mcfr.utils.McFrPlayer;

public class WhoIsCommand extends AbstractCommand {

  public WhoIsCommand(Essentials plugin) {
    super(plugin);
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    Player player = null;
    if (args.hasAny("joueur")) {
      player = args.<Player>getOne("joueur").get();
    } else {
      if (src instanceof Player) {
        player = (Player) src;
      } else {
        src.sendMessage(ONLY_PLAYERS_COMMAND);
        return CommandResult.success();
      }
    }

    String name = McFrPlayer.getMcFrPlayer(player).getName();
    src.sendMessage(Text.of(TextColors.DARK_GREEN, player.getName() + " -> " + name));
    src.sendMessage(Text.of(TextColors.DARK_GREEN, " - Desc : " + McFrPlayer.getMcFrPlayer(player).getDescription()));

    return CommandResult.success();
  }

  @Override
  public CommandSpec getCommandSpec() {
    //#f:0
    return CommandSpec.builder()
            .description(Text.of("Affiche les informations d'un joueur"))
            .permission("essentials.command.whois")
            .executor(this)
            .arguments(GenericArguments.optional(GenericArguments.player(Text.of("joueur"))))
            .build();
    //#f:1
  }

  @Override
  public String[] getAliases() {
    return new String[] { "whois" };
  }

}
