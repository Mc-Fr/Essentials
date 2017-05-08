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
import net.mcfr.utils.McFrPlayer;

public class RangeCommand extends AbstractCommand {

  public RangeCommand(Essentials plugin) {
    super(plugin);
  }
  
  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    if (src instanceof Player) {
      Player p = (Player) src;
      
      if (args.hasAny("portée")) {
        int range = args.<Integer>getOne("portée").get();
        if (range > 20 || range < 1) {
          src.sendMessage(Text.of(TextColors.YELLOW, "La portée maximale d'écoute est de 20."));
        } else if (range == 20) {
          cancelReducedRange(p);
        } else {
          McFrPlayer.getMcFrPlayer(p).setListeningRange(range);
          src.sendMessage(Text.of(TextColors.YELLOW, "Votre portée d'écoute maximale est fixée à " + range + ". Les cris, hurlements et actions ne sont pas affectés."));
        }
      } else {
        cancelReducedRange(p);
      }
    }
    else {
      src.sendMessage(ONLY_PLAYERS_COMMAND);
    }
    
    return CommandResult.success();
  }
  
  private void cancelReducedRange(Player p) {
    McFrPlayer.getMcFrPlayer(p).setListeningRange(20);
    p.sendMessage(Text.of(TextColors.YELLOW, "Votre portée d'écoute est revenue à la normale (20)."));
  }
  
  @Override
  public CommandSpec getCommandSpec() {
    //#f:0
    return CommandSpec.builder()
            .description(Text.of("Change votre portée d'écoute maximale (max 20). Les cris et hurlements ne sont pas affectés."))
            .permission("essentials.command.range")
            .executor(this)
            .arguments(GenericArguments.optional(GenericArguments.integer(Text.of("portée"))))
            .build();
    //#f:1
  }
  
  @Override
  public String[] getAliases() {
    return new String[] { "range" };
  }
}
