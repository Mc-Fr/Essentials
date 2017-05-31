package net.mcfr.commands.game;

import org.spongepowered.api.Sponge;
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

public class HideCommand extends AbstractCommand {

  public HideCommand(Essentials plugin) {
    super(plugin);
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    src.sendMessage(Text.of(TextColors.YELLOW, "Renseignez l'action que vous souhaitez accomplir : /hide add, /hide remove, /hide list"));
    return CommandResult.success();
  }

  @Override
  public CommandSpec getCommandSpec() {
    //#f:0
    return CommandSpec.builder()
            .description(Text.of("Rend un joueur invisible sur la liste des connectés."))
            .permission("essentials.command.hide")
            .executor(this)
            .children(getChildrenList(new Add(getPlugin()),
                new Remove(getPlugin()),
                new List(getPlugin())))
            .build();
    //#f:1
  }

  @Override
  public String[] getAliases() {
    return new String[] { "hide" };
  }

  static class Add extends AbstractCommand {

    public Add(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      Player p = args.<Player>getOne("joueur").get();
      
      Sponge.getCommandManager().process(Sponge.getServer().getConsole(),
          "pex user " + p.getUniqueId().toString() + " perm essentials.hidden true");
      
      McFrPlayer.getMcFrPlayer(p).hideForAll();
      
      src.sendMessage(Text.of(TextColors.DARK_BLUE, "Le joueur " + p.getName() + " est caché."));
      
      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder()
          .description(Text.of("Rend le joueur invisible sur la liste des connectés."))
          .permission("essentials.command.hide.add")
          .arguments(GenericArguments.player(Text.of("joueur")))
          .executor(this)
          .build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "add" };
    }
  }

  static class Remove extends AbstractCommand {

    public Remove(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      Player p = args.<Player>getOne("joueur").get();
      
      Sponge.getCommandManager().process(Sponge.getServer().getConsole(),
          "pex user " + p.getUniqueId().toString() + " perm essentials.hidden false");
      
      McFrPlayer.getMcFrPlayer(p).unhideForAll();
      
      src.sendMessage(Text.of(TextColors.DARK_BLUE, "Le joueur " + p.getName() + " n'est plus caché."));
      
      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder()
          .description(Text.of("Rend visible un joueur sur la liste des connectés."))
          .permission("essentials.command.hide.remove")
          .arguments(GenericArguments.player(Text.of("joueur")))
          .executor(this)
          .build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "remove" };
    }
  }

  static class List extends AbstractCommand {

    public List(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      String message = "Liste des joueurs cachés :";
      for (Player p : Sponge.getServer().getOnlinePlayers()) {
        if (McFrPlayer.getMcFrPlayer(p).isHidden())
          message += "\n- " + p.getName();
      }
      
      src.sendMessage(Text.of(TextColors.DARK_BLUE, message));
      
      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder()
          .description(Text.of("Donne la liste des joueurs connectés étant cachés de la liste des connectés."))
          .permission("essentials.command.hide.list")
          .executor(this)
          .build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "list" };
    }
  }
}
