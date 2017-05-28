package net.mcfr.commands;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import net.mcfr.Essentials;
import net.mcfr.commands.utils.AbstractCommand;
import net.mcfr.utils.McFrPlayer;

public class ItemCommand extends AbstractCommand {

  public ItemCommand(Essentials plugin) {
    super(plugin);
  }

  public static void applyItemDescription(Player p, String description) {
    Optional<ItemStack> it = p.getItemInHand(HandTypes.MAIN_HAND);
    if (it.isPresent()) {
      ItemStack it1 = it.get();

      Optional<java.util.List<Text>> optionalList = it1.get(Keys.ITEM_LORE);
      java.util.List<Text> list = optionalList.orElse(new ArrayList<>());
      list.add(Text.of(description));
      it1.offer(Keys.ITEM_LORE, list);

      p.setItemInHand(HandTypes.MAIN_HAND, it1);
      p.sendMessage(Text.of(TextColors.YELLOW, "La description de l'item a été modifiée : " + description + "."));
    } else {
      p.sendMessage(Text.of(TextColors.RED, "Vous devez tenir un objet en main pour pouvoir modifier sa description !"));
    }
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    src.sendMessage(Text.of(TextColors.RED, "Utilisation : /item ?"));
    return CommandResult.empty();
  }

  @Override
  public CommandSpec getCommandSpec() {
    //#f:0
    return CommandSpec.builder()
            .description(Text.of("Commande de manipulation d'un objet."))
            .permission("essentials.command.item")
            .executor(this)
            .children(getChildrenList(new Help(getPlugin()),
                new Name(getPlugin()),
                new Description(getPlugin()),
                new Clear(getPlugin())))
            .build();
    // #f:1
  }

  @Override
  public String[] getAliases() {
    return new String[] { "item" };
  }

  static class Help extends AbstractCommand {

    public Help(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      src.sendMessage(createNotification("=== Aide du /item ==="));
      src.sendMessage(createNotification(" /item ? => Affiche l'aide de la commande"));
      src.sendMessage(createNotification(" /item name <nom> => Attribue un nom à l'item tenu en main"));
      src.sendMessage(createNotification(" /item desc <desc> => Attribue une description à l'item tenu en main"));
      src.sendMessage(createNotification(" /item list => Affiche la liste des descriptions"));
      src.sendMessage(createNotification(" /item read <desc> => Affiche la description renseignée"));
      src.sendMessage(createNotification(" /item load <desc> => Applique la description renseignée à l'item tenu en main"));
      src.sendMessage(createNotification(" /item clear => Supprime la description de l'item tenu en main"));
      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      //#f:0
      return CommandSpec.builder()
              .description(Text.of("Aide de la commande item"))
              .permission("essentials.command.item.help")
              .executor(this)
              .build();
      //#f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "?" };
    }

  }

  static class Name extends AbstractCommand {

    public Name(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      if (src instanceof Player) {
        Player p = (Player) src;
        Optional<ItemStack> it = p.getItemInHand(HandTypes.MAIN_HAND);
        if (it.isPresent()) {
          ItemStack it1 = it.get();
          String name = args.<String>getOne("nom").get();
          it1.offer(Keys.DISPLAY_NAME, Text.of(name));
          p.setItemInHand(HandTypes.MAIN_HAND, it1);
          src.sendMessage(Text.of(TextColors.YELLOW, "L'item a été renommé : " + name + "."));
          return CommandResult.success();
        } else
          src.sendMessage(Text.of(TextColors.RED, "Vous devez tenir un objet en main pour pouvoir modifier son nom !"));
      } else
        src.sendMessage(ONLY_PLAYERS_COMMAND);
      return CommandResult.empty();
    }

    @Override
    public CommandSpec getCommandSpec() {
      //#f:0
      return CommandSpec.builder()
              .description(Text.of("Permet de modifier le nom d'un objet."))
              .permission("essentials.command.item.name")
              .arguments(GenericArguments.remainingJoinedStrings(Text.of("nom")))
              .executor(this)
              .build();
      //#f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "name" };
    }

  }

  static class Description extends AbstractCommand {

    public Description(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      if (src instanceof Player) {
        applyItemDescription((Player) src, args.<String>getOne("description").get());
      } else
        src.sendMessage(ONLY_PLAYERS_COMMAND);
      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      //#f:0
      return CommandSpec.builder()
              .description(Text.of("Permet de modifier la description d'un objet."))
              .permission("essentials.command.item.desc")
              .arguments(GenericArguments.remainingJoinedStrings(Text.of("description")))
              .executor(this)
              .build();
      //#f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "desc" };
    }
  }

