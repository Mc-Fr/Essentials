package net.mcfr.roleplay.rollResults;

import java.util.Optional;

import org.spongepowered.api.entity.living.player.Player;

import net.mcfr.roleplay.Defenses;
import net.mcfr.roleplay.Skills;

public class DefenseRollResult extends RollResult {
  
  private Defenses defense;
  private Optional<Skills> skill;
  
  public DefenseRollResult(Player player, Defenses defense, Optional<Skills> optSskill, int modifier, int roll, int score, int margin) {
    super(player, modifier, roll, score, margin);
    this.defense = defense;
    this.skill = optSskill;
  }
  
  public Defenses getDefense() {
    return this.defense;
  }
  
  public Optional<Skills> getSkill() {
    return this.skill;
  }
}
