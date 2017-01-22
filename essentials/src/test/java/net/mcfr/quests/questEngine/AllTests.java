package net.mcfr.quests.questEngine;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import net.mcfr.quests.questEngine.statements.StatementSuiteTests;

@RunWith(Suite.class)
@SuiteClasses({ InterpreterTest.class, QuestEventTest.class, QuestObjectTest.class, StatementSuiteTests.class })
public class AllTests {

}
