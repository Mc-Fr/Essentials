package net.mcfr.quests.questEngine.statements;

import net.mcfr.quests.PlayerData;
import net.mcfr.quests.questEngine.QuestObject;

public interface IStatement {
  
  void execute(PlayerData playerData, QuestObject qstObj);
  
}
