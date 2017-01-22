package net.mcfr.quests.questEngine;

public class QuestEvent {
  private String id;
  private String text;

  public QuestEvent(String id, String text) {
    if (!id.equals(id.toUpperCase()))
      throw new IllegalArgumentException("Event's id must be in upper case! (" + id + ")");

    this.id = id;
    this.text = text;
  }

  public String getId() {
    return this.id;
  }

  public String getText() {
    return this.text;
  }

  @Override
  public String toString() {
    return String.format("%s : \"%s\"", getId(), getText());
  }

}
