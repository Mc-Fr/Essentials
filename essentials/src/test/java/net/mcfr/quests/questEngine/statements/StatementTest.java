package net.mcfr.quests.questEngine.statements;

import org.junit.Before;

import net.mcfr.quests.PlayerData;
import net.mcfr.quests.questEngine.QuestObject;

public class StatementTest {
  protected QuestObject qstObj;
  protected PlayerData pData;
  
  @Before
  public void setUp() {
    this.pData = new PlayerData("Lepticed", "Colin");
    this.qstObj = new QuestObject();
  }
}
