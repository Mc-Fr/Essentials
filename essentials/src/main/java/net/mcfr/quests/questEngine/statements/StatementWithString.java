package net.mcfr.quests.questEngine.statements;

import net.mcfr.quests.PlayerData;

public abstract class StatementWithString implements IStatement {

  private String param;

  public StatementWithString(String param) {
    this.param = param;
  }

  public String getParam() {
    return this.param;
  }

  protected String getFormattedParam(PlayerData playerData) {
    return getParam().replace("<playerName>", playerData.getPlayerName()).replace("<displayName>", playerData.getDisplayName());
  }

}
