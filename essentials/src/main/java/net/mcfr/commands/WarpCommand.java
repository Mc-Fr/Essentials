package net.mcfr.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import net.mcfr.Essentials;
import net.mcfr.commands.utils.AbstractCommand;
import net.mcfr.dao.DaoFactory;
import net.mcfr.warp.Warp;

public class WarpCommand extends AbstractCommand {

  public WarpCommand(Essentials plugin) {
    super(plugin);
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args)
      throws CommandException {
    if (src instanceof Player) {
      Player p = (Player) src;
      Warp warp = args.<Warp>getOne("warp").get();
      if (p.hasPermission(warp.getPermission())) {
        p.setLocation(warp.getLocation());
        src.sendMessage(Text.of("Vous avez été téléporté sur le warp."));
        return CommandResult.success();
      }
      src.sendMessage(Text.of("Vous n'avez les permissions nécessaires."));
    } else
      src.sendMessage(ONLY_PLAYERS_COMMAND);
    return CommandResult.empty();
  }

  @Override
  public CommandSpec getCommandSpec() {
    //#f:0
    return CommandSpec.builder()
            .description(Text.of("Commande de manipulation des warps"))
            .permission("essentials.command.warp")
            .executor(this)
            .arguments(GenericArguments.choices(Text.of("warp"), Warp.getWarps()))
            .children(getChildrenList(new Create(getPlugin()), new Delete(getPlugin()), new Lock(getPlugin())))
            .build();
    //#f:1
  }

  @Override
  public String[] getAliases() {
    return new String[] { "warp" };
  }

  static class Create extends AbstractCommand {
    public Create(Essentials plugin) {
      super(plugin);
      // TODO Auto-generated constructor stub
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args)
        throws CommandException {
      if (src instanceof Player) {
        Player p = (Player) src;
        Warp warp = new Warp(args.<String>getOne("name").get(),
            p.getLocation());
        if (DaoFactory.getWarpDao().create(warp)) {
          src.sendMessage(Text.of("Le warp a été créé."));
          return CommandResult.success();
        }
        src.sendMessage(Text.of("L'opération a échoué."));
      } else
        src.sendMessage(ONLY_PLAYERS_COMMAND);
      return CommandResult.empty();
    }

    @Override
    public CommandSpec getCommandSpec() {
      //#f:0
      return CommandSpec.builder()
          .description(Text.of("Permet d'ajouter un warp."))
          .permission("essentials.command.warp.create")
          .executor(this)
          .arguments(GenericArguments.string(Text.of("name")))
          .build();
      //#f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "create" };
    }
  }

  static class Delete extends AbstractCommand {
    public Delete(Essentials plugin) {
      super(plugin);
      // TODO Auto-generated constructor stub
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args)
        throws CommandException {
      Warp warp = args.<Warp>getOne("warp").get();
      if (DaoFactory.getWarpDao().delete(warp)) {
        src.sendMessage(Text.of("Le warp a été supprimé."));
        return CommandResult.success();
      }
      src.sendMessage(Text.of("L'opération a échoué."));
      return CommandResult.empty();
    }

    @Override
    public CommandSpec getCommandSpec() {
      //#f:0
      return CommandSpec.builder()
              .description(Text.of("Permet de supprimer un warp."))
              .permission("essentials.command.warp.delete")
              .executor(this)
              .arguments(GenericArguments.choices(Text.of("warp"), Warp.getWarps()))
              .build();
      //#f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "delete" };
    }
  }

  static class Lock extends AbstractCommand {
    public Lock(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args)
        throws CommandException {
      Warp warp = args.<Warp>getOne("warp").get();
      warp.setLocked(warp.isLocked());
      if (DaoFactory.getWarpDao().update(warp)) {
        src.sendMessage(Text.of(
            "Le warp a été " + (warp.isLocked() ? "" : "dé") + "verrouillé."));
        return CommandResult.success();
      }
      src.sendMessage(Text.of("L'opération a échoué."));
      return CommandResult.empty();
    }

    @Override
    public CommandSpec getCommandSpec() {
      //#f:0
      return CommandSpec.builder()
              .description(Text.of("Permet de (dé)verrouiller un warp"))
              .permission("essentials.command.warp.lock")
              .executor(this)
              .arguments(GenericArguments.choices(Text.of("warp"), Warp.getWarps()))
              .build();
      //#f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "lock" };
    }
  }
}