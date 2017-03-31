package net.mcfr.commands;

import static org.spongepowered.api.text.format.TextColors.YELLOW;

import java.util.Optional;

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
import net.mcfr.commands.utils.AbstractCommand;
import net.mcfr.roleplay.Attributes;
import net.mcfr.roleplay.Defenses;
import net.mcfr.roleplay.RolePlayService;
import net.mcfr.roleplay.RollTypes;
import net.mcfr.roleplay.Senses;
import net.mcfr.roleplay.Skills;
import net.mcfr.roleplay.rollResults.AttackRollResult;
import net.mcfr.roleplay.rollResults.AttributeRollResult;
import net.mcfr.roleplay.rollResults.DefenseRollResult;
import net.mcfr.roleplay.rollResults.PerceptionRollResult;
import net.mcfr.roleplay.rollResults.ResistanceRollResult;
import net.mcfr.roleplay.rollResults.RollResult;
import net.mcfr.roleplay.rollResults.SkillRollResult;
import net.mcfr.utils.McFrPlayer;

public class RollCommand extends AbstractCommand {
  public RollCommand(Essentials plugin) {
    super(plugin);
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    src.sendMessage(Text.of(TextColors.YELLOW, "------------------------- ROLL -------------------------\n"
        + "Cette commande vous permet de lancer des jets de dés.\n"
        + "Utilisation : /roll at/c/a/d/e/p [paramètres]\n"
        + "- at : jet d'attribut (for, dex, int, end)\n"
        + "- c : jet de compétence (forge, course...)\n"
        + "- a : jet d'attaque\n"
        + "- d : jet de défense\n"
        + "- e : jet d'encaissement\n"
        + "- p : jet de perception\n"
        + "Essayez l'une des commandes pour plus de précisions !\n"
        + "--------------------------------------------------------------"));
    return CommandResult.success();
  }

  @Override
  public CommandSpec getCommandSpec() {
    //#f:0
    return CommandSpec.builder()
            .description(Text.of("Permet de faire des jets de dés."))
            .permission("essentials.command.roll")
            .executor(this)
            .children(getChildrenList(new Skill(getPlugin()),
                new Attribute(getPlugin()),
                new Resistance(getPlugin()),
                new Perception(getPlugin()),
                new Attack(getPlugin()),
                new Defense(getPlugin()),
                new None(getPlugin())))
            .build();
    //#f:1
  }

  @Override
  public String[] getAliases() {
    return new String[] { "roll" };
  }

  private static void printResult(RollTypes type, RollResult res) {
    Player player = res.getPlayer();

    String line1 = "";

    switch (type) {
    case ATTACK: {
      AttackRollResult result = (AttackRollResult) res;
      String displayName = result.getSkill().getDisplayName();

      String weaponString;
      switch (result.getSkill().getName()) {
      case "arts_martiaux":
        weaponString = "aux arts martiaux";
        break;
      case "pugilat":
        weaponString = "aux poings";
        break;
      case "lutte":
        weaponString = "à la lutte";
        break;
      case "sabre":
      case "baton":
        weaponString = "au " + displayName;
        break;
      case "arc":
      case "epee_courte":
      case "epee_a_deux_mains":
      case "attaque_innee":
        weaponString = "à l'" + displayName;
        break;
      default:
        weaponString = "à la " + displayName;
        break;
      }

      line1 = String.format("%s attaque %s, score de %d" + result.getModifierString(), McFrPlayer.getMcFrPlayer(player).getName(), weaponString,
          result.getScore());
    }
      break;
    case ATTRIBUTE: {
      AttributeRollResult result = (AttributeRollResult) res;

      line1 = String.format("%s fait un jet de %s, score de %d" + result.getModifierString(), McFrPlayer.getMcFrPlayer(player).getName(),
          result.getAttribute().getName(), result.getScore());
    }
      break;
    case DEFENSE: {
      DefenseRollResult result = (DefenseRollResult) res;

      String defenseString = "";
      switch (result.getDefense().toString()) {
      case "PARADE":
        defenseString = "une parade";
        break;
      case "ESQUIVE":
        defenseString = "d'esquiver";
        break;
      case "BLOCAGE":
        defenseString = "de bloquer au bouclier";
        break;
      }

      line1 = String.format("%s tente %s, score de %d" + result.getModifierString(), McFrPlayer.getMcFrPlayer(player).getName(), defenseString,
          result.getScore());
    }
      break;
    case PERCEPTION: {
      PerceptionRollResult result = (PerceptionRollResult) res;

      line1 = String.format("%s fait un jet de %s, score de %d" + result.getModifierString(), McFrPlayer.getMcFrPlayer(player).getName(),
          result.getSense(), result.getScore());
    }
      break;
    case RESISTANCE: {
      ResistanceRollResult result = (ResistanceRollResult) res;

      line1 = String.format("%s fait un jet d'encaissement, score de %d" + result.getModifierString(), McFrPlayer.getMcFrPlayer(player).getName(),
          result.getScore());
    }
      break;
    case SKILL: {
      SkillRollResult result = (SkillRollResult) res;

      String skillDisplayName = result.getSkill().getDisplayName();
      char firstLetter = skillDisplayName.charAt(0);
      if (firstLetter == 'a' || firstLetter == 'e' || firstLetter == 'i' || firstLetter == 'o' || firstLetter == 'u' || firstLetter == 'é'
          || firstLetter == 'à' || firstLetter == 'è') {
        skillDisplayName = "d'" + skillDisplayName;
      } else {
        skillDisplayName = "de " + skillDisplayName;
      }

      String alternateAttribute = result.getSkill().getAttribute().equals(result.getAttribute()) ? "" : " (" + result.getAttribute().getName() + ") ";
      
      line1 = String.format("%s fait un jet %s, score de %d" + result.getModifierString() + alternateAttribute, McFrPlayer.getMcFrPlayer(player).getName(),
          skillDisplayName, result.getScore());
    }
      break;
    }

    Text text1 = Text.of(YELLOW, line1);
    Text text2 = Text.of(YELLOW, String.format("Résultat : %d -> %s avec une marge de %d", res.getRoll(), res.getResult(), res.getMargin()));
    Sponge.getServer().getOnlinePlayers().stream().filter(p -> McFrPlayer.distance(player, p) < 20).forEach(p -> {
      p.sendMessage(text1);
      p.sendMessage(text2);
    });
    // TODO : rajouter un argument optionnel de portée. Il s'agirait d'une
    // chaine de caractère correspondant à un niveau de chat.
  }

