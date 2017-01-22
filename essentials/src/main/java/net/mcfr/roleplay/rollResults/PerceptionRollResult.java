package net.mcfr.roleplay.rollResults;

import org.spongepowered.api.entity.living.player.Player;

import net.mcfr.roleplay.Senses;

public class PerceptionRollResult extends RollResult {
  private Senses sense;
  
  public PerceptionRollResult(Player player, Senses sense, int modifier, int roll, int score, int margin) {
    super(player, modifier, roll, score, margin);
    this.sense = sense;
  }
  
  public String getSense() {
    return this.sense.name();
  }
  
}
