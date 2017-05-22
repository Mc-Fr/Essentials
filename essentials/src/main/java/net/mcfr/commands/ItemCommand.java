package net.mcfr.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import net.mcfr.Essentials;
import net.mcfr.commands.utils.AbstractCommand;

public class ItemCommand extends AbstractCommand {

  public ItemCommand(Essentials plugin) {
    super(plugin);
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
        Player p = (Player) src;
        Optional<ItemStack> it = p.getItemInHand(HandTypes.MAIN_HAND);
        if (it.isPresent()) {
          ItemStack it1 = it.get();

          Optional<List<Text>> optionalList = it1.get(Keys.ITEM_LORE);
          List<Text> list = optionalList.orElse(new ArrayList<>());
          String description = args.<String>getOne("description").get();
          list.add(Text.of(description));
          it1.offer(Keys.ITEM_LORE, list);

          p.setItemInHand(HandTypes.MAIN_HAND, it1);
          src.sendMessage(Text.of(TextColors.YELLOW, "La description de l'item a été modifiée : " + description + "."));
        } else
          src.sendMessage(Text.of(TextColors.RED, "Vous devez tenir un objet en main pour pouvoir modifier sa description !"));
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

          Optional<List<Text>> optionalList = it1.get(Keys.ITEM_LORE);
          List<Text> list = optionalList.orElse(new ArrayList<>());
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
}
