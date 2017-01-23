package net.mcfr.roleplay;

import org.spongepowered.api.entity.living.player.Player;

import net.mcfr.roleplay.rollResults.AttackRollResult;
import net.mcfr.roleplay.rollResults.AttributeRollResult;
import net.mcfr.roleplay.rollResults.DefenseRollResult;
import net.mcfr.roleplay.rollResults.PerceptionRollResult;
import net.mcfr.roleplay.rollResults.ResistanceRollResult;
import net.mcfr.roleplay.rollResults.SkillRollResult;

public interface RolePlayService {

  SkillRollResult skillRoll(Player player, Skills skill, int modifier);

  AttributeRollResult attributeRoll(Player player, Attributes attribute, int modifier);

  ResistanceRollResult resistanceRoll(Player player, int modifier);

  PerceptionRollResult perceptionRoll(Player player, Senses sense, int modifier);

  AttackRollResult attackRoll(Player player, int modifier);

  DefenseRollResult defenseRoll(Player player, Defenses defense, int modifier);

  int rollDice(int faces);
}
