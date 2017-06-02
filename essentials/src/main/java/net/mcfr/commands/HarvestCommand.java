package net.mcfr.commands;

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
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import net.mcfr.Essentials;
import net.mcfr.commands.utils.AbstractCommand;
import net.mcfr.dao.HarvestTools;
import net.mcfr.harvest.HarvestArea;
import net.mcfr.harvest.HarvestImp;
import net.mcfr.harvest.HarvestService;
import net.mcfr.roleplay.Skill;
import net.mcfr.utils.McFrPlayer;

public class HarvestCommand extends AbstractCommand {

  public HarvestCommand(Essentials plugin) {
    super(plugin);
  }

  public void askForHarvest(McFrPlayer p, HarvestArea area, HarvestService harvest) {
    //#f:0
    Text clickableText = Text.builder()
        .append(Text.of(TextColors.DARK_GREEN, ">> Confirmer <<"))
        .onClick(TextActions.executeCallback((cmdSrc) -> harvest.harvest(p, area)))
        .build();
    
    p.sendMessage(Text.join(Text.of(TextColors.GREEN, "Voulez-vous récolter : "),
        Text.of(TextColors.WHITE, area.getName()),
        Text.of(TextColors.GREEN, " ? "),
        clickableText));
    //f#1
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    Optional<HarvestService> optHarvestService = Sponge.getServiceManager().provide(HarvestService.class);

    if (!optHarvestService.isPresent()) {
      src.sendMessage(Text.of(TextColors.RED, "Le système de récolte n'a pas été correctement initialisé."));
      return CommandResult.success();
    }

    HarvestService harvest = optHarvestService.get();

    if (src instanceof Player) {
      McFrPlayer p = McFrPlayer.getMcFrPlayer((Player) src);

      java.util.List<Text> texts = new ArrayList<>();

      java.util.List<HarvestArea> areas = harvest.getAreasForPlayer(p);

      //#f:0
      if (p.getHarvestTokens() == 0) {
        texts.add(Text.of(TextColors.GREEN, "Vous n'avez plus de jeton de récolte."));
      } else {
        texts.add(Text.join(Text.of(TextColors.GREEN, "Vous avez "),
            Text.of(TextColors.WHITE, p.getHarvestTokens()),
            Text.of(TextColors.GREEN, " jetons de récolte. Votre jeton actuel vaut "),
            Text.of(TextColors.WHITE, p.getTokenValue()),
            Text.of(TextColors.GREEN, "% d'une récolte.")));
      }
      
      areas.forEach(a -> texts.add(
              Text.builder()
              .append(Text.join(Text.of(TextColors.DARK_GREEN, "Récolte de : "),
                  Text.of(TextColors.WHITE, a.getName())))
              .onClick(TextActions.executeCallback((cmdSrc) -> askForHarvest(p, a, harvest)))
              .build()));
      
      texts.add(Text.of(TextColors.GREEN, "Cliquez sur une ressource pour la récolter."));
      //#f:1

      PaginationService paginationService = Sponge.getServiceManager().provide(PaginationService.class).get();
      paginationService.builder().title(Text.of(TextColors.GREEN, "Zones de récolte")).linesPerPage(15).padding(Text.of(TextColors.DARK_GREEN, "="))
          .contents(texts).sendTo(src);

    } else {
      src.sendMessage(ONLY_PLAYERS_COMMAND);
    }
    return CommandResult.success();
  }

  @Override
  public CommandSpec getCommandSpec() {
    //#f:0
    return CommandSpec.builder()
            .description(Text.of("Propose la liste des zones de récolte avoisinnantes."))
            .permission("essentials.command.harvest")
            .executor(this)
            .children(getChildrenList(new List(getPlugin()),
                new AddArea(getPlugin()),
                new RemoveArea(getPlugin()),
                new AddItemEntry(getPlugin()),
                new RemoveItemEntry(getPlugin()),
                new AddRareItemEntry(getPlugin()),
                new RemoveRareItemEntry(getPlugin()),
                new Tokens(getPlugin())))
            .build();
    //#f:1
  }

  @Override
  public String[] getAliases() {
    return new String[] { "harvest" };
  }

  static class List extends AbstractCommand {

