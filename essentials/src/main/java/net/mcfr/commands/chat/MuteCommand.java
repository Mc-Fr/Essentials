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
import net.mcfr.commands.AbstractCommand;
import net.mcfr.utils.McFrPlayer;

public class MuteCommand extends AbstractCommand {

  public MuteCommand(Essentials plugin) {
    super(plugin);
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    Player target = args.<Player>getOne("joueur").get();
    McFrPlayer mcfrPlayer = McFrPlayer.getMcFrPlayer(target);
    mcfrPlayer.toggleMute();
    src.sendMessage(Text.of(TextColors.YELLOW, target.getName() + " " + (mcfrPlayer.isMuted() ? "est" : "n'est plus") + " muet."));
    target.sendMessage(Text.of(TextColors.YELLOW, "Vous " + (mcfrPlayer.isMuted() ? "êtes" : "n'êtes plus") + " muet."));
    return CommandResult.success();
  }

  @Override
  public CommandSpec getCommandSpec() {
    //#f:0
    return CommandSpec.builder()
            .description(Text.of("Octroie/retire le droit de parler à un joueur"))
            .permission("essentials.command.mute")
            .executor(this)
            .arguments(GenericArguments.onlyOne(GenericArguments.player(Text.of("joueur"))))
            .build();
    //#f:1
  }

  @Override
  public String[] getAliases() {
    return new String[] { "mute" };
  }

}
