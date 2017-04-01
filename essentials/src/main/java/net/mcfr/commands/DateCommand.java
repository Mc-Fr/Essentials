package net.mcfr.commands;

import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import net.mcfr.Essentials;
import net.mcfr.commands.utils.AbstractCommand;
import net.mcfr.time.McFrDate;
import net.mcfr.time.TimeService;

public class DateCommand extends AbstractCommand {

  public DateCommand(Essentials plugin) {
    super(plugin);
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    Optional<TimeService> optTimeService = Sponge.getServiceManager().provide(TimeService.class);
    if (optTimeService.isPresent()) {
      McFrDate date = optTimeService.get().getDate();
      src.sendMessage(Text.of(TextColors.BLUE, "* " + date + " *"));
    } else {
      src.sendMessage(Text.of(TextColors.RED, "Le système de gestion du temps n'a pas été correctement chargé."));
    }
    
    return CommandResult.success();
  }

  @Override
  public CommandSpec getCommandSpec() {
    //#f:0
    return CommandSpec.builder()
            .description(Text.of("Affiche la date roleplay courante."))
            .permission("essentials.command.date")
            .executor(this)
            .build();
    //#f:1
  }

  @Override
  public String[] getAliases() {
    return new String[] { "date" };
  }
}
