package net.mcfr.roleplay.rollResults;

import org.spongepowered.api.entity.living.player.Player;

public class ResistanceRollResult extends RollResult {
  
  private int armorModifier;
  
  public ResistanceRollResult(Player player, int modifier, int armorModifier, int roll, int score, int margin) {
    super(player, modifier, roll, score, margin);
    this.armorModifier = armorModifier;
  }
  
  public int getArmorModifier() {
    return this.armorModifier;
  }
  
}
