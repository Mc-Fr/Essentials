package net.mcfr.quests.questEngine;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.mcfr.quests.questEngine.QuestEvent;

public class QuestEventTest {
  
  @Test
  public void testToStringUpperCase() {
    QuestEvent qstEvt = new QuestEvent("PHRASE_INTRO", "Bonjour !");
    assertEquals("PHRASE_INTRO : \"Bonjour !\"", qstEvt.toString());
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testToStringLowerCase() {
    new QuestEvent("phrase_intro", "Bonjour !");
  }
  
}
