package net.mcfr.commands.chat;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import net.mcfr.Essentials;
import net.mcfr.commands.AbstractCommand;

public class TrollCommand extends AbstractCommand {

  public TrollCommand(Essentials plugin) {
    super(plugin);
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    //#f:0
    if (src instanceof Player && ((Player)src).getName().equals("canibalissimo"))
      src.sendMessage(Text.of(TextColors.DARK_RED, "T'es viré. - Signé Daeniya"));
    else
      src.sendMessage(Text.join(
        Text.of(TextColors.YELLOW, "L"),
        Text.of(TextColors.GOLD, "e "),
        Text.of(TextColors.RED, "R"),
        Text.of(TextColors.LIGHT_PURPLE, "P"),
        Text.of(TextColors.BLUE, "Q "),
        Text.of(TextColors.AQUA, "c"),
        Text.of(TextColors.GREEN, "'"),
        Text.of(TextColors.YELLOW, "e"),
        Text.of(TextColors.GOLD, "s"),
        Text.of(TextColors.RED, "t "),
        Text.of(TextColors.LIGHT_PURPLE, "m"),
        Text.of(TextColors.BLUE, "a"),
        Text.of(TextColors.AQUA, "l "),
        Text.of(TextColors.GREEN, "!")));
    //#f:1

    return CommandResult.success();
  }

  @Override
  public CommandSpec getCommandSpec() {
    //#f:0
    return CommandSpec.builder()
            .description(Text.of("Surprise."))
            .permission("essentials.command.troll")
            .executor(this)
            .build();
    //#f:1
  }

  @Override
  public String[] getAliases() {
    return new String[] { "rpq" };
  }
}
