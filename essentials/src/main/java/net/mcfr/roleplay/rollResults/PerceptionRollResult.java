package net.mcfr.roleplay.rollResults;

import org.spongepowered.api.entity.living.player.Player;

import net.mcfr.roleplay.Sense;

public class PerceptionRollResult extends RollResult {
  private Sense sense;
  
  public PerceptionRollResult(Player player, Sense sense, int modifier, int roll, int score, int margin) {
    super(player, modifier, roll, score, margin);
    this.sense = sense;
  }
  
  public Sense getSense() {
    return this.sense;
  }
  
}
