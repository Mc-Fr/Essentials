package net.mcfr.quests.questEngine;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.mcfr.quests.questEngine.Interpreter;

public class InterpreterTest {
  
  @Test
  public void testOneEvent() {
    String script = "#intro PHRASE_INTRO \"Bonjour à vous !\"";
    Interpreter i = Interpreter.compile(script);
    assertEquals("[PHRASE_INTRO : \"Bonjour à vous !\"]", i.getEvents().toString());
  }
  
  @Test
  public void testTwoEvents() {
    // #f:0
    String script = "#intro PHRASE_INTRO \"Bonjour à vous !\"\n"
                  + "#intro PHRASE_SUITE \"Non.\"";
    // #f:1
    Interpreter i = Interpreter.compile(script);
    assertEquals("[PHRASE_INTRO : \"Bonjour à vous !\", PHRASE_SUITE : \"Non.\"]", i.getEvents().toString());
  }
  
}
