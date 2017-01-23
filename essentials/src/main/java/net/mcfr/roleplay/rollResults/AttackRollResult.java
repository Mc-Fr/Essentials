package net.mcfr.roleplay.rollResults;

import org.spongepowered.api.entity.living.player.Player;

import net.mcfr.roleplay.Attributes;
import net.mcfr.roleplay.Skills;

public class AttackRollResult extends SkillRollResult {

  public AttackRollResult(Player player, Skills skill, Attributes attribute, int modifier, int roll, int score, int margin) {
    super(player, skill, attribute, modifier, roll, score, margin);
  }

  public String getWeaponName() {
    return Skills.getWeaponSkill(getPlayer()).getName();
  }

}
