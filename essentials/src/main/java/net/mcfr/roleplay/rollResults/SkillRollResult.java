package net.mcfr.roleplay.rollResults;

import org.spongepowered.api.entity.living.player.Player;

import net.mcfr.roleplay.Attributes;
import net.mcfr.roleplay.Skills;

public class SkillRollResult extends AttributeRollResult {
  
  private Skills skill;
  
  public SkillRollResult(Player player, Skills skill, Attributes attribute, int modifier, int roll, int score, int margin) {
    super(player, attribute, modifier, roll, score, margin);
    this.skill = skill;
  }
  
  public Skills getSkill() {
    return this.skill;
  }
}
