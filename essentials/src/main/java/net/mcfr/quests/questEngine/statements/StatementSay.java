package net.mcfr.quests.questEngine.statements;

import net.mcfr.quests.PlayerData;
import net.mcfr.quests.questEngine.QuestObject;

public class StatementSay extends StatementWithString {
  
  public StatementSay(String sentence) {
    super(sentence);
  }
  
  @Override
  public void execute(PlayerData playerData, QuestObject qstObj) {
    qstObj.appendSentence(getFormattedParam(playerData));
  }
  
}
