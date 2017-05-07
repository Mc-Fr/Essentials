package net.mcfr.roleplay;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.spongepowered.api.entity.living.player.Player;

import net.mcfr.chat.Language;
import net.mcfr.roleplay.rollResults.AttackRollResult;
import net.mcfr.roleplay.rollResults.AttributeRollResult;
import net.mcfr.roleplay.rollResults.DefenseRollResult;
import net.mcfr.roleplay.rollResults.PerceptionRollResult;
import net.mcfr.roleplay.rollResults.ResistanceRollResult;
import net.mcfr.roleplay.rollResults.SkillRollResult;
import net.mcfr.utils.McFrPlayer;

public class RolePlayImp implements RolePlayService {
  private static Map<String, Object> rollEntries = new HashMap<>();
  
  private Random rd;

  public RolePlayImp() {
    this.rd = new Random();
    Skill.loadFromDatabase();
    Language.loadFromDatabase();
    
    rollEntries.putAll(Skill.getSkills());
    rollEntries.put("att", "attack");
    rollEntries.put("sort", "spell");
    for (Attribute att : Attribute.values())
      rollEntries.put(att.getName(), att);
    for (Sense sense : Sense.values())
      rollEntries.put(sense.name(), sense);
    for (Defense defense : Defense.values())
      rollEntries.put(defense.name(), defense);
    
  }

  public static Map<String, Object> getRollEntries() {
    return rollEntries;
  }
  
  @Override
  public SkillRollResult skillRoll(Player player, Skill skill, int modifier, Optional<Attribute> optAttribute) {
    int roll = rollDice(3, 6);
    McFrPlayer mcFrPlayer = McFrPlayer.getMcFrPlayer(player);
    Attribute attribute = optAttribute.isPresent() ? optAttribute.get() : skill.getAttribute();
    modifier += mcFrPlayer.getHealthState().getMalus(attribute);
    int score = mcFrPlayer.getSkillLevel(skill, optAttribute) + modifier;

    switch (skill.getName()) {
    case "escalade":
    case "evasion":
      score += mcFrPlayer.hasTrait("souplesse") ? 3 : 0;
      break;
    case "force_mentale":
      score += mcFrPlayer.getTraitLevel("resistance_a_la_magie");
      score -= mcFrPlayer.getTraitLevel("sensibilite_accrue_a_la_magie");
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
      score += mcFrPlayer.hasTrait("tres_bonne_memoire") ? 1 : 0;
      break;
    case "meditation":
      score += mcFrPlayer.hasTrait("concentration_accrue") ? 1 : 0;
      break;
    }

    if (skill.getAttribute() == Attribute.DEXTERITE) {
      score -= mcFrPlayer.hasTrait("desordre_neurologique_spasmes_legers") ? 2 : 0;
      score -= mcFrPlayer.hasTrait("desordre_neurologique_spasmes_severes") ? 4 : 0;
    }

    int margin = score - roll;
    return new SkillRollResult(player, skill, attribute, modifier, roll, score, margin);
  }

  @Override
  public AttributeRollResult attributeRoll(Player player, Attribute attribute, int modifier) {
    int roll = rollDice(3, 6);
    modifier += McFrPlayer.getMcFrPlayer(player).getHealthState().getMalus(attribute);
    int score = McFrPlayer.getMcFrPlayer(player).getAttributePoints(attribute) + modifier;

    int margin = score - roll;
    return new AttributeRollResult(player, attribute, modifier, roll, score, margin);
  }

  @Override
  public ResistanceRollResult resistanceRoll(Player player, int modifier) {
    int roll = rollDice(3, 6);
    int armorModifier = McFrPlayer.getMcFrPlayer(player).getArmorModifier();
    int endModifier = (McFrPlayer.getMcFrPlayer(player).getAttributePoints(Attribute.ENDURANCE) - 10) / 2;
    modifier += McFrPlayer.getMcFrPlayer(player).getHealthState().getMalus(Attribute.ENDURANCE);
    int score = 10 + endModifier + armorModifier + modifier;

    score += McFrPlayer.getMcFrPlayer(player).hasTrait("armure_naturelle") ? 1 : 0;

    int margin = score - roll;
    return new ResistanceRollResult(player, modifier, armorModifier, roll, score, margin);
  }

