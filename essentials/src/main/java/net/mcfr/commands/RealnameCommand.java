package net.mcfr.commands;

import java.util.List;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import net.mcfr.Essentials;
import net.mcfr.commands.utils.AbstractCommand;
import net.mcfr.utils.McFrPlayer;

public class RealnameCommand extends AbstractCommand {

  public RealnameCommand(Essentials plugin) {
    super(plugin);
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    String nom = args.<String>getOne("nom").get();
    List<McFrPlayer> l = McFrPlayer.getMcFrPlayers();
    if (l.stream().filter(p -> p.getName().equals(nom)).count() == 0) {
      src.sendMessage(Text.of(TextColors.YELLOW, "Aucun joueur ne correspond à ce nom."));
    } else {
      src.sendMessage(Text.of(TextColors.YELLOW, "Liste des \"" + nom + "\" :"));
      l.stream().filter(p -> p.getName().equals(nom)).forEach(p -> src.sendMessage(Text.of(TextColors.YELLOW, " - " + p.getPlayer().getName())));
    }
    return CommandResult.success();
  }

  @Override
  public CommandSpec getCommandSpec() {
    //#f:0
    return CommandSpec.builder()
            .description(Text.of("Affiche du pseudo d'un joueur"))
            .permission("essentials.command.realname")
            .executor(this)
            .arguments(GenericArguments.remainingJoinedStrings(Text.of("nom")))
            .build();
    //#f:1
  }

  @Override
  public String[] getAliases() {
    return new String[] { "realname", "rn" };
  }

}