    public List(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      Collection<HarvestArea> areas = HarvestImp.getHarvestAreas().values();

      java.util.List<Text> texts = new ArrayList<>(areas.size());
      areas.forEach(
          a -> texts.add(Text.builder().append((Text.of(TextColors.WHITE, "- " + a.getName()))).onClick(TextActions.executeCallback((cmdSrc) -> {
            if (cmdSrc instanceof Player)
              ((Player) cmdSrc).setLocation(a.getLocation());
          })).build()));

      PaginationService paginationService = Sponge.getServiceManager().provide(PaginationService.class).get();
      paginationService.builder().title(Text.of(TextColors.GREEN, "Zones de récolte")).linesPerPage(15).padding(Text.of(TextColors.GREEN, "="))
          .contents(texts).sendTo(src);

      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      //#f:0
      return CommandSpec.builder()
              .description(Text.of("Donne la liste des zones de récolte."))
              .permission("essentials.command.harvest.list")
              .executor(this)
              .build();
      //#f:1
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
      Optional<HarvestService> optHarvestService = Sponge.getServiceManager().provide(HarvestService.class);

      if (!optHarvestService.isPresent()) {
        src.sendMessage(Text.of(TextColors.RED, "Le système de récolte n'a pas été correctement initialisé."));
        return CommandResult.success();
      }

      if (src instanceof Player) {
        //#f:0
        optHarvestService.get().addArea(args.<String>getOne("nom").get(),
            ((Player) src).getLocation(),
            args.<Skill>getOne("compétence").get(),
            args.<HarvestTools>getOne("outil").get(),
            args.<Integer>getOne("usure").get());
        //#f:1
        src.sendMessage(Text.of(TextColors.GREEN, "La zone a été ajoutée."));
      } else {
        src.sendMessage(ONLY_PLAYERS_COMMAND);
      }

      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      //#f:0
      return CommandSpec.builder()
              .description(Text.of("Ajoute une zone de récolte à l'emplacement du joueur"))
              .permission("essentials.command.harvest.addarea")
              .arguments(GenericArguments.string(Text.of("nom")),
                  GenericArguments.choices(Text.of("compétence"), Skill.getHarvestSkills()),
                  GenericArguments.enumValue(Text.of("outil"), HarvestTools.class),
                  GenericArguments.integer(Text.of("usure")))
              .executor(this)
              .build();
      //#f:1
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
      Optional<HarvestService> optHarvestService = Sponge.getServiceManager().provide(HarvestService.class);

      if (!optHarvestService.isPresent()) {
        src.sendMessage(Text.of(TextColors.RED, "Le système de récolte n'a pas été correctement initialisé."));
        return CommandResult.success();
      }

      optHarvestService.get().removeArea(args.<HarvestArea>getOne("zone").get());
      src.sendMessage(Text.of(TextColors.GREEN, "La zone a été supprimée."));

      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      //#f:0
      return CommandSpec.builder()
              .description(Text.of("Supprime la zone de récolte sélectionnée"))
              .permission("essentials.command.harvest.removearea")
              .arguments(GenericArguments.choices(Text.of("zone"), HarvestImp.getHarvestAreas()::keySet, HarvestImp.getHarvestAreas()::get))
              .executor(this)
              .build();
      //#f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "removearea" };
    }
  }

  static class AddItemEntry extends AbstractCommand {
    public AddItemEntry(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      Optional<HarvestService> optHarvestService = Sponge.getServiceManager().provide(HarvestService.class);

      if (!optHarvestService.isPresent()) {
        src.sendMessage(Text.of(TextColors.RED, "Le système de récolte n'a pas été correctement initialisé."));
        return CommandResult.success();
      }

      if (src instanceof Player) {
        Optional<ItemStack> optItem = ((Player) src).getItemInHand(HandTypes.MAIN_HAND);

        if (optItem.isPresent()) {
          optHarvestService.get().addItemEntry(optItem.get(), args.<HarvestArea>getOne("zone").get());
          src.sendMessage(Text.of(TextColors.GREEN, "Le stack a été ajouté à la zone."));
        } else {
          src.sendMessage(Text.of(TextColors.GREEN, "Vous devez tenir un stack d'items pour l'ajouter sur la zone."));
        }
      } else {
        src.sendMessage(ONLY_PLAYERS_COMMAND);
      }

      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      //#f:0
      return CommandSpec.builder()
              .description(Text.of("Ajoute le stack d'item porté à la zone de récolte spécifiée"))
              .permission("essentials.command.harvest.additem")
              .arguments(GenericArguments.choices(Text.of("zone"), HarvestImp.getHarvestAreas()::keySet, HarvestImp.getHarvestAreas()::get))
              .executor(this)
              .build();
      //#f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "additem" };
    }
  }

  static class RemoveItemEntry extends AbstractCommand {
    public RemoveItemEntry(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      Optional<HarvestService> optHarvestService = Sponge.getServiceManager().provide(HarvestService.class);

      if (!optHarvestService.isPresent()) {
        src.sendMessage(Text.of(TextColors.RED, "Le système de récolte n'a pas été correctement initialisé."));
        return CommandResult.success();
      }

      if (src instanceof Player) {
        Optional<ItemStack> optItem = ((Player) src).getItemInHand(HandTypes.MAIN_HAND);

        if (optItem.isPresent()) {
          optHarvestService.get().removeItemEntry(optItem.get(), args.<HarvestArea>getOne("zone").get());
          src.sendMessage(Text.of(TextColors.GREEN, "Le stack a été retiré de la zone."));
        } else {
          src.sendMessage(Text.of(TextColors.GREEN, "Vous devez tenir un stack d'items pour le retirer de la zone."));
        }
      } else {
        src.sendMessage(ONLY_PLAYERS_COMMAND);
      }

      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      //#f:0
      return CommandSpec.builder()
              .description(Text.of("Retire les items du type porté à la zone de récolte spécifiée"))
              .permission("essentials.command.harvest.removeitem")
              .arguments(GenericArguments.choices(Text.of("zone"), HarvestImp.getHarvestAreas()::keySet, HarvestImp.getHarvestAreas()::get))
              .executor(this)
              .build();
      //#f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "removeitem" };
    }
  }

