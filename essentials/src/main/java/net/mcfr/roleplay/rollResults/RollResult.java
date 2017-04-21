package net.mcfr.roleplay.rollResults;

import org.spongepowered.api.entity.living.player.Player;

import net.mcfr.roleplay.Result;

public abstract class RollResult {
  private Player player;
  private Result result;
  private int roll;
  private int modifier;
  private int score;
  private int margin;

  public RollResult(Player player, int modifier, int roll, int score, int margin) {
    this.player = player;
    this.modifier = modifier;
    this.result = Result.getResult(roll, margin);
    this.roll = roll;
    this.score = score;
    this.margin = margin;
  }

  public Player getPlayer() {
    return this.player;
  }

  public Result getResult() {
    return this.result;
  }

  public int getModifier() {
    return this.modifier;
  }

  public String getModifierString() {
    if (this.modifier > 0)
      return " (+" + this.modifier + ")";
    else if (this.modifier < 0)
      return " (" + this.modifier + ")";
    else
      return "";
  }

  public int getScore() {
    return this.score;
  }

  public int getMargin() {
    return this.margin;
  }

  public int getRoll() {
    return this.roll;
  }

}
