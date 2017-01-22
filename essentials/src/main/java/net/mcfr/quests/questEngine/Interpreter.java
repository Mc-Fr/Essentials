package net.mcfr.quests.questEngine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Interpreter {

  private static Pattern eventPattern = Pattern.compile("^#(\\w+)\\s+(\\w+)\\s\"([^\"]+)\"$");

  private List<QuestEvent> events;

  private Interpreter() {
    this.events = new ArrayList<>();
  }

  public static Interpreter compile(String script) {
    script = script.replace("(?:\r([^\n])|\r\n)", "\n$1");

    Interpreter interp = new Interpreter();
    String[] scriptLines = script.split("\n");
    for (String line : scriptLines) {
      Matcher matcher = eventPattern.matcher(line);
      if (matcher.find()) {
        interp.addEvent(new QuestEvent(matcher.group(2), matcher.group(3)));
      }
    }
    return interp;
  }

  private void addEvent(QuestEvent questEvent) {
    this.events.add(questEvent);
  }

  public List<QuestEvent> getEvents() {
    return Collections.unmodifiableList(this.events);
  }

}