  static class AddRareItemEntry extends AbstractCommand {
    public AddRareItemEntry(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      Optional<HarvestService> optHarvestService = Sponge.getServiceManager().provide(HarvestService.class);

      if (!optHarvestService.isPresent()) {
        src.sendMessage(Text.of(TextColors.RED, "Le système de récolte n'a pas été correctement initialisé."));
        return CommandResult.success();
      }

      if (src instanceof Player) {
        Optional<ItemStack> optItem = ((Player) src).getItemInHand(HandTypes.MAIN_HAND);

        if (optItem.isPresent()) {
          optHarvestService.get().addRareItemEntry(optItem.get(), args.<Double>getOne("probabilité").get(), args.<HarvestArea>getOne("zone").get());
          src.sendMessage(Text.of(TextColors.GREEN, "Le stack d'items rares a été ajouté à la zone."));
        } else {
          src.sendMessage(Text.of(TextColors.GREEN, "Vous devez tenir un stack d'items pour l'ajouter sur la zone."));
        }
      } else {
        src.sendMessage(ONLY_PLAYERS_COMMAND);
      }

      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      //#f:0
      return CommandSpec.builder()
              .description(Text.of("Ajoute l'item porté à la zone de récolte spécifiée avec la probabiité renseignée"))
              .permission("essentials.command.harvest.addrareitem")
              .arguments(GenericArguments.choices(Text.of("zone"), HarvestImp.getHarvestAreas()::keySet, HarvestImp.getHarvestAreas()::get),
                  GenericArguments.doubleNum(Text.of("probabilité")))
              .executor(this)
              .build();
      //#f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "addrareitem" };
    }
  }

  static class RemoveRareItemEntry extends AbstractCommand {
    public RemoveRareItemEntry(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      Optional<HarvestService> optHarvestService = Sponge.getServiceManager().provide(HarvestService.class);

      if (!optHarvestService.isPresent()) {
        src.sendMessage(Text.of(TextColors.RED, "Le système de récolte n'a pas été correctement initialisé."));
        return CommandResult.success();
      }

      if (src instanceof Player) {
        Optional<ItemStack> optItem = ((Player) src).getItemInHand(HandTypes.MAIN_HAND);

        if (optItem.isPresent()) {
          optHarvestService.get().removeRareItemEntry(optItem.get(), args.<HarvestArea>getOne("zone").get());
          src.sendMessage(Text.of(TextColors.GREEN, "Le stack d'items rares a été retiré de la zone."));
        } else {
          src.sendMessage(Text.of(TextColors.GREEN, "Vous devez tenir un stack d'items pour le retirer de la zone."));
        }
      } else {
        src.sendMessage(ONLY_PLAYERS_COMMAND);
      }

      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      //#f:0
      return CommandSpec.builder()
              .description(Text.of("Retire l'objet rare porté de la zone spécifiée"))
              .permission("essentials.command.harvest.removerareitem")
              .arguments(GenericArguments.choices(Text.of("zone"), HarvestImp.getHarvestAreas()::keySet, HarvestImp.getHarvestAreas()::get))
              .executor(this)
              .build();
      //#f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "removerareitem" };
    }
  }

  static class Tokens extends AbstractCommand {
    public Tokens(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      McFrPlayer p = McFrPlayer.getMcFrPlayer(args.<Player>getOne("joueur").get());

      if (args.hasAny("valeur")) {
        int value = args.<Integer>getOne("valeur").get();

        p.setHarvestTokens(value);
        src.sendMessage(Text.of(TextColors.GREEN, "Les jetons de récolte de " + p.getPlayer().getName() + " ont été fixés à " + value + "."));
      } else {
        src.sendMessage(Text.of(TextColors.GREEN, p.getPlayer().getName() + " a " + p.getHarvestTokens() + " jeton(s) de récolte."));
      }

      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      //#f:0
      return CommandSpec.builder()
              .description(Text.of("Donne ou modifie le nombre de jetons de récolte du joueur spécifié"))
              .permission("essentials.command.harvest.token")
              .arguments(GenericArguments.player(Text.of("joueur")), GenericArguments.optional(GenericArguments.integer(Text.of("valeur"))))
              .executor(this)
              .build();
      //#f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "tokens" };
    }
  }

}
