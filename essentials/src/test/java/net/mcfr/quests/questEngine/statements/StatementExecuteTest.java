package net.mcfr.quests.questEngine.statements;

import static org.junit.Assert.*;

import org.junit.Test;

import net.mcfr.quests.questEngine.statements.StatementExecute;

public class StatementExecuteTest extends StatementTest {
  
  @Test
  public void testExecuteOneHardCodedCommand() {
    new StatementExecute("tp Lepticed 0 0 0").execute(this.pData, this.qstObj);
    assertEquals("[tp Lepticed 0 0 0]", this.qstObj.getCommands().toString());
  }
  
  @Test
  public void testExecuteOneGenericCommandWithOneVariable() {
    new StatementExecute("tp <playerName> 0 0 0").execute(this.pData, this.qstObj);
    assertEquals("[tp Lepticed 0 0 0]", this.qstObj.getCommands().toString());
  }
  
  @Test
  public void testExecuteOneGenericCommandWithTwoVariables() {
    new StatementExecute("tell <playerName> Bonjour <displayName> !").execute(this.pData, this.qstObj);
    assertEquals("[tell Lepticed Bonjour Colin !]", this.qstObj.getCommands().toString());
  }
  
  @Test
  public void testExecuteTwoHardCodedCommands() {
    new StatementExecute("tp Lepticed 0 0 0").execute(this.pData, this.qstObj);
    new StatementExecute("give Lepticed stone").execute(this.pData, this.qstObj);
    assertEquals("[tp Lepticed 0 0 0, give Lepticed stone]", this.qstObj.getCommands().toString());
  }
  
  @Test
  public void testExecuteTwoGenericCommands() {
    new StatementExecute("tp <playerName> 0 0 0").execute(this.pData, this.qstObj);
    new StatementExecute("give <playerName> stone").execute(this.pData, this.qstObj);
    assertEquals("[tp Lepticed 0 0 0, give Lepticed stone]", this.qstObj.getCommands().toString());
  }
}
