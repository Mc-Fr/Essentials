package net.mcfr.roleplay;

import java.util.Random;

import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;

import net.mcfr.babel.Language;
import net.mcfr.roleplay.rollResults.AttackRollResult;
import net.mcfr.roleplay.rollResults.AttributeRollResult;
import net.mcfr.roleplay.rollResults.DefenseRollResult;
import net.mcfr.roleplay.rollResults.PerceptionRollResult;
import net.mcfr.roleplay.rollResults.ResistanceRollResult;
import net.mcfr.roleplay.rollResults.SkillRollResult;
import net.mcfr.utils.McFrPlayer;

public class RolePlayImp implements RolePlayService {
  private Random rd;

  public RolePlayImp() {
    this.rd = new Random();
    Skills.loadFromDatabase();
    Language.loadFromDatabase();
  }

  @Override
  public SkillRollResult skillRoll(Player player, Skills skill, int modifier) {
    int roll = rollDice(3, 6);
    Attributes attribute = skill.getAttribute();
    int score = McFrPlayer.getMcFrPlayer(player).getAttributePoints(attribute) + McFrPlayer.getMcFrPlayer(player).getSkillLevel(skill)
        + skill.getDifficulty() + modifier;

    switch (skill.getName()) {
    case "escalade":
    case "arts_erotiques":
    case "evasion":
      score += McFrPlayer.getMcFrPlayer(player).hasTrait("souplesse") ? 3 : 0;
      break;
    case "force_mentale":
      score += McFrPlayer.getMcFrPlayer(player).getTraitLevel("resistance_a_la_magie");
      score -= McFrPlayer.getMcFrPlayer(player).getTraitLevel("sensibilite_accrue_a_la_magie");
      break;
    case "connaissance_de_callac":
    case "connaissance_de_dromorth":
    case "connaissance_de_la_loi":
    case "connaissance_de_l_ile_des_brumes":
    case "connaissance_de_l_ostalie":
    case "connaissance_de_menicea":
    case "connaissance_de_vays":
    case "connaissance_d_anskylvia":
    case "connaissance_d_heragan":
    case "connaissance_secrete":
    case "heraldique":
    case "herboristerie":
    case "alchimie":
    case "histoire":
    case "litterature":
    case "naturaliste":
    case "theologie":
      score += McFrPlayer.getMcFrPlayer(player).hasTrait("tres_bonne_memoire") ? 3 : 0;
      break;
    }

    if (skill.getAttribute() == Attributes.DEXTERITE) {
      score -= McFrPlayer.getMcFrPlayer(player).hasTrait("desordre_neurologique_spasmes_legers") ? 2 : 0;
      score -= McFrPlayer.getMcFrPlayer(player).hasTrait("desordre_neurologique_spasmes_severes") ? 4 : 0;
      score -= McFrPlayer.getMcFrPlayer(player).getTraitLevel("doigt_en_moins");
    }

    int margin = score - roll;
    return new SkillRollResult(player, skill, attribute, modifier, roll, score, margin);
  }

  @Override
  public AttributeRollResult attributeRoll(Player player, Attributes attribute, int modifier) {
    int roll = rollDice(3, 6);
    int score = McFrPlayer.getMcFrPlayer(player).getAttributePoints(attribute) + modifier;

    int margin = score - roll;
    return new AttributeRollResult(player, attribute, modifier, roll, score, margin);
  }

  @Override
  public ResistanceRollResult resistanceRoll(Player player, int modifier) {
    int roll = rollDice(3, 6);
    int armorModifier = McFrPlayer.getMcFrPlayer(player).getArmorModifier();
    int score = McFrPlayer.getMcFrPlayer(player).getAttributePoints(Attributes.ENDURANCE) + modifier + armorModifier;

    score += McFrPlayer.getMcFrPlayer(player).getTraitLevel("difficile_a_mettre_ko");
    score += McFrPlayer.getMcFrPlayer(player).getTraitLevel("armure_naturelle");

    int margin = score - roll;
    return new ResistanceRollResult(player, modifier, armorModifier, roll, score, margin);
  }

