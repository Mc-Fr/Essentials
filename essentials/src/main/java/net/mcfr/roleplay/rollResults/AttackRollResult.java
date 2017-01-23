package net.mcfr.roleplay.rollResults;

import org.spongepowered.api.entity.living.player.Player;

import net.mcfr.roleplay.Skills;

public class AttackRollResult extends RollResult {

  public AttackRollResult(Player player, int modifier, int roll, int score, int margin) {
    super(player, modifier, roll, score, margin);
  }

  public String getWeaponName() {
    return Skills.getWeaponSkill(getPlayer()).getName();
  }

}
