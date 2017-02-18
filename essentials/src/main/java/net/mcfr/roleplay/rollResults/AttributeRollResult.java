package net.mcfr.roleplay.rollResults;

import org.spongepowered.api.entity.living.player.Player;

import net.mcfr.roleplay.Attributes;

public class AttributeRollResult extends RollResult {
  private Attributes attribute;
  
  public AttributeRollResult(Player player, Attributes attribute, int modifier, int roll, int score, int margin) {
    super(player, modifier, roll, score, margin);
    this.attribute = attribute;
  }
  
  public Attributes getAttribute() {
    return this.attribute;
  }
}