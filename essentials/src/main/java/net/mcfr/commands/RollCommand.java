package net.mcfr.commands;

import static org.spongepowered.api.text.format.TextColors.*;

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
import net.mcfr.chat.ChatType;
import net.mcfr.commands.utils.AbstractCommand;
import net.mcfr.roleplay.Attribute;
import net.mcfr.roleplay.DamageCategory;
import net.mcfr.roleplay.Defense;
import net.mcfr.roleplay.RolePlayImp;
import net.mcfr.roleplay.RolePlayService;
import net.mcfr.roleplay.RollType;
import net.mcfr.roleplay.Sense;
import net.mcfr.roleplay.Skill;
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
    if (src instanceof Player) {
      Object rollEntry = args.getOne("type").get();
      Optional<Object> secondaryRollEntry = args.getOne("secondaire");
      int modifier = args.<Integer>getOne("modificateur").orElse(0);

      int range = 20;
      if (args.hasAny("portée")) {
        String rangeString = args.<String>getOne("portée").get();
        
        if (ChatType.rangeStrings.containsKey(rangeString)) {
          range = ChatType.rangeStrings.get(rangeString);
        } else if ((rangeString.length() == 2 && rangeString.matches("p[1-9]")) || (rangeString.length() == 3 && rangeString.matches("p[1-9][0-9]"))) {
          range = Integer.parseInt(rangeString.substring(1, rangeString.length()));
        } else {
          src.sendMessage(Text.of(TextColors.RED, "La portée doit être de la forme p10, ou p\", p', p&... Le nombre doit être entre 1 et 99."));
          return CommandResult.success();
        }
      }

      if (rollEntry instanceof Skill) {
        Optional<Attribute> optAttribute = Optional.empty();
        if (secondaryRollEntry.isPresent() && secondaryRollEntry.get() instanceof Attribute) {
          optAttribute = Optional.of((Attribute) secondaryRollEntry.get());
        }

        printResult(range, RollType.SKILL,
            Sponge.getServiceManager().provide(RolePlayService.class).get().skillRoll((Player) src, (Skill) rollEntry, modifier, optAttribute));

      } else if (rollEntry instanceof Attribute) {
        printResult(range, RollType.ATTRIBUTE,
            Sponge.getServiceManager().provide(RolePlayService.class).get().attributeRoll((Player) src, (Attribute) rollEntry, modifier));

      } else if (rollEntry instanceof Defense) {
        Optional<Skill> optSkill = Optional.empty();
        if (rollEntry.equals(Defense.PARADE) && secondaryRollEntry.isPresent() && secondaryRollEntry.get() instanceof Skill) {
          optSkill = Optional.of((Skill) secondaryRollEntry.get());
        }

        printResult(range, RollType.DEFENSE,
            Sponge.getServiceManager().provide(RolePlayService.class).get().defenseRoll((Player) src, (Defense) rollEntry, modifier, optSkill));

      } else if (rollEntry instanceof Sense) {
        printResult(range, RollType.PERCEPTION,
            Sponge.getServiceManager().provide(RolePlayService.class).get().perceptionRoll((Player) src, (Sense) rollEntry, modifier));

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
            .description(Text.of("Permet de faire des jets de dés."))
            .permission("essentials.command.roll")
            .arguments(GenericArguments.choices(Text.of("type"), RolePlayImp.getRollEntries()),
                GenericArguments.optionalWeak(GenericArguments.choices(Text.of("secondaire"), RolePlayImp.getSecondaryRollEntries())),
                GenericArguments.optionalWeak(GenericArguments.integer(Text.of("modificateur"))),
                GenericArguments.optionalWeak(GenericArguments.string(Text.of("portée"))))
            .executor(this)
            .children(getChildrenList(new None(getPlugin()),
                new Damage(getPlugin()),
                new Spell(getPlugin()),
                new Attack(getPlugin())))
            .build();
    //#f:1
  }

  @Override
  public String[] getAliases() {
    return new String[] { "roll" };
  }

  private static String getWeaponString(Skill skill) {
    String weaponString = " ";
    String displayName = skill.getDisplayName();
    switch (skill.getName()) {
    case "arts_martiaux":
      weaponString += "aux arts martiaux";
      break;
    case "pugilat":
      weaponString += "aux poings";
      break;
    case "lutte":
      weaponString += "à la lutte";
      break;
    case "armes_a_feu":
      weaponString += "à l'arme à feu";
      break;
    case "sabre":
    case "baton":
      weaponString += "au " + displayName;
      break;
    case "arc":
    case "epee_courte":
    case "epee_a_deux_mains":
    case "attaque_innee":
      weaponString += "à l'" + displayName;
      break;
    default:
      weaponString += "à la " + displayName;
      break;
    }
    return weaponString;
  }

  private static RollResult printResult(int range, RollType type, RollResult res) {
    Player player = res.getPlayer();

    String line1 = "";

    switch (type) {
    case ATTACK: {
      AttackRollResult result = (AttackRollResult) res;

      String weaponString = getWeaponString(result.getSkill());

      line1 = String.format("%s attaque%s, score de %d" + result.getModifierString(), McFrPlayer.getMcFrPlayer(player).getName(), weaponString,
          result.getScore());
    }
      break;
    case ATTRIBUTE: {
      AttributeRollResult result = (AttributeRollResult) res;

      String attributeString = "de " + result.getAttribute().getName();
      if (result.getAttribute().equals(Attribute.INTELLECT)) {
        attributeString = "d'" + Attribute.INTELLECT.getName();
      }

      line1 = String.format("%s fait un jet " + attributeString + ", score de %d" + result.getModifierString(),
          McFrPlayer.getMcFrPlayer(player).getName(), result.getScore());
    }
      break;
    case DEFENSE: {
      DefenseRollResult result = (DefenseRollResult) res;

      String defenseString = "";
      switch (result.getDefense().toString()) {
      case "PARADE":
        defenseString = "une parade" + (result.getSkill().isPresent() ? getWeaponString(result.getSkill().get()) : "");
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

      String perceptionString = "";
      switch (result.getSense()) {
      case OUIE:
      case ODORAT:
        perceptionString = "d'";
        break;
      default:
        perceptionString = "de ";
        break;
      }
      perceptionString += result.getSense().getName();

      line1 = String.format("%s fait un jet " + perceptionString + ", score de %d" + result.getModifierString(),
          McFrPlayer.getMcFrPlayer(player).getName(), result.getScore());
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

      line1 = String.format("%s fait un jet %s, score de %d" + result.getModifierString() + alternateAttribute,
          McFrPlayer.getMcFrPlayer(player).getName(), skillDisplayName, result.getScore());
    }
      break;
    }

    Text text1 = Text.of(YELLOW, line1);
    Text text2 = Text.of(YELLOW, String.format("Résultat : %d -> %s avec une marge de %d", res.getRoll(), res.getResult(), res.getMargin()));
    Sponge.getServer().getOnlinePlayers().stream().filter(p -> McFrPlayer.distance(player, p) < range).forEach(p -> {
      p.sendMessage(text1);
      p.sendMessage(text2);
    });

    return res;
  }

  static class None extends AbstractCommand {
    public None(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      if (src instanceof Player) {
        int faces = args.<Integer>getOne("faces").orElse(6);
        int nbre = args.<Integer>getOne("nombre").orElse(1);
        int result = Sponge.getServiceManager().provide(RolePlayService.class).get().rollDice(nbre, faces);

        Sponge.getServer().getOnlinePlayers().stream().filter(p -> McFrPlayer.distance((Player) src, p) < 20).forEach(p -> {
          src.sendMessage(
              Text.of(TextColors.YELLOW, McFrPlayer.getMcFrPlayer((Player) src).getName() + " lance " + nbre + "D" + faces + " : " + result));
        });
      } else {
        src.sendMessage(ONLY_PLAYERS_COMMAND);
      }

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

  static class Damage extends AbstractCommand {
    public Damage(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      if (src instanceof Player) {
        int range = 20;
        if (args.hasAny("portée")) {
          String rangeString = args.<String>getOne("portée").get();
          
          if (ChatType.rangeStrings.containsKey(rangeString)) {
            range = ChatType.rangeStrings.get(rangeString);
          } else if ((rangeString.length() == 2 && rangeString.matches("p[1-9]")) || (rangeString.length() == 3 && rangeString.matches("p[1-9][0-9]"))) {
            range = Integer.parseInt(rangeString.substring(1, rangeString.length()));
          } else {
            src.sendMessage(Text.of(TextColors.RED, "La portée doit être de la forme p10, ou p\", p', p&... Le nombre doit être entre 1 et 99."));
            return CommandResult.success();
          }
        }
        final int finalRange = range;
        
        int strenght = args.hasAny("force") ? args.<Integer>getOne("force").get()
            : McFrPlayer.getMcFrPlayer((Player) src).getAttributePoints(Attribute.FORCE);
        DamageCategory category = args.<DamageCategory>getOne("catégorie").get();
        int dies = category.getDies(strenght);
        int firstBonus = category.getBonus(strenght);
        int secondBonus = args.<Integer>getOne("bonus").orElse(0);

        int result = Sponge.getServiceManager().provide(RolePlayService.class).get().rollDice(dies, 6) + firstBonus + secondBonus;

        Sponge.getServer().getOnlinePlayers().stream().filter(p -> McFrPlayer.distance((Player) src, p) < finalRange).forEach(p -> {
          String roll = dies + "D6";
          if (firstBonus > 0) {
            roll += "+" + firstBonus;
          } else if (firstBonus < 0) {
            roll += "-" + (-firstBonus);
          }
          roll += " (";
          if (secondBonus > 0) {
            roll += "+" + secondBonus + ", ";
          } else if (secondBonus < 0) {
            roll += "-" + (-secondBonus) + ", ";
          }
          roll += category + ")";
          p.sendMessage(Text.of(TextColors.YELLOW, McFrPlayer.getMcFrPlayer((Player) src).getName() + " fait " + roll + " de dégâts : " + result));
        });
      } else {
        src.sendMessage(ONLY_PLAYERS_COMMAND);
      }

      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
    //#f:0
    return CommandSpec.builder()
            .description(Text.of("Fait un jet de dégâts."))
            .permission("essentials.command.roll.damage")
            .arguments(GenericArguments.optionalWeak(GenericArguments.integer(Text.of("force"))),
                GenericArguments.enumValue(Text.of("catégorie"), DamageCategory.class),
                GenericArguments.optionalWeak(GenericArguments.integer(Text.of("bonus"))),
                GenericArguments.optionalWeak(GenericArguments.string(Text.of("portée"))))
            .executor(this)
            .build();
    //#f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "deg" };
    }
  }

  static class Attack extends AbstractCommand {
    public Attack(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      if (src instanceof Player) {
        int range = 20;
        if (args.hasAny("portée")) {
          String rangeString = args.<String>getOne("portée").get();
          
          if (ChatType.rangeStrings.containsKey(rangeString)) {
            range = ChatType.rangeStrings.get(rangeString);
          } else if ((rangeString.length() == 2 && rangeString.matches("p[1-9]")) || (rangeString.length() == 3 && rangeString.matches("p[1-9][0-9]"))) {
            range = Integer.parseInt(rangeString.substring(1, rangeString.length()));
          } else {
            src.sendMessage(Text.of(TextColors.RED, "La portée doit être de la forme p10, ou p\", p', p&... Le nombre doit être entre 1 et 99."));
            return CommandResult.success();
          }
        }
        
        printResult(range, RollType.ATTACK,
            Sponge.getServiceManager().provide(RolePlayService.class).get().attackRoll((Player) src, args.<Integer>getOne("modificateur").orElse(0)));
      } else {
        src.sendMessage(ONLY_PLAYERS_COMMAND);
      }

      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
    //#f:0
    return CommandSpec.builder()
            .description(Text.of("Fait un jet d'attaque avec l'arme portée en main."))
            .permission("essentials.command.roll.attack")
            .arguments(GenericArguments.optionalWeak(GenericArguments.integer(Text.of("modificateur"))),
                GenericArguments.optionalWeak(GenericArguments.string(Text.of("portée"))))
            .executor(this)
            .build();
    //#f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "a" };
    }
  }

  static class Spell extends AbstractCommand {
    public Spell(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      if (src instanceof Player) {
        int range = 20;
        if (args.hasAny("portée")) {
          String rangeString = args.<String>getOne("portée").get();
          
          if (ChatType.rangeStrings.containsKey(rangeString)) {
            range = ChatType.rangeStrings.get(rangeString);
          } else if ((rangeString.length() == 2 && rangeString.matches("p[1-9]")) || (rangeString.length() == 3 && rangeString.matches("p[1-9][0-9]"))) {
            range = Integer.parseInt(rangeString.substring(1, rangeString.length()));
          } else {
            src.sendMessage(Text.of(TextColors.RED, "La portée doit être de la forme p10, ou p\", p', p&... Le nombre doit être entre 1 et 99."));
            return CommandResult.success();
          }
        }
        
        int mana = args.<Integer>getOne("palier").get();

        RollResult res = printResult(range, RollType.SKILL,
            Sponge.getServiceManager().provide(RolePlayService.class).get().skillRoll((Player) src, Skill.getSkillByName("thaumatologie"),
                args.<Integer>getOne("modificateur").orElse(0), Optional.empty()));

        switch (res.getResult()) {
        case CRITICAL_SUCCESS:
          mana /= Math.ceil(1f * mana / 3f);
          break;
        case FAILURE:
          mana = 1;
          break;
        default:
          break;
        }

        Sponge.getCommandManager().process(src, "mana -" + mana);
      } else {
        src.sendMessage(ONLY_PLAYERS_COMMAND);
      }

      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
    //#f:0
    return CommandSpec.builder()
            .description(Text.of("Permet de tenter de lancer un sort."))
            .permission("essentials.command.roll.spell")
            .arguments(GenericArguments.integer(Text.of("palier")),
                GenericArguments.optionalWeak(GenericArguments.integer(Text.of("modificateur"))),
                GenericArguments.optionalWeak(GenericArguments.string(Text.of("portée"))))
            .executor(this)
            .build();
    //#f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "spell" };
    }
  }
}