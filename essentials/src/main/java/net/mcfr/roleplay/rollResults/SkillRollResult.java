package net.mcfr.roleplay.rollResults;

import org.spongepowered.api.entity.living.player.Player;

import net.mcfr.roleplay.Attribute;
import net.mcfr.roleplay.Skill;

public class SkillRollResult extends AttributeRollResult {
  
  private Skill skill;
  
  public SkillRollResult(Player player, Skill skill, Attribute attribute, int modifier, int roll, int score, int margin) {
    super(player, attribute, modifier, roll, score, margin);
    this.skill = skill;
  }
  
  public Skill getSkill() {
    return this.skill;
  }
}
