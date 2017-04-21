package net.mcfr.roleplay.rollResults;

import java.util.Optional;

import org.spongepowered.api.entity.living.player.Player;

import net.mcfr.roleplay.Defense;
import net.mcfr.roleplay.Skill;

public class DefenseRollResult extends RollResult {
  
  private Defense defense;
  private Optional<Skill> skill;
  
  public DefenseRollResult(Player player, Defense defense, Optional<Skill> optSskill, int modifier, int roll, int score, int margin) {
    super(player, modifier, roll, score, margin);
    this.defense = defense;
    this.skill = optSskill;
  }
  
  public Defense getDefense() {
    return this.defense;
  }
  
  public Optional<Skill> getSkill() {
    return this.skill;
  }
}