  static class Skill extends AbstractCommand {

    public Skill(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      if (src instanceof Player) {
        if (args.hasAny("compétence")) {
          Skills skill = args.<Skills>getOne("compétence").get();
          printResult(RollTypes.SKILL, Sponge.getServiceManager().provide(RolePlayService.class).get().skillRoll((Player) src,
              skill, args.<Integer>getOne("modificateur").orElse(0), args.<Attributes>getOne("attribut")));
        } else {
          src.sendMessage(Text.of(TextColors.YELLOW, "------------------------ ROLL C ------------------------\n"
              + "Jet de compétence :\n"
              + "/roll c [compétence] pour lancer un jet direct.\n"
              + "   Exemple : /roll c chasse\n"
              + "/roll c [compétence] [modificateur] pour lancer un jet modifié.\n"
              + "   Exemple : /roll c chasse -2\n"
              + "/roll c [compétence] [modificateur] [attribut] pour lancer un jet modifié sur un attribut.\n"
              + "   Exemple : /roll c chasse -2 FORCE\n"
              + "N'hésitez pas à utiliser la touche TAB pour avoir les différentes valeurs possibles.\n"
              + "--------------------------------------------------------------"));
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
              .description(Text.of("Fait un jet de compétence."))
              .permission("essentials.command.roll.skill")
              .executor(this)
              .arguments(GenericArguments.optional(GenericArguments.choices(Text.of("compétence"), Skills.getSkills())),
                         GenericArguments.optional(GenericArguments.integer(Text.of("modificateur"))),
                         GenericArguments.optional(GenericArguments.enumValue(Text.of("attribut"), Attributes.class)))
              .build();
      //#f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "c" };
    }

  }

  static class Attribute extends AbstractCommand {

    public Attribute(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      if (src instanceof Player) {
        if (args.hasAny("attribut")) {
          printResult(RollTypes.ATTRIBUTE, Sponge.getServiceManager().provide(RolePlayService.class).get().attributeRoll((Player) src,
              args.<Attributes>getOne("attribut").get(), args.<Integer>getOne("modificateur").orElse(0)));          
        } else {
          src.sendMessage(Text.of(TextColors.YELLOW, "------------------------ ROLL AT -----------------------\n"
              + "Jet d'attribut :\n"
              + "/roll at FORCE/DEXTERITE/INTELLECT/ENDURANCE\n"
              + "Vous pouvez ajouter un modificateur.\n"
              + "   Exemple : /roll at DEXTERITE -5\n"
              + "N'hésitez pas à utiliser la touche TAB pour avoir les différentes valeurs possibles.\n"
              + "--------------------------------------------------------------"));
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
              .description(Text.of("Fait un jet d'attribut."))
              .permission("essentials.command.roll.attribute")
              .executor(this)
              .arguments(GenericArguments.optional(GenericArguments.enumValue(Text.of("attribut"), Attributes.class)),
                         GenericArguments.optional(GenericArguments.integer(Text.of("modificateur"))))
              .build();
      //#f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "at" };
    }

  }

  static class Resistance extends AbstractCommand {

    public Resistance(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      if (src instanceof Player) {
        printResult(RollTypes.RESISTANCE, Sponge.getServiceManager().provide(RolePlayService.class).get().resistanceRoll((Player) src,
            args.<Integer>getOne("modificateur").orElse(0)));

      } else {
        src.sendMessage(ONLY_PLAYERS_COMMAND);
      }
      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      //#f:0
      return CommandSpec.builder()
              .description(Text.of("Fait un jet d'encaissement."))
              .permission("essentials.command.roll.resistance")
              .executor(this)
              .arguments(GenericArguments.optional(GenericArguments.integer(Text.of("modificateur"))))
              .build();
      //#f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "e" };
    }

  }

  static class Perception extends AbstractCommand {
    public Perception(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      if (src instanceof Player) {
        if (args.hasAny("sens")) {
          printResult(RollTypes.PERCEPTION, Sponge.getServiceManager().provide(RolePlayService.class).get().perceptionRoll((Player) src,
              args.<Senses>getOne("sens").get(), args.<Integer>getOne("modificateur").orElse(0)));        
        } else {
          src.sendMessage(Text.of(TextColors.YELLOW, "------------------------ ROLL P ------------------------\n"
              + "Jet de perception :\n"
              + "/roll p OUIE/VUE/ODORAT/GOUT/TOUCHER\n"
              + "Vous pouvez ajouter un modificateur.\n"
              + "   Exemple : /roll p VUE -5\n"
              + "N'hésitez pas à utiliser la touche TAB pour avoir les différentes valeurs possibles.\n"
              + "--------------------------------------------------------------"));
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
            .description(Text.of("Fait un jet de perception."))
            .permission("essentials.command.roll.perception")
            .executor(this)
            .arguments(GenericArguments.optional(GenericArguments.enumValue(Text.of("sens"), Senses.class)),
                GenericArguments.optional(GenericArguments.integer(Text.of("modificateur"))))
            .build();
    //#f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "p" };
    }

  }

  static class Attack extends AbstractCommand {

    public Attack(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      if (src instanceof Player) {
        printResult(RollTypes.ATTACK,
            Sponge.getServiceManager().provide(RolePlayService.class).get().attackRoll((Player) src, args.<Integer>getOne("modificateur").orElse(0), args.<Skills>getOne("compétence")));
      } else {
        src.sendMessage(ONLY_PLAYERS_COMMAND);
      }
      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      //#f:0
      return CommandSpec.builder()
              .description(Text.of("Fait un jet d'attaque."))
              .permission("essentials.command.roll.attack")
              .executor(this)
              .arguments(GenericArguments.optional(GenericArguments.integer(Text.of("modificateur"))),
                  GenericArguments.optional(GenericArguments.choices(Text.of("compétence"), Skills.getCombatSkills())))
              .build();
      //#f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "a" };
    }

  }

  static class Defense extends AbstractCommand {
    public Defense(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      if (src instanceof Player) {        
        if (args.hasAny("type")) {
          Defenses type = args.<Defenses>getOne("type").get();
          if (type.equals(Defenses.PARADE) && args.hasAny("compétence")) {
            printResult(RollTypes.DEFENSE, Sponge.getServiceManager().provide(RolePlayService.class).get().defenseRoll((Player) src,
                args.<Defenses>getOne("type").get(), args.<Integer>getOne("modificateur").orElse(0), Optional.of(args.<Skills>getOne("compétence").get())));
          } else {
            printResult(RollTypes.DEFENSE, Sponge.getServiceManager().provide(RolePlayService.class).get().defenseRoll((Player) src,
                args.<Defenses>getOne("type").get(), args.<Integer>getOne("modificateur").orElse(0), Optional.empty()));
          }
        } else {
          src.sendMessage(Text.of(TextColors.YELLOW, "------------------------ ROLL D ------------------------\n"
              + "Jet de défense :\n"
              + "/roll d ESQUIVE/PARADE/BLOCAGE\n"
              + "Vous pouvez ajouter un modificateur.\n"
              + "   Exemple : /roll d ESQUIVE -5\n"
              + "N'hésitez pas à utiliser la touche TAB pour avoir les différentes valeurs possibles.\n"
              + "--------------------------------------------------------------"));
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
          .description(Text.of("Fait un jet de défense."))
          .permission("essentials.command.roll.defense")
          .executor(this)
          .arguments(GenericArguments.optional(GenericArguments.enumValue(Text.of("type"), Defenses.class)),
              GenericArguments.optional(GenericArguments.integer(Text.of("modificateur"))),
              GenericArguments.optional(GenericArguments.choices(Text.of("compétence"), Skills.getCombatSkills())))
          .build();
  //#f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "d" };
    }

  }

  static class None extends AbstractCommand {
    public None(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      int faces = args.<Integer>getOne("faces").orElse(6);
      int nbre = args.<Integer>getOne("nombre").orElse(1);
      src.sendMessage(Text.of(TextColors.YELLOW,
          "Jet de " + nbre + "D" + faces + " : " + Sponge.getServiceManager().provide(RolePlayService.class).get().rollDice(nbre, faces)));
      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
    //#f:0
    return CommandSpec.builder()
            .description(Text.of("Fait un jet sans attribut ni compétence."))
            .permission("essentials.command.roll.none")
            .arguments(GenericArguments.optional(GenericArguments.integer(Text.of("faces"))),
                GenericArguments.optional(GenericArguments.integer(Text.of("nombre"))))
            .executor(this)
            .build();
    //#f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "n" };
    }
  }
}