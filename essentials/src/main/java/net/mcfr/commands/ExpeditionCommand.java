package net.mcfr.commands;

import java.util.ArrayList;
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
import org.spongepowered.api.text.format.TextColors;

import net.mcfr.Essentials;
import net.mcfr.commands.utils.AbstractCommand;
import net.mcfr.expedition.AuthorizedArea;
import net.mcfr.expedition.ExpeditionService;
import net.mcfr.utils.McFrPlayer;

public class ExpeditionCommand extends AbstractCommand {

  public ExpeditionCommand(Essentials plugin) {
    super(plugin);
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    src.sendMessage(Text.of(TextColors.RED, "Merci de choisir l'une des actions suivantes : add, remove, list, addarea, removearea, listareas"));
    return CommandResult.empty();
  }

  @Override
  public CommandSpec getCommandSpec() {
    //#f:0
    return CommandSpec.builder()
            .description(Text.of("Gestion des joueurs autorisés à partir en expédition."))
            .permission("essentials.command.expedition")
            .executor(this)
            .children(getChildrenList(new Add(getPlugin()), 
                new Remove(getPlugin()),
                new List(getPlugin()),
                new AddArea(getPlugin()),
                new RemoveArea(getPlugin()),
                new ListAreas(getPlugin())))
            .build();
    //#f:1
  }

  @Override
  public String[] getAliases() {
    return new String[] { "expedition" };
  }

  static class Add extends AbstractCommand {

    public Add(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      if (args.hasAny("joueur")) {
        Player player = args.<Player>getOne("joueur").get();
        if (!player.hasPermission("essentials.leavearea") && !player.hasPermission("essentials.freefromarea")) {
          Sponge.getCommandManager().process(Sponge.getServer().getConsole(),
              "pex user " + player.getUniqueId().toString() + " perm essentials.leavearea true");

          src.sendMessage(
              Text.of(TextColors.YELLOW, "Le joueur " + McFrPlayer.getMcFrPlayer(player).getName() + " peut à présent quitter la zone sécurisée."));
          player.sendMessage(Text.of(TextColors.YELLOW, "Un MJ vous a autorisé à quitter la zone sécurisée. Restez attentifs !"));
        } else {
          src.sendMessage(Text.of(TextColors.YELLOW,
              "Le joueur " + McFrPlayer.getMcFrPlayer(player).getName() + " est déjà autorisé à quitter la zone sécurisée."));
        }
      } else {
        src.sendMessage(Text.of(TextColors.RED, "Merci de renseigner les arguments : /expedition add <joueur>"));
      }
      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder()
          .description(Text.of("Autorise le joueur à quitter la zone sécurisée."))
          .permission("essentials.command.expedition.add")
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
      if (args.hasAny("joueur")) {
        Player player = args.<Player>getOne("joueur").get();
        if (player.hasPermission("essentials.leavearea")) {
          Sponge.getCommandManager().process(Sponge.getServer().getConsole(),
              "pex user " + player.getUniqueId().toString() + " perm essentials.leavearea false");
          src.sendMessage(Text.of(TextColors.YELLOW,
              "Le joueur " + McFrPlayer.getMcFrPlayer(player).getName() + " doit à présent rester dans la zone sécurisée."));
          player.sendMessage(Text.of(TextColors.YELLOW, "Vous devez à présent rester dans la zone sécurisée ! Ne vous éloignez pas trop."));
        } else {
          src.sendMessage(
              Text.of(TextColors.YELLOW, "Le joueur " + McFrPlayer.getMcFrPlayer(player).getName() + " doit déjà rester dans la zone sécurisée."));
        }
      } else {
        src.sendMessage(Text.of(TextColors.RED, "Merci de renseigner les arguments : /expedition remove <joueur>"));
      }
      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder()
          .description(Text.of("Interdit à un joueur de quitter la zone sécurisée."))
          .permission("essentials.command.expedition.remove")
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
    private String message;

    public List(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      this.message = "Joueurs autorisés à quitter la zone sécurisée :";

      Sponge.getServer().getOnlinePlayers().forEach(p -> {
        if (p.hasPermission("essentials.leavearea")) {
          this.message += "\n- " + p.getName();
        }
      });

      src.sendMessage(Text.of(TextColors.YELLOW, message));
      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder()
          .description(Text.of("Donne la liste des joueurs connectés autorisés à quitter la zone sécurisée."))
          .permission("essentials.command.expedition.list")
          .executor(this)
          .build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "list" };
    }
  }

  static class AddArea extends AbstractCommand {

