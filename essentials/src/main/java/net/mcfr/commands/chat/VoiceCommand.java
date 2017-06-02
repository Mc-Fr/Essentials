package net.mcfr.commands.chat;

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
import net.mcfr.chat.ChatType;
import net.mcfr.commands.AbstractCommand;
import net.mcfr.utils.McFrPlayer;

public class VoiceCommand extends AbstractCommand {

  public VoiceCommand(Essentials plugin) {
    super(plugin);
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    if (src instanceof Player) {
      Player p = (Player) src;

      ChatType chatType = ChatType.MEDIUM;

      if (args.hasAny("chat"))
        chatType = args.<ChatType>getOne("chat").get();

      McFrPlayer.getMcFrPlayer(p).setDefaultChat(chatType);
      p.sendMessage(Text.of(TextColors.YELLOW, "Votre canal de chat par défaut a été réglé sur : " + chatType.getCharsRequired() + " - " + chatType.name() + "."));

    } else {
      src.sendMessage(ONLY_PLAYERS_COMMAND);
    }

    return CommandResult.success();
  }

  @Override
  public CommandSpec getCommandSpec() {
    //#f:0
    return CommandSpec.builder()
            .description(Text.of("Change votre canal de chat par défaut (uniquement la parole RP)."))
            .permission("essentials.command.voice")
            .executor(this)
            .arguments(GenericArguments.optional(GenericArguments.choices(Text.of("chat"), ChatType.voiceStrings)))
            .build();
    //#f:1
  }

  @Override
  public String[] getAliases() {
    return new String[] { "voice" };
  }
}
