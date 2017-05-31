package net.mcfr.commands.tp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.storage.WorldProperties;

import net.mcfr.Essentials;
import net.mcfr.commands.AbstractCommand;
import net.mcfr.dao.DaoFactory;
import net.mcfr.warp.Warp;
import net.mcfr.warp.WarpImp;
import net.mcfr.warp.WarpService;

public class WarpCommand extends AbstractCommand {

  public WarpCommand(Essentials plugin) {
    super(plugin);
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    Warp warp = args.<Warp>getOne("warp").get();

    if (args.hasAny("joueur")) {
      if (!(src instanceof Player) || ((Player) src).hasPermission(warp.getPermission())) {
        Player pToTeleport = args.<Player>getOne("joueur").get();
        pToTeleport.setLocation(warp.getLocation());
        pToTeleport.sendMessage(Text.of(TextColors.DARK_GREEN, "Vous avez été téléporté sur : " + warp.getName()));
        src.sendMessage(Text.of(TextColors.DARK_GREEN, pToTeleport.getName() + " a été téléporté sur : " + warp.getName()));
      } else {
        src.sendMessage(Text.of(TextColors.DARK_GREEN, "Vous n'avez les permissions nécessaires pour utiliser ce warp."));
      }
    } else {
      if (src instanceof Player) {
        if (((Player) src).hasPermission(warp.getPermission())) {
          ((Player) src).setLocation(warp.getLocation());
          src.sendMessage(Text.of(TextColors.DARK_GREEN, "Vous avez été téléporté sur : " + warp.getName()));
        } else {
          src.sendMessage(Text.of(TextColors.DARK_GREEN, "Vous n'avez les permissions nécessaires pour utiliser ce warp."));
        }
      } else {
        src.sendMessage(ONLY_PLAYERS_COMMAND);
        return CommandResult.empty();
      }
    }
    return CommandResult.success();
  }

  @Override
  public CommandSpec getCommandSpec() {
    //#f:0
    return CommandSpec.builder()
            .description(Text.of("Commande de manipulation des warps"))
            .permission("essentials.command.warp")
            .executor(this)
            .arguments(GenericArguments.choices(Text.of("warp"), WarpImp.getWarps()::keySet, WarpImp.getWarps()::get), GenericArguments.optional(GenericArguments.player(Text.of("joueur"))))
            .children(getChildrenList(new List(getPlugin()),
                new Create(getPlugin()),
                new Delete(getPlugin()),
                new Lock(getPlugin())))
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
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      if (src instanceof Player) {
        Player p = (Player) src;
        Warp warp = new Warp(args.<String>getOne("name").get(), p.getLocation());
        Optional<WarpService> optWarpService = Sponge.getServiceManager().provide(WarpService.class);

        if (optWarpService.isPresent()) {
          if (optWarpService.get().addWarp(warp)) {
            src.sendMessage(Text.of(TextColors.YELLOW, "Le warp a été créé."));
            return CommandResult.success();
          } else {
            src.sendMessage(Text.of(TextColors.RED, "L'opération a échoué."));
          }
        } else {
          src.sendMessage(Text.of(TextColors.RED, "Le service de warps n'a pas été initialisé correctement."));
        }
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
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      Warp warp = args.<Warp>getOne("warp").get();
      Optional<WarpService> optWarpService = Sponge.getServiceManager().provide(WarpService.class);

      if (optWarpService.isPresent()) {
        if (optWarpService.get().deleteWarp(warp)) {
          src.sendMessage(Text.of(TextColors.YELLOW, "Le warp a été supprimé."));
          return CommandResult.success();
        } else {
          src.sendMessage(Text.of(TextColors.RED, "L'opération a échoué."));
        }
      } else {
        src.sendMessage(Text.of(TextColors.RED, "Le service de warps n'a pas été initialisé correctement."));
      }

      return CommandResult.empty();
    }

    @Override
    public CommandSpec getCommandSpec() {
      //#f:0
      return CommandSpec.builder()
              .description(Text.of("Permet de supprimer un warp."))
              .permission("essentials.command.warp.delete")
              .executor(this)
              .arguments(GenericArguments.choices(Text.of("warp"), WarpImp.getWarps()::keySet, WarpImp.getWarps()::get))
              .build();
      //#f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "delete" };
    }
  }

  static class List extends AbstractCommand {

    public List(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      Optional<WarpService> optWarpService = Sponge.getServiceManager().provide(WarpService.class);

      if (optWarpService.isPresent()) {
        java.util.List<Text> texts = new ArrayList<>();

        Collection<Warp> warps;
        if (args.hasAny("world")) {
          String worldName = args.<WorldProperties>getOne("world").get().getWorldName();

          warps = new ArrayList<>();
          for (Warp warp : WarpImp.getWarps().values().stream().filter(w -> w.getLocation().getExtent().getName().equals(worldName))
              .toArray(Warp[]::new)) {
            warps.add(warp);
          }
        } else {
          warps = WarpImp.getWarps().values();
        }

        //#f:0
        warps.forEach(w -> texts.add(
                Text.builder()
                .append(Text.join(Text.of(TextColors.GREEN, "Nom: "),
                    Text.of(TextColors.WHITE, w.getName()),
                    Text.of(TextColors.GREEN, " ; "),
                    Text.of(TextColors.WHITE, w.getLocation().getExtent().getName())))
                .onClick(TextActions.runCommand("/warp " + w.getName()))
                .build()));
        //#f:1

        PaginationService paginationService = Sponge.getServiceManager().provide(PaginationService.class).get();
        paginationService.builder().title(Text.of(TextColors.GREEN, "Warps")).linesPerPage(15).padding(Text.of(TextColors.DARK_GREEN, "="))
            .contents(texts).sendTo(src);
      } else {
        src.sendMessage(Text.of(TextColors.RED, "Le service de warps n'a pas été initialisé correctement."));
      }

      return CommandResult.empty();
    }

    @Override
    public CommandSpec getCommandSpec() {
      //#f:0
      return CommandSpec.builder()
              .description(Text.of(""))
              .permission("essentials.command.warp.list")
              .arguments(GenericArguments.optional(GenericArguments.world(Text.of("world"))))
              .executor(this)
              .build();
      //#f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "list" };
    }
  }

  static class Lock extends AbstractCommand {
    public Lock(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      Warp warp = args.<Warp>getOne("warp").get();
      warp.setLocked(warp.isLocked());
      if (DaoFactory.getWarpDao().update(warp)) {
        src.sendMessage(Text.of(TextColors.YELLOW, "Le warp a été " + (warp.isLocked() ? "" : "dé") + "verrouillé."));
        return CommandResult.success();
      }
      src.sendMessage(Text.of(TextColors.RED, "L'opération a échoué."));
      return CommandResult.empty();
    }

    @Override
    public CommandSpec getCommandSpec() {
      //#f:0
      return CommandSpec.builder()
              .description(Text.of("Permet de (dé)verrouiller un warp"))
              .permission("essentials.command.warp.lock")
              .executor(this)
              .arguments(GenericArguments.choices(Text.of("warp"), WarpImp.getWarps()::keySet, WarpImp.getWarps()::get))
              .build();
      //#f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "lock" };
    }
  }
}