package net.mcfr.quests.questEngine;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import net.mcfr.quests.questEngine.QuestObject;

public class QuestObjectTest {
  
  private QuestObject qstObj;
  
  @Before
  public void setUp() {
    this.qstObj = new QuestObject();
  }
  
  @Test
  public void testNoSentences() {
    assertEquals("", this.qstObj.getSentences());
  }
  
  @Test
  public void testOneSentence() {
    this.qstObj.appendSentence("Bonjour !");
    assertEquals("Bonjour !", this.qstObj.getSentences());
  }
  
  @Test
  public void testTwoSentences() {
    this.qstObj.appendSentence("Bonjour !");
    this.qstObj.appendSentence("Au revoir !");
    assertEquals("Bonjour !\nAu revoir !", this.qstObj.getSentences());
  }
  
}
