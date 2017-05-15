package net.mcfr.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
import net.mcfr.death.CareCenter;
import net.mcfr.death.CareImp;
import net.mcfr.services.CareService;

public class CareCenterCommand extends AbstractCommand {

  public CareCenterCommand(Essentials plugin) {
    super(plugin);
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args)
      throws CommandException {
    src.sendMessage(Text.of(TextColors.RED,
        "Merci de choisir l'une des actions suivantes : addcenter, removecenter, listcenters, givefaction, removefaction, listfactions"));
    return CommandResult.empty();
  }

  @Override
  public CommandSpec getCommandSpec() {
    //#f:0
    return CommandSpec.builder()
            .description(Text.of("Gestion des centres de soins."))
            .permission("essentials.command.carecenter")
            .executor(this)
            .children(getChildrenList(new AddCenter(getPlugin()),
                new RemoveCenter(getPlugin()),
                new ListCenters(getPlugin()),
                new GiveFaction(getPlugin()),
                new RemoveFaction(getPlugin()),
                new ListFactions(getPlugin())))
            .build();
    //#f:1
  }

  @Override
  public String[] getAliases() {
    return new String[] { "carecenter" };
  }

  static class AddCenter extends AbstractCommand {

    public AddCenter(Essentials plugin) {
      super(plugin);
      // TODO Auto-generated constructor stub
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args)
        throws CommandException {
      CareImp careService = (CareImp) Sponge.getServiceManager().provide(
          CareService.class).get();

      if (src instanceof Player) {
        if (args.hasAny("nom") && args.hasAny("rayon")
            && args.<Integer>getOne("rayon").get() > 0) {
          if (careService.addCenter(args.<String>getOne("nom").get(),
              ((Player) src).getLocation(), args.<Integer>getOne("rayon").get(),
              args.<String>getOne("faction").orElse("neutral")))
            src.sendMessage(Text.of(TextColors.YELLOW,
                "Un centre de soins a été créée : "
                    + args.<String>getOne("nom").get() + " : "
                    + args.<Integer>getOne("rayon").get() + ", "
                    + args.<String>getOne("faction").orElse("neutral")));
          else if (Sponge.getServiceManager().provide(
              CareService.class).isPresent())
            src.sendMessage(Text.of(TextColors.YELLOW,
                "Une zone porte déjà le nom " + args.<String>getOne("nom").get()
                    + " ou la faction spécifiée n'existe pas."));
          else
            src.sendMessage(Text.of(TextColors.RED,
                "Le système de centres de soins n'a pas été correctement chargé."));
        } else
          src.sendMessage(Text.of(TextColors.RED,
              "Merci de renseigner les arguments : /expedition addcenter <nom> <rayon> [faction] (avec <rayon> strictement supérieur à 0)"));

      } else
        src.sendMessage(ONLY_PLAYERS_COMMAND);

      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder()
          .description(Text.of("Crée un nouveau centre de soins à l'emplacement du joueur."))
          .permission("essentials.command.carecenter.addcenter")
          .arguments(GenericArguments.string(Text.of("nom")),
              GenericArguments.integer(Text.of("rayon")),
              GenericArguments.optional(GenericArguments.string(Text.of("faction"))))
          .executor(this)
          .build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "addcenter" };
    }
  }

  static class RemoveCenter extends AbstractCommand {

    public RemoveCenter(Essentials plugin) {
      super(plugin);
      // TODO Auto-generated constructor stub
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args)
        throws CommandException {
      if (args.hasAny("nom")) {
        Optional<CareService> optCareService = Sponge.getServiceManager().provide(
            CareService.class);

        if (optCareService.isPresent()) {
          CareImp careService = (CareImp) optCareService.get();
          Optional<CareCenter> optCenter = careService.getCenterByName(
              args.<String>getOne("nom").get());

          if (optCenter.isPresent()) {
            if (args.<String>getOne("confirmation").isPresent()
                && args.<String>getOne("confirmation").get().equals(
                    "confirm")) {
              careService.removeCenter(optCenter.get());
              src.sendMessage(Text.of(TextColors.YELLOW,
                  "Le centre de soins a été effacé : "
                      + args.<String>getOne("nom").get()));
            } else
              src.sendMessage(Text.of(TextColors.YELLOW,
                  "Le centre de soins a été trouvé : "
                      + args.<String>getOne("nom").get()
                      + ". Merci de recommencer la commande en ajoutant \"confirm\" à la fin pour confirmer la suppression."));
          } else
            src.sendMessage(Text.of(TextColors.YELLOW,
                "Il n'y a aucun centre de soins portant le nom : "
                    + args.<String>getOne("nom").get()));
        } else
          src.sendMessage(Text.of(TextColors.RED,
              "Le système de centres de soins n'a pas été correctement chargé."));
      } else
        src.sendMessage(Text.of(TextColors.RED,
            "Merci de renseigner les arguments : /carecenter removecenter <nom> confirm"));

      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder()
          .description(Text.of("Détruit définitivement le centre de soins au nom spécifié."))
          .permission("essentials.command.carecenter.removecenter")
          .arguments(GenericArguments.string(Text.of("nom")),
              GenericArguments.optional(GenericArguments.string(Text.of("confirmation"))))
          .executor(this)
          .build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "removecenter" };
    }
  }

  static class ListCenters extends AbstractCommand {

    public ListCenters(Essentials plugin) {
      super(plugin);
      // TODO Auto-generated constructor stub
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args)
        throws CommandException {
      Optional<CareService> optCareService = Sponge.getServiceManager().provide(
          CareService.class);

      if (optCareService.isPresent()) {
        CareImp careService = (CareImp) optCareService.get();

        List<CareCenter> centers = careService.getCenters();
        List<Text> texts = new ArrayList<>(centers.size());
        centers.forEach(c -> texts.add(Text.of(TextColors.YELLOW, "- " + c)));

        PaginationService paginationService = Sponge.getServiceManager().provide(
            PaginationService.class).get();
        paginationService.builder().title(
            Text.of(TextColors.YELLOW, "Centres de soins")).linesPerPage(
                10).padding(Text.of(TextColors.GOLD, "=")).contents(
                    texts).sendTo(src);
      } else
        src.sendMessage(Text.of(TextColors.RED,
            "Le système de centres de soins n'a pas été correctement chargé."));

      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder()
          .description(Text.of("Donne la liste des centres de soins existants."))
          .permission("essentials.command.carecenter.listcenters")
          .executor(this)
          .build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "listcenters" };
    }
  }

  static class GiveFaction extends AbstractCommand {
    public GiveFaction(Essentials plugin) {
      super(plugin);
      // TODO Auto-generated constructor stub
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args)
        throws CommandException {
      // TODO
      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder()
          .description(Text.of("Donne une faction au joueur."))
          .permission("essentials.command.carecenter.givefaction")
          .arguments(GenericArguments.player(Text.of("joueur")),
              GenericArguments.string(Text.of("faction")))
          .executor(this)
          .build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "givefaction" };
    }
  }

  static class RemoveFaction extends AbstractCommand {
    public RemoveFaction(Essentials plugin) {
      super(plugin);
      // TODO Auto-generated constructor stub
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args)
        throws CommandException {
      // TODO
      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder()
          .description(Text.of("Retire une faction au joueur."))
          .permission("essentials.command.carecenter.removefaction")
          .arguments(GenericArguments.player(Text.of("joueur")),
              GenericArguments.string(Text.of("faction")))
          .executor(this)
          .build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "removefaction" };
    }
  }

  static class ListFactions extends AbstractCommand {

    public ListFactions(Essentials plugin) {
      super(plugin);
      // TODO Auto-generated constructor stub
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args)
        throws CommandException {
      Optional<CareService> optCareService = Sponge.getServiceManager().provide(
          CareService.class);

      if (optCareService.isPresent()) {
        CareImp careService = (CareImp) optCareService.get();

        Map<String, String> factions = careService.getFactions();
        List<Text> texts = new ArrayList<>(factions.size());
        factions.forEach(
            (s1, s2) -> texts.add(Text.of(TextColors.YELLOW, "- " + s1)));

        PaginationService paginationService = Sponge.getServiceManager().provide(
            PaginationService.class).get();
        paginationService.builder().title(
            Text.of(TextColors.YELLOW, "Factions")).linesPerPage(10).padding(
                Text.of(TextColors.GOLD, "=")).contents(texts).sendTo(src);
      } else
        src.sendMessage(Text.of(TextColors.RED,
            "Le système de centres de soins n'a pas été correctement chargé."));

      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder()
          .description(Text.of("Donne la liste des factions existantes."))
          .permission("essentials.command.carecenter.listfactions")
          .executor(this)
          .build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "listfactions" };
    }
  }
}
