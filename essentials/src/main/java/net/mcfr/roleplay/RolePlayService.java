package net.mcfr.roleplay;

import java.util.Optional;

import org.spongepowered.api.entity.living.player.Player;

import net.mcfr.roleplay.rollResults.RollResult;

public interface RolePlayService {
  
  RollResult skillRoll(Player player, Skill skill, int modifier, Optional<Attribute> optAttribute);

  RollResult attributeRoll(Player player, Attribute attribute, int modifier);

  RollResult perceptionRoll(Player player, Sense sense, int modifier);

  RollResult attackRoll(Player player, int modifier);

  RollResult defenseRoll(Player player, Defense defense, int modifier, Optional<Skill> optSkill);

  int rollDice(int times, int faces);

  int rollDie(int faces);
}
