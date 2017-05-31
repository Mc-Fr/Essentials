package net.mcfr.commands.roleplay;

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

public class NameCommand extends AbstractCommand {
  public NameCommand(Essentials plugin) {
    super(plugin);
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    if (args.hasAny("joueur")) {
      Player player = args.<Player>getOne("joueur").get();
      if (args.hasAny("nom")) {
        McFrPlayer.getMcFrPlayer(player).setName(args.<String>getOne("nom").get());
        src.sendMessage(
            Text.of(TextColors.YELLOW, "Le nom de " + player.getName() + " est désormais " + McFrPlayer.getMcFrPlayer(player).getName() + "."));
      } else {
        src.sendMessage(Text.of(TextColors.YELLOW, "Le nom de " + player.getName() + " est " + McFrPlayer.getMcFrPlayer(player).getName() + "."));
      }
    }
    return CommandResult.success();
  }

  @Override
  public CommandSpec getCommandSpec() {
    // #f:0
		return CommandSpec.builder().description(Text.of("Modifie ou affiche le nom du personnage du joueur sélectionné."))
				.permission("essentials.command.name").executor(this)
				.arguments(GenericArguments.player(Text.of("joueur")), GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("nom"))))
				.build();
		// #f:1
  }

  @Override
  public String[] getAliases() {
    return new String[] { "name" };
  }

}
