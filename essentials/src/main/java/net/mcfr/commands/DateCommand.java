package net.mcfr.commands;

import java.util.Optional;

import org.spongepowered.api.Sponge;
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
import net.mcfr.time.McFrDate;
import net.mcfr.time.TimeService;
import net.mcfr.time.TimeValue;

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
            .children(getChildrenList(new Freeze(getPlugin()),
                new Resume(getPlugin())))
            .build();
    //#f:1
  }

  @Override
  public String[] getAliases() {
    return new String[] { "date" };
  }
  
  static class Freeze extends AbstractCommand {

    public Freeze(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      Optional<TimeService> optTimeService = Sponge.getServiceManager().provide(TimeService.class);
      if (optTimeService.isPresent()) {
        TimeService timeService = optTimeService.get();
        if (args.hasAny("temps")) {
          timeService.freezeTime(new TimeValue(args.<Integer>getOne("temps").get()));
        } else {
          timeService.freezeTime();
        }
        
        if (timeService.isTimeFreezed()) {
          src.sendMessage(Text.of(TextColors.YELLOW, "Le temps a été figé au : " + timeService.getDate()));
        } else {
          src.sendMessage(Text.of(TextColors.RED, "Erreur : le temps n'a pas été figé."));
        }
      } else {
        src.sendMessage(Text.of(TextColors.RED, "Le système de gestion du temps n'a pas été correctement chargé."));
      }
      
      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder()
          .description(Text.of("Fige le temps à l'heure actuelle ou à l'heure spécifiée."))
          .permission("essentials.command.date.freeze")
          .arguments(GenericArguments.optional(GenericArguments.integer(Text.of("temps"))))
          .executor(this)
          .build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "freeze" };
    }
  }
  
  static class Resume extends AbstractCommand {

    public Resume(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      Optional<TimeService> optTimeService = Sponge.getServiceManager().provide(TimeService.class);
      if (optTimeService.isPresent()) {
        TimeService timeService = optTimeService.get();
        timeService.resumeTime();
        src.sendMessage(Text.of(TextColors.YELLOW, "Le temps a repris son cours au : " + timeService.getDate()));
      } else {
        src.sendMessage(Text.of(TextColors.RED, "Le système de gestion du temps n'a pas été correctement chargé."));
      }
      
      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder()
          .description(Text.of("Fait reprendre son cours au temps, s'il était figé."))
          .permission("essentials.command.date.resume")
          .executor(this)
          .build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "resume" };
    }
  }
}
