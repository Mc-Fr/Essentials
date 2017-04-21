package net.mcfr.roleplay.rollResults;

import org.spongepowered.api.entity.living.player.Player;

import net.mcfr.roleplay.Attribute;

public class AttributeRollResult extends RollResult {
  private Attribute attribute;
  
  public AttributeRollResult(Player player, Attribute attribute, int modifier, int roll, int score, int margin) {
    super(player, modifier, roll, score, margin);
    this.attribute = attribute;
  }
  
  public Attribute getAttribute() {
    return this.attribute;
  }
}