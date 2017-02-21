package net.mcfr.fight;

import java.util.Set;
import java.util.TreeSet;

import net.mcfr.utils.McFrPlayer;

public class Fight {

  private String id;
  private McFrPlayer creator;
  private Set<Fighter> fighters;

  public Fight(String id, McFrPlayer creator) {
    this.id = id;
    this.creator = creator;
    this.fighters = new TreeSet<>((f1, f2) -> 0); //TODO Compléter le Comparator.
  }

  public void stop() {

  }

  public void add(Fighter fighter) {

  }

  public void remove(Fighter fighter) {
    // TODO Auto-generated method stub

  }

  public void start() {
    // TODO Auto-generated method stub

  }

}
