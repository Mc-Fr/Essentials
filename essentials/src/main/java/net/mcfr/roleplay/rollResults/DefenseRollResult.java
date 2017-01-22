package net.mcfr.roleplay.rollResults;

import org.spongepowered.api.entity.living.player.Player;

import net.mcfr.roleplay.Defenses;

public class DefenseRollResult extends RollResult {
  
  private Defenses defense;
  
  public DefenseRollResult(Player player, Defenses defense, int modifier, int roll, int score, int margin) {
    super(player, modifier, roll, score, margin);
    this.defense = defense;
  }
  
  public Defenses getDefense() {
    return this.defense;
  }
}
