package net.mcfr.quests.questEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class QuestObject {

  private StringJoiner sentences;
  private List<String> commands;

  public QuestObject() {
    this.sentences = new StringJoiner("\n");
    this.commands = new ArrayList<>();
  }

  public void appendSentence(String sentence) {
    this.sentences.add(sentence);
  }

  public String getSentences() {
    return this.sentences.toString();
  }

  public void addCommand(String command) {
    this.commands.add(command);
  }

  public List<String> getCommands() {
    return this.commands;
  }

}
