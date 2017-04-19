package net.mcfr.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import net.mcfr.Essentials;
import net.mcfr.commands.utils.AbstractCommand;
import net.mcfr.utils.McFrPlayer;

public class DateCommand extends AbstractCommand {
  public DateCommand(Essentials plugin) {
    super(plugin);
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    if (src instanceof Player) {
      McFrPlayer player = McFrPlayer.getMcFrPlayer((Player) src);
      
      src.sendMessage(Text.of(TextColors.DARK_PURPLE, "Date ?"));
      String answer;
      player.increaseTrollDate();
      switch(player.getTrollDate()) {
      case 1:
        answer = "I have a boyfriend.";
        break;
      case 2:
        answer = "Can't we just be friends ?";
        break;
      case 3:
        answer = "I'm not ready for this...";
        break;
      case 4:
        answer = "You're my BFF, why do you say that ?";
        break;
      case 5:
        answer = "You're like a brother to me !";
        break;
      default:
        answer = "I'm offended.";
        break;
      }
      src.sendMessage(Text.of(TextColors.LIGHT_PURPLE, answer));
    }
    return CommandResult.success();
  }

  @Override
  public CommandSpec getCommandSpec() {
    // #f:0
    return CommandSpec.builder().description(Text.of(""))
        .permission("essentials.command.date").executor(this)
        .arguments()
        .build();
    // #f:1
  }

  @Override
  public String[] getAliases() {
    return new String[] { "date" };
  }
}