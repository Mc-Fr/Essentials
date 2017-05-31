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
import net.mcfr.commands.AbstractCommand;
import net.mcfr.utils.McFrPlayer;

public class DescCommand extends AbstractCommand {
  public DescCommand(Essentials plugin) {
    super(plugin);
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    if (src instanceof Player) {
      Player player = (Player) src;
      if (args.hasAny("description")) {
        String description = args.<String>getOne("description").get();
        McFrPlayer.getMcFrPlayer(player).setDescription(description);
        src.sendMessage(Text.of(TextColors.YELLOW, "Votre description a été modifiée : " + description));
      }
    }
    return CommandResult.success();
  }

  @Override
  public CommandSpec getCommandSpec() {
    // #f:0
		return CommandSpec.builder().description(Text.of("Modifie la description du personnage."))
				.permission("essentials.command.desc").executor(this)
				.arguments(GenericArguments.remainingJoinedStrings(Text.of("description")))
				.build();
		// #f:1
  }

  @Override
  public String[] getAliases() {
    return new String[] { "desc" };
  }

}