  @Override
  public PerceptionRollResult perceptionRoll(Player player, Senses sense, int modifier) {
    int roll = rollDice(3, 6);
    int score = McFrPlayer.getMcFrPlayer(player).getAttributePoints(Attributes.INTELLECT);

    switch (sense) {
    case VISION:
      score += McFrPlayer.getMcFrPlayer(player).getTraitLevel("vue_accentuee");
      score -= McFrPlayer.getMcFrPlayer(player).hasTrait("mauvaise_vue_sans_lunettes") ? 4 : 0;
      score -= McFrPlayer.getMcFrPlayer(player).hasTrait("pas_de_vision_de_profondeur") ? 2 : 0;
      score -= McFrPlayer.getMcFrPlayer(player).hasTrait("un_seul_oeil") ? 5 : 0;

      int lightMalus = McFrPlayer.getMcFrPlayer(player).getLightMalus();
      lightMalus += McFrPlayer.getMcFrPlayer(player).getTraitLevel("vision_dans_la_nuit");
      modifier += lightMalus < 0 ? lightMalus : 0;
      break;
    case OUIE:
      score += McFrPlayer.getMcFrPlayer(player).getTraitLevel("ouie_accentuee");
      score -= McFrPlayer.getMcFrPlayer(player).hasTrait("dur_de_la_feuille") ? 4 : 0;
      break;
    case GOUT:
    case ODORAT:
      score += McFrPlayer.getMcFrPlayer(player).getTraitLevel("gout_et_odorat_accentues");
      break;
    case TOUCHER:
      break;
    }

    score += modifier;

    int margin = score - roll;
    return new PerceptionRollResult(player, sense, modifier, roll, score, margin);
  }

  @Override
  public AttackRollResult attackRoll(Player player, int modifier) {
    int roll = rollDice(3, 6);

    Skills attackSkill = Skills.getWeaponSkill(player);
    McFrPlayer mcfrPlayer = McFrPlayer.getMcFrPlayer(player);
    int score = mcfrPlayer.getAttributePoints(attackSkill.getAttribute()) + mcfrPlayer.getSkillLevel(attackSkill) + attackSkill.getDifficulty()
        + modifier;

    int margin = score - roll;
    return new AttackRollResult(player, attackSkill, attackSkill.getAttribute(), modifier, roll, score, margin);
  }

  @Override
  public DefenseRollResult defenseRoll(Player player, Defenses defense, int modifier) {
    int roll = rollDice(3, 6);
    int score = 0;

    switch (defense) {
    case BLOCAGE:
      if (!player.getItemInHand(HandTypes.MAIN_HAND).equals(ItemTypes.SHIELD) && !player.getItemInHand(HandTypes.OFF_HAND).equals(ItemTypes.SHIELD))
        throw new IllegalStateException("Player can't block without shield!");
      Skills shieldSkill = Skills.getSkills().get("bouclier");
      score = McFrPlayer.getMcFrPlayer(player).getAttributePoints(Attributes.DEXTERITE) + McFrPlayer.getMcFrPlayer(player).getSkillLevel(shieldSkill)
          + shieldSkill.getDifficulty();
      score /= 2;
      score += 3;
      break;
    case ESQUIVE:
      score = McFrPlayer.getMcFrPlayer(player).getAttributePoints(Attributes.ENDURANCE)
          + McFrPlayer.getMcFrPlayer(player).getAttributePoints(Attributes.DEXTERITE);
      score /= 4;
      score += 3;
      score += McFrPlayer.getMcFrPlayer(player).hasTrait("esquive_amelioree") ? 1 : 0;
      break;
    case PARADE:
      Skills weaponSkill = Skills.getWeaponSkill(player);
      score = McFrPlayer.getMcFrPlayer(player).getAttributePoints(Attributes.DEXTERITE) + McFrPlayer.getMcFrPlayer(player).getSkillLevel(weaponSkill)
          + weaponSkill.getDifficulty();
      score /= 2;
      score += 3;
      if ((weaponSkill.getName().equals("pugilat") || weaponSkill.getName().equals("lutte"))
          && McFrPlayer.getMcFrPlayer(player).hasTrait("parade_a_mains_nues_amelioree")) {
        score += 1;
      } else if (McFrPlayer.getMcFrPlayer(player).hasTrait("parade_amelioree")) {
        score += 1;
      }
      break;
    }

    score += McFrPlayer.getMcFrPlayer(player).hasTrait("reflexes_de_combat") ? 1 : 0;

    int margin = score - roll;
    return new DefenseRollResult(player, defense, modifier, roll, score, margin);
  }

  public int rollDice(int times, int faces) {
    int value = 0;
    for (int i = 0; i < times; i++) {
      value += rollDie(faces);
    }
    return value;
  }

  @Override
  public int rollDie(int faces) {
    return this.rd.nextInt(faces - 1) + 1;
  }
}