  static class Clear extends AbstractCommand {

    public Clear(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      if (src instanceof Player) {
        Player p = (Player) src;
        Optional<ItemStack> it = p.getItemInHand(HandTypes.MAIN_HAND);
        if (it.isPresent()) {
          ItemStack it1 = it.get();

          Optional<java.util.List<Text>> optionalList = it1.get(Keys.ITEM_LORE);
          java.util.List<Text> list = optionalList.orElse(new ArrayList<>());
          list.clear();
          it1.offer(Keys.ITEM_LORE, list);

          p.setItemInHand(HandTypes.MAIN_HAND, it1);
          src.sendMessage(Text.of(TextColors.YELLOW, "La description de l'item a été effacée."));
        } else
          src.sendMessage(Text.of(TextColors.RED, "Vous devez tenir un objet en main pour pouvoir effacer sa description !"));
      } else
        src.sendMessage(ONLY_PLAYERS_COMMAND);
      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      //#f:0
      return CommandSpec.builder()
              .description(Text.of("Vide la description d'un item."))
              .permission("essentials.command.item.clear")
              .executor(this)
              .build();
      //#f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "clear" };
    }
  }

  static class List extends AbstractCommand {

    public List(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      if (src instanceof Player) {
        McFrPlayer p = McFrPlayer.getMcFrPlayer((Player) src);

        java.util.List<Text> texts = new ArrayList<>();

        Map<String, String> descriptions = p.getItemDescriptions();

        //#f:0
        descriptions.keySet().forEach(n -> texts.add(
                Text.builder()
                .append(Text.join(Text.of(TextColors.GREEN, "Nom: "),
                    Text.of(TextColors.WHITE, n),
                    Text.of(TextColors.GREEN, " : \""),
                    Text.of(TextColors.WHITE, descriptions.get(n).substring(0, 50)),
                    Text.of(TextColors.GREEN, "\"")))
                .onClick(TextActions.suggestCommand("/item load " + n))
                .build()));
        //#f:1

        PaginationService paginationService = Sponge.getServiceManager().provide(PaginationService.class).get();
        paginationService.builder().title(Text.of(TextColors.GREEN, "Descriptions")).linesPerPage(15).padding(Text.of(TextColors.DARK_GREEN, "="))
            .contents(texts).sendTo(src);
      } else
        src.sendMessage(ONLY_PLAYERS_COMMAND);
      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      //#f:0
      return CommandSpec.builder()
              .description(Text.of("Donne la liste de descriptions pré-enregistrées du joueur."))
              .permission("essentials.command.item.list")
              .executor(this)
              .build();
      //#f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "list" };
    }
  }

  static class Load extends AbstractCommand {

    public Load(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      if (src instanceof Player) {
        McFrPlayer p = McFrPlayer.getMcFrPlayer((Player) src);
        String name = args.<String>getOne("nom").get();

        if (p.hasItemDescription(name)) {
          applyItemDescription(p.getPlayer(), p.getItemDescription(name));
        } else {
          src.sendMessage(Text.of(TextColors.RED, "Le nom que vous avez spécifié ne correspond à aucune de vos descriptions. Utilisez /item list."));
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
              .description(Text.of("Applique à l'item tenu en main la description pré-enregistrée du nom spécifié."))
              .permission("essentials.command.item.load")
              .arguments(GenericArguments.string(Text.of("nom")))
              .executor(this)
              .build();
      //#f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "load" };
    }
  }

  static class Read extends AbstractCommand {

    public Read(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      if (src instanceof Player) {
        McFrPlayer p = McFrPlayer.getMcFrPlayer((Player) src);
        String name = args.<String>getOne("nom").get();

        if (p.hasItemDescription(name)) {

          //#f:0
          src.sendMessage(Text.builder()
              .append(Text.join(Text.of(TextColors.GREEN, "Nom: "),
              Text.of(TextColors.WHITE, name),
              Text.of(TextColors.GREEN, " : \""),
              Text.of(TextColors.WHITE, p.getItemDescription(name)),
              Text.of(TextColors.GREEN, "\"")))
              .onClick(TextActions.suggestCommand("/item load " + name))
              .build());
          //#f:1

        } else {
          src.sendMessage(Text.of(TextColors.RED, "Le nom que vous avez spécifié ne correspond à aucune de vos descriptions. Utilisez /item list."));
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
              .description(Text.of("Affiche la description pré-enregistrée du nom spécifié."))
              .permission("essentials.command.item.read")
              .arguments(GenericArguments.string(Text.of("nom")))
              .executor(this)
              .build();
      //#f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "read" };
    }
  }
}
