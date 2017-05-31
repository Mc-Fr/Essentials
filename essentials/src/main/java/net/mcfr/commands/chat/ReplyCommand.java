package net.mcfr.commands.chat;

import java.util.Optional;

import org.spongepowered.api.Sponge;
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

public class ReplyCommand extends AbstractCommand {

  public ReplyCommand(Essentials plugin) {
    super(plugin);
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    if (src instanceof Player) {
      Player player = (Player) src;

      Optional<Player> lastCorrespondent = McFrPlayer.getMcFrPlayer(player).getLastCorrespondent();
      if (lastCorrespondent.isPresent()) {
        String message = args.<String>getOne("message").get();
        Player recipient = lastCorrespondent.get();

        if (!McFrPlayer.getMcFrPlayer(recipient).wantsMP()) {
          player.sendMessage(Text.of(TextColors.YELLOW, "Votre correspondant ignore les mps."));
          return CommandResult.success();
        }

        Text text = Text.of(TextColors.DARK_GRAY, String.format("[%s -> %s] : %s", player.getName(), recipient.getName(), message));
        player.sendMessage(text);
        recipient.sendMessage(text);

        Sponge.getServer().getOnlinePlayers().parallelStream()
            .filter(p -> McFrPlayer.getMcFrPlayer(p).spiesMp() && !p.equals(player) && !p.equals(recipient)).forEach(p -> p.sendMessage(text));
        McFrPlayer.setLastCorrespondents(player, recipient);
      } else {
        src.sendMessage(Text.of(TextColors.RED, "Vous n'avez aucun correspondant !"));
      }
    } else {
      src.sendMessage(ONLY_PLAYERS_COMMAND);
    }

    return CommandResult.success();
  }

  @Override
  public CommandSpec getCommandSpec() {
    //#f:0
    return CommandSpec.builder()
            .description(Text.of("Répond au dernier correspondant."))
            .permission("essentials.command.reply")
            .executor(this)
            .arguments(GenericArguments.remainingJoinedStrings(Text.of("message")))
            .build();
    //#f:1
  }

  @Override
  public String[] getAliases() {
    return new String[] { "reply", "r" };
  }
}