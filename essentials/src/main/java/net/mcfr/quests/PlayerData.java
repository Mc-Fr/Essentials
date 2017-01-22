package net.mcfr.quests;

public class PlayerData {

  private String playerName;
  private String displayName;

  public PlayerData(String playerName, String displayName, String... uselessForNow) {
    this.playerName = playerName;
    this.displayName = displayName;
  }

  public String getPlayerName() {
    return this.playerName;
  }

  public String getDisplayName() {
    return this.displayName;
  }

}
