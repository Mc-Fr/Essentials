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

public class TpToCommand extends AbstractCommand {

  public TpToCommand(Essentials plugin) {
    super(plugin);
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    if (src instanceof Player) {
      Player p = args.<Player>getOne("joueur").get();
      Player target = args.<Player>getOne("cible").get();
      p.setLocation(target.getLocation());
      src.sendMessage(Text.of(TextColors.YELLOW, String.format("%s avez été téléporté sur %s", p.getName(), target.getName())));
    } else {
      src.sendMessage(ONLY_PLAYERS_COMMAND);
    }
    return CommandResult.success();
  }

  @Override
  public CommandSpec getCommandSpec() {
    //#f:0
    return CommandSpec.builder()
            .description(Text.of("Téléporte un joueur sur un autre joueur."))
            .permission("essentials.command.tppos")
            .arguments(GenericArguments.player(Text.of("joueur")), GenericArguments.player(Text.of("cible")))
            .executor(this)
            .build();
    //#f:1
  }

  @Override
  public String[] getAliases() {
    return new String[] { "tpto" };
  }

}
