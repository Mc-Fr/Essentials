package net.mcfr.commands.chat;

import java.util.List;

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

public class RealnameCommand extends AbstractCommand {

  public RealnameCommand(Essentials plugin) {
    super(plugin);
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    if (src instanceof Player) {
      String nom = args.<String>getOne("nom").get();
      boolean seeHidden = ((Player)src).hasPermission("essentials.seeHidden");
      
      List<McFrPlayer> l = McFrPlayer.getMcFrPlayers();
      
      if (l.stream().filter(p -> p.getName().equalsIgnoreCase(nom) && (seeHidden || !p.isHidden())).count() == 0) {
        src.sendMessage(Text.of(TextColors.YELLOW, "Aucun joueur ne correspond à ce nom."));
        
      } else {
        src.sendMessage(Text.of(TextColors.YELLOW, "Liste des \"" + nom + "\" :"));
        
        l.stream().filter(p -> p.getName().equalsIgnoreCase(nom) && (seeHidden || !p.isHidden()))
            .forEach(p -> src.sendMessage(Text.of(TextColors.YELLOW, " - " + p.getPlayer().getName())));
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
