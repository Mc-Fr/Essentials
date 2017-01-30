package net.mcfr.commands;

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
import net.mcfr.babel.TribalWord;
import net.mcfr.commands.utils.AbstractCommand;

public class TribalLanguageCommand extends AbstractCommand {
  public TribalLanguageCommand(Essentials plugin) {
    super(plugin);
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    src.sendMessage(Text.of(TextColors.RED, "Merci de renseigner l'action que vous souhaitez effectuer."));
    return CommandResult.empty();
  }

  @Override
  public CommandSpec getCommandSpec() {
 // #f:0
    return CommandSpec.builder()
        .description(Text.of("Commande de dictionnaire tribal."))
        .permission("essentials.command.tribal")
        .executor(this)
        .children(getChildrenList(new RandomWords(getPlugin()), 
            new Add(getPlugin()), 
            new Tribal(getPlugin()), 
            new Common(getPlugin())))
        .build();
    // #f:1
  }

  @Override
  public String[] getAliases() {
    return new String[] { "tribal" };
  }

  static class RandomWords extends AbstractCommand {
    private String message;

    public RandomWords(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      if (args.hasAny("joueur") && args.hasAny("nombre de mots") && args.hasAny("niveau des mots")) {
        Player player = args.<Player>getOne("joueur").get();

        this.message = "Vous apprenez les mots tribaux suivants (prenez des notes !) :";

        TribalWord.getRandomsByLevel(args.<Integer>getOne("nombre de mots").get(), args.<Integer>getOne("niveau des mots").get())
            .forEach(w -> this.message += "\n- " + w.getTranslationString());

        player.sendMessage(Text.of(TextColors.BLUE, this.message));
      } else {
        src.sendMessage(Text.of(TextColors.YELLOW, "Merci de renseigner les arguments : /tribal random <joueur> <nombre de mots> <niveau des mots>"));
      }
      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder()
          .description(Text.of("Affiche pour le joueur une sélection aléatoire de mots d'un certain niveau."))
          .permission("essentials.command.burrow.random")
          .arguments(GenericArguments.player(Text.of("joueur")),
              GenericArguments.integer(Text.of("nombre de mots")),
              GenericArguments.integer(Text.of("niveau des mots")))
          .executor(this)
          .build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "random" };
    }
  }

  static class Add extends AbstractCommand {

    public Add(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      if (args.hasAny("mot tribal") && args.hasAny("mot commun") && args.hasAny("niveau")) {
        TribalWord word = TribalWord.add(args.<String>getOne("mot tribal").get(), args.<String>getOne("mot commun").get(), args.<Integer>getOne("niveau").get());
        src.sendMessage(Text.of(TextColors.YELLOW, "Mot de niveau " + word.getLevel() + " ajouté : " + word.getWord() + " = " + word.getTranslation()));
      } else {
        src.sendMessage(Text.of(TextColors.YELLOW, "Merci de renseigner les arguments : /tribal add <mot tribal> <mot commun> <niveau>"));
      }
      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder()
          .description(Text.of("Ajoute un mot au dictionnaire tribal."))
          .permission("essentials.command.burrow.add")
          .arguments(GenericArguments.string(Text.of("mot tribal")),
              GenericArguments.string(Text.of("mot commun")),
              GenericArguments.integer(Text.of("niveau")))
          .executor(this)
          .build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "add" };
    }
  }

  static class Tribal extends AbstractCommand {

    public Tribal(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      if (args.hasAny("mot")) {
        String word = args.<String>getOne("mot").get();
        src.sendMessage(Text.of(TextColors.YELLOW, "- " + word + " = " + TribalWord.getTribalTranslation(word).orElse("Non traduit")));
      } else {
        src.sendMessage(Text.of(TextColors.YELLOW, "Merci de renseigner les arguments : /tribal tribal <mot commun>"));
      }
      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder()
          .description(Text.of("Donne la traduction tribale du mot commun renseigné."))
          .permission("essentials.command.burrow.tribal")
          .arguments(GenericArguments.string(Text.of("mot")))
          .executor(this)
          .build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "tribal" };
    }
  }

  static class Common extends AbstractCommand {

    public Common(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      if (args.hasAny("mot")) {
        String word = args.<String>getOne("mot").get();
        src.sendMessage(Text.of(TextColors.YELLOW, "- " + word + " = " + TribalWord.getCommonTranslation(word).orElse("Non traduit")));
      } else {
        src.sendMessage(Text.of(TextColors.YELLOW, "Merci de renseigner les arguments : /tribal common <mot tribal>"));
      }
      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder()
          .description(Text.of("Donne la traduction commune du mot tribal renseigné."))
          .permission("essentials.command.burrow.common")
          .arguments(GenericArguments.string(Text.of("mot")))
          .executor(this)
          .build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "common" };
    }
  }
}
