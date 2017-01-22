package net.mcfr.quests.questEngine.statements;

import static org.junit.Assert.*;

import org.junit.Test;

import net.mcfr.quests.questEngine.statements.StatementSay;

public class StatementSayTest extends StatementTest {

  @Test
  public void testSayOneHardCodedSentence() {
    new StatementSay("Bonjour !").execute(this.pData, this.qstObj);
    assertEquals("Bonjour !", this.qstObj.getSentences());
  }

  @Test
  public void testSayOneGenericSentenceWithOneVariable() {
    new StatementSay("Bonjour <displayName> !").execute(this.pData, this.qstObj);
    assertEquals("Bonjour Colin !", this.qstObj.getSentences());
  }

  @Test
  public void testSayOneGenericSentenceWithTwoVariables() {
    new StatementSay("<playerName> : Je suis <displayName> !").execute(this.pData, this.qstObj);
    assertEquals("Lepticed : Je suis Colin !", this.qstObj.getSentences());

  }

  @Test
  public void testSayTwoHardCodedSentences() {
    new StatementSay("Bonjour !").execute(this.pData, this.qstObj);
    new StatementSay("Au revoir !").execute(this.pData, this.qstObj);
    assertEquals("Bonjour !\nAu revoir !", this.qstObj.getSentences());
  }

  @Test
  public void testSayTwoGenericSentencesWithOneVariable() {
    new StatementSay("Bonjour <displayName> !").execute(this.pData, this.qstObj);
    new StatementSay("Au revoir <displayName> !").execute(this.pData, this.qstObj);
    assertEquals("Bonjour Colin !\nAu revoir Colin !", this.qstObj.getSentences());
  }

}
