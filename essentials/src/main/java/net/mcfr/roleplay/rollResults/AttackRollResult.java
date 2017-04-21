package net.mcfr.roleplay.rollResults;

import org.spongepowered.api.entity.living.player.Player;

import net.mcfr.roleplay.Attribute;
import net.mcfr.roleplay.Skill;

public class AttackRollResult extends SkillRollResult {

  public AttackRollResult(Player player, Skill skill, Attribute attribute, int modifier, int roll, int score, int margin) {
    super(player, skill, attribute, modifier, roll, score, margin);
  }
}
