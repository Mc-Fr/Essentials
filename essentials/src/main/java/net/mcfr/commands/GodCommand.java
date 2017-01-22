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

public class GodCommand extends AbstractCommand {

  public GodCommand(Essentials plugin) {
    super(plugin);
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    if (args.hasAny("joueur")) {
      god(args.<Player>getOne("joueur").get());
    } else if (src instanceof Player) {
      god((Player) src);
    } else {
      src.sendMessage(ONLY_PLAYERS_COMMAND);
    }
    return CommandResult.success();
  }

  @Override
  public CommandSpec getCommandSpec() {
    //#f:0
    return CommandSpec.builder()
            .description(Text.of("Octroie/Retire l'invincibilité à un joueur."))
            .permission("essentials.command.god")
            .executor(this)
            .arguments(GenericArguments.optional(GenericArguments.player(Text.of("joueur"))))
            .build();
    //#f:1
  }

  @Override
  public String[] getAliases() {
    return new String[] { "god" };
  }

  private void god(Player player) {
    McFrPlayer mcfrPlayer = McFrPlayer.getMcFrPlayer(player);
    mcfrPlayer.toggleGod();
    player.sendMessage(Text.of(TextColors.YELLOW, "Vous êtes maintenant " + (mcfrPlayer.isGod() ? "in" : "") + "vulnérable aux dégâts."));
  }

}