    public AddArea(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      if (src instanceof Player) {
        if (args.hasAny("nom") && args.hasAny("rayon") && args.<Integer>getOne("rayon").get() > 0) {
          if (Sponge.getServiceManager().provide(ExpeditionService.class).get().addArea(args.<String>getOne("nom").get(),
              ((Player) src).getLocation(), args.<Integer>getOne("rayon").get())) {
            src.sendMessage(Text.of(TextColors.YELLOW,
                "Une zone a été créée : " + args.<String>getOne("nom").get() + " : " + args.<Integer>getOne("rayon").get()));
          } else {
            if (Sponge.getServiceManager().provide(ExpeditionService.class).isPresent()) {
              src.sendMessage(Text.of(TextColors.YELLOW, "Une zone porte déjà le nom : " + args.<String>getOne("nom").get()));
            } else {
              src.sendMessage(Text.of(TextColors.RED, "Le système d'expéditions n'a pas été correctement chargé."));
            }
          }
        } else {
          src.sendMessage(Text.of(TextColors.RED,
              "Merci de renseigner les arguments : /expedition addarea <nom> <rayon> (avec <rayon> strictement supérieur à 0)"));
        }

      } else {
        src.sendMessage(ONLY_PLAYERS_COMMAND);
      }

      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder()
          .description(Text.of("Crée un nouvelle zone d'expedition à l'emplacement du joueur."))
          .permission("essentials.command.expedition.addarea")
          .arguments(GenericArguments.string(Text.of("nom")),
              GenericArguments.integer(Text.of("rayon")))
          .executor(this)
          .build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "addarea" };
    }
  }

  static class RemoveArea extends AbstractCommand {

    public RemoveArea(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      if (args.hasAny("nom")) {
        Optional<ExpeditionService> optExpeditionService = Sponge.getServiceManager().provide(ExpeditionService.class);

        if (optExpeditionService.isPresent()) {
          ExpeditionService expeditionService = optExpeditionService.get();
          Optional<AuthorizedArea> optArea = expeditionService.getAreaByName(args.<String>getOne("nom").get());

          if (optArea.isPresent()) {
            if (args.<String>getOne("confirmation").isPresent() && args.<String>getOne("confirmation").get().equals("confirm")) {
              expeditionService.removeArea(optArea.get());
              src.sendMessage(Text.of(TextColors.YELLOW, "La zone a été effacée : " + args.<String>getOne("nom").get()));
            } else {
              src.sendMessage(Text.of(TextColors.YELLOW, "La zone a été trouvée : " + args.<String>getOne("nom").get()
                  + ". Merci de recommencer la commande en ajoutant \"confirm\" à la fin pour confirmer la suppression."));
            }
          } else {
            src.sendMessage(Text.of(TextColors.YELLOW, "Il n'y a aucune zone portant le nom : " + args.<String>getOne("nom").get()));
          }
        } else {
          src.sendMessage(Text.of(TextColors.RED, "Le système d'expéditions n'a pas été correctement chargé."));
        }
      } else {
        src.sendMessage(Text.of(TextColors.RED, "Merci de renseigner les arguments : /expedition removearea <nom> confirm"));
      }

      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder()
          .description(Text.of("Détruit définitivement la zone sécurisée au nom spécifié."))
          .permission("essentials.command.expedition.removearea")
          .arguments(GenericArguments.string(Text.of("nom")),
              GenericArguments.optional(GenericArguments.string(Text.of("confirmation"))))
          .executor(this)
          .build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "removearea" };
    }
  }

  static class ListAreas extends AbstractCommand {

    public ListAreas(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      Optional<ExpeditionService> optExpeditionService = Sponge.getServiceManager().provide(ExpeditionService.class);

      if (optExpeditionService.isPresent()) {
        ExpeditionService expeditionService = optExpeditionService.get();

        java.util.List<AuthorizedArea> areas = expeditionService.getAreas();
        java.util.List<Text> texts = new ArrayList<>(areas.size());
        areas.forEach(a -> texts.add(Text.of(TextColors.YELLOW, "- " + a)));

        PaginationService paginationService = Sponge.getServiceManager().provide(PaginationService.class).get();
        paginationService.builder().title(Text.of(TextColors.YELLOW, "Zones sécurisées")).linesPerPage(10).padding(Text.of(TextColors.GOLD, "="))
            .contents(texts).sendTo(src);
      } else {
        src.sendMessage(Text.of(TextColors.RED, "Le système d'expéditions n'a pas été correctement chargé."));
      }

      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder()
          .description(Text.of("Donne la liste des zones d'expédition existantes."))
          .permission("essentials.command.expedition.listareas")
          .executor(this)
          .build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "listareas" };
    }
  }
}
