package net.mcfr.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import net.mcfr.Essentials;
import net.mcfr.commands.utils.AbstractCommand;

public class FightCommand extends AbstractCommand {

  public FightCommand(Essentials plugin) {
    super(plugin);
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public CommandSpec getCommandSpec() {
  //#f:0
    return CommandSpec.builder()
            .description(Text.of("Commande du système de combat tour par tour."))
            .permission("essentials.command.fight")
            .executor(this)
            .arguments(GenericArguments.allOf(GenericArguments.player(Text.of("joueur"))))
            .build();
    //#f:1
  }

  @Override
  public String[] getAliases() {
    return new String[] { "fight", "f" };
  }
}
