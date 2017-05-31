package net.mcfr.commands.chat;

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

public class MpCommand extends AbstractCommand {

  public MpCommand(Essentials plugin) {
    super(plugin);
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    if (src instanceof Player) {
      Player player = (Player) src;
      Player recipient = args.<Player>getOne("destinataire").get();

      if (player.equals(recipient)) {
        player.sendMessage(Text.of(TextColors.RED, "Vous ne pouvez pas vous écrire à vous même !"));
        return CommandResult.success();
      }

      if (!McFrPlayer.getMcFrPlayer(recipient).wantsMP()) {
        player.sendMessage(Text.of(TextColors.YELLOW, "Votre correspondant ignore les mps."));
        return CommandResult.success();
      }

      Text text = Text.of(TextColors.DARK_GRAY,
          String.format("[%s -> %s] : %s", player.getName(), recipient.getName(), args.<String>getOne("message").get()));
      player.sendMessage(text);
      recipient.sendMessage(text);
      Sponge.getServer().getOnlinePlayers().parallelStream()
          .filter(p -> McFrPlayer.getMcFrPlayer(p).spiesMp() && !p.equals(player) && !p.equals(recipient)).forEach(p -> p.sendMessage(text));
      McFrPlayer.setLastCorrespondents(player, recipient);

    } else {
      src.sendMessage(ONLY_PLAYERS_COMMAND);
    }
    return CommandResult.success();
  }

  @Override
  public CommandSpec getCommandSpec() {
    //#f:0
    return CommandSpec.builder()
            .description(Text.of("Envoie un message au joueur ciblé."))
            .permission("essentials.command.mp")
            .executor(this)
            .arguments(GenericArguments.onlyOne(GenericArguments.player(Text.of("destinataire"))), GenericArguments.remainingJoinedStrings(Text.of("message")))
            .build();
    //#f:1
  }

  @Override
  public String[] getAliases() {
    return new String[] { "mp", "m" };
  }

}