  @Override
  public PerceptionRollResult perceptionRoll(Player player, Sense sense, int modifier) {
    int roll = rollDice(3, 6);
    McFrPlayer mcFrPlayer = McFrPlayer.getMcFrPlayer(player);
    int score = mcFrPlayer.getAttributePoints(Attribute.PERCEPTION);

    switch (sense) {
    case VISION:
      score += mcFrPlayer.hasTrait("vue_elfe") ? 1 : 0;
      score -= mcFrPlayer.hasTrait("mauvaise_vue_sans_lunettes") ? 4 : 0;
      score -= mcFrPlayer.hasTrait("pas_de_vision_de_profondeur") ? 2 : 0;
      score -= mcFrPlayer.hasTrait("un_seul_oeil") ? 5 : 0;
      break;
    case OUIE:
      score += mcFrPlayer.hasTrait("ouie_amelioree_legere") ? 1 : 0;
      score += mcFrPlayer.hasTrait("ouie_amelioree_moyenne") ? 2 : 0;
      score += mcFrPlayer.hasTrait("ouie_amelioree_complete") ? 3 : 0;
      score -= mcFrPlayer.hasTrait("dur_de_la_feuille") ? 4 : 0;
      break;
    case GOUT:
    case ODORAT:
      score += mcFrPlayer.hasTrait("truffe_geroune") ? 1 : 0;
      score += mcFrPlayer.hasTrait("truffe_sheonne") ? 6 : 0;
      break;
    case TOUCHER:
      break;
    }

    modifier += mcFrPlayer.getHealthState().getMalus(Attribute.INTELLECT);
    score += modifier;

    int margin = score - roll;
    return new PerceptionRollResult(player, sense, modifier, roll, score, margin);
  }

  @Override
  public AttackRollResult attackRoll(Player player, int modifier) {
    int roll = rollDice(3, 6);

    Skill attackSkill = Skill.getWeaponSkill(player);
    McFrPlayer mcFrPlayer = McFrPlayer.getMcFrPlayer(player);

    modifier += mcFrPlayer.getHealthState().getMalus(attackSkill.getAttribute());
    int score = mcFrPlayer.getSkillLevel(attackSkill, Optional.empty()) + modifier;
    int margin = score - roll;
    return new AttackRollResult(player, attackSkill, attackSkill.getAttribute(), modifier, roll, score, margin);
  }

  @Override
  public DefenseRollResult defenseRoll(Player player, Defense defense, int modifier, Optional<Skill> optSkill) {
    int roll = rollDice(3, 6);
    int score = 0;
    McFrPlayer mcFrPlayer = McFrPlayer.getMcFrPlayer(player);

    switch (defense) {
    case BLOCAGE:
      Skill shieldSkill = Skill.getSkills().get("bouclier");
      optSkill = Optional.of(shieldSkill);
      score = mcFrPlayer.getSkillLevel(shieldSkill, Optional.empty());
      score /= 2;
      score += 3;
      break;
    case ESQUIVE:
      optSkill = Optional.empty();
      score = mcFrPlayer.getAttributePoints(Attribute.DEXTERITE) - 4;
      score += mcFrPlayer.hasTrait("esquive_amelioree") ? 1 : 0;
      break;
    case PARADE:
      Skill weaponSkill = Skill.getWeaponSkill(player);
      if (optSkill.isPresent()) {
        weaponSkill = optSkill.get();
      } else {
        optSkill = Optional.of(weaponSkill);
      }
      score = mcFrPlayer.getSkillLevel(weaponSkill, Optional.empty());
      score /= 2;
      score += 3;
      if ((weaponSkill.getName().equals("pugilat") || weaponSkill.getName().equals("lutte") || weaponSkill.getName().equals("arts_martiaux") || weaponSkill.getName().equals("attaque_innee"))
          && mcFrPlayer.hasTrait("parade_a_mains_nues_amelioree")) {
        score += 1;
      } else if (mcFrPlayer.hasTrait("parade_amelioree")) {
        score += 1;
      }
      break;
    }

    modifier += mcFrPlayer.getHealthState().getMalus(Attribute.DEXTERITE);
    score += modifier;
    score += mcFrPlayer.hasTrait("reflexes_de_combat") ? 1 : 0;

    int margin = score - roll;
    return new DefenseRollResult(player, defense, optSkill, modifier, roll, score, margin);
  }

  @Override
  public int rollDice(int times, int faces) {
    int value = 0;
    for (int i = 0; i < times; i++) {
      value += rollDie(faces);
    }
    return value;
  }

  @Override
  public int rollDie(int faces) {
    return this.rd.nextInt(faces) + 1;
  }
}
