package net.mcfr.roleplay;

import org.spongepowered.api.entity.living.player.Player;

import net.mcfr.roleplay.rollResults.RollResult;

public interface RolePlayService {
  
  RollResult skillRoll(Player player, Skills skill, int modifier);
  
  RollResult attributeRoll(Player player, Attributes attribute, int modifier);
  
  RollResult resistanceRoll(Player player, int modifier);
  
  RollResult perceptionRoll(Player player, Senses sense, int modifier);
  
  RollResult attackRoll(Player player, int modifier);
  
  RollResult defenseRoll(Player player, Defenses defense, int modifier);
  
  int rollDice(int faces);
}
