package net.mcfr.commands.game;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import net.mcfr.Essentials;
import net.mcfr.commands.AbstractCommand;

  //TODO : tout reste à faire

public class InvCommand extends AbstractCommand {
  
  public InvCommand(Essentials plugin) {
    super(plugin);
  }
  
  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    return CommandResult.success();
  }
  
  @Override
  public CommandSpec getCommandSpec() {
    //#f:0
    return CommandSpec.builder()
            .description(Text.of("Gestionnaire d'inventaire."))
            .permission("essentials.command.inv")
            .executor(this)
            .children(getChildrenList(new Copy(getPlugin()), new See(getPlugin()), new Spy(getPlugin())))
            .build();
    //#f:1
  }
  
  @Override
  public String[] getAliases() {
    return new String[] { "inv" };
  }
  
  static class Copy extends AbstractCommand {
    
    public Copy(Essentials plugin) {
      super(plugin);
    }
    
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      return CommandResult.success();
    }
    
    @Override
    public CommandSpec getCommandSpec() {
      //#f:0
      return CommandSpec.builder()
              .description(Text.of("Copie un inventaire d'un joueur vers un joueur."))
              .permission("essentials.command.inv.copy")
              .executor(this)
              .arguments(GenericArguments.onlyOne(GenericArguments.player(Text.of("source"))),
                         GenericArguments.allOf(GenericArguments.player(Text.of("destination"))))
              .build();
      //f#:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "copy", "cp" };
    }

  }

  static class See extends AbstractCommand {

    public See(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      return null;
    }

    @Override
    public CommandSpec getCommandSpec() {
      return null;
    }

    @Override
    public String[] getAliases() {
      return null;
    }

  }

  static class Spy extends AbstractCommand {

    public Spy(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      return null;
    }

    @Override
    public CommandSpec getCommandSpec() {
      return null;
    }

    @Override
    public String[] getAliases() {
      return null;
    }

  }

}
