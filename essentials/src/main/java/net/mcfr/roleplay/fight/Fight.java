package net.mcfr.roleplay.fight;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Optional;

import net.mcfr.utils.McFrPlayer;

public class Fight {
  private LinkedList<Fighter> fighters;
  private Iterator<Fighter> fightersIterator;
  private Optional<Fighter> currentFighter;
  
  public Fight() {
    this.fighters = new LinkedList<>();
    this.fightersIterator = this.fighters.iterator();
  }
  
  public boolean addFighter(McFrPlayer fighter) {
    Optional<Fighter> optFighter = this.fighters.stream().filter(f -> f.getPlayer().getPlayer() == fighter.getPlayer()).findFirst();
    if (optFighter.isPresent()) {
      return false;
    } else {
      fighters.addLast(new Fighter(fighter));
      return true;
    }
  }
  
  public boolean addFighter(McFrPlayer fighter, int order) {
    Optional<Fighter> optFighter = this.fighters.stream().filter(f -> f.getPlayer().getPlayer() == fighter.getPlayer()).findFirst();
    if (optFighter.isPresent()) {
      return false;
    } else {
      fighters.add(order - 1, new Fighter(fighter));
      return true;
    }
  }
  
  public boolean removeFighter(McFrPlayer fighter) {
    Optional<Fighter> optFighter = this.fighters.stream().filter(f -> f.getPlayer().getPlayer() == fighter.getPlayer()).findFirst();
    if (optFighter.isPresent()) {
      this.fighters.remove(optFighter.get());
      return true;
    } else {
      return false;
    }
  }
  
  public Optional<Fighter> getCurrentFighter() {
    return this.currentFighter;
  }
  
  public void nextTurn() {
    if (this.fighters.size() > 0) {
      if (!this.fightersIterator.hasNext()) {
        this.fightersIterator = this.fighters.iterator();
      }
      this.currentFighter = Optional.of(this.fightersIterator.next());
    } else {
      this.currentFighter = Optional.empty();
    }
  }
}
