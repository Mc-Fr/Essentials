package net.mcfr.roleplay.fight;

import net.mcfr.utils.McFrPlayer;

public class Fighter {
  private McFrPlayer player;
  private int injuries;
  
  public Fighter(McFrPlayer player) {
    this.player = player;
    this.injuries = 0;
  }
  
  public void inflictInjury(int injury) {
    this.injuries += injury;
  }
  
  public int getInjuries() {
    return this.injuries;
  }
  
  public McFrPlayer getPlayer() {
    return this.player;
  }
}
