package net.mcfr.quests.questEngine.statements;

import net.mcfr.quests.PlayerData;
import net.mcfr.quests.questEngine.QuestObject;

public class StatementExecute extends StatementWithString {
  
  public StatementExecute(String command) {
    super(command);
  }
  
  @Override
  public void execute(PlayerData playerData, QuestObject qstObj) {
    qstObj.addCommand(getFormattedParam(playerData));
  }
  
}
