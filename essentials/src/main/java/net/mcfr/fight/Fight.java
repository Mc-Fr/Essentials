package net.mcfr.fight;

import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import net.mcfr.roleplay.rollResults.AttributeRollResult;
import net.mcfr.utils.McFrPlayer;

public class Fight {

  private String id;
  private McFrPlayer leader;
  private int index;
  private Set<Fighter> fighters;
  private Set<McFrPlayer> banFighters;

  public Fight(String id, McFrPlayer leader) {
    this.id = id;
    this.leader = leader;
    this.index = 0;
    this.fighters = new TreeSet<>((f1, f2) -> {
      AttributeRollResult f1Result = f1.getInitiativeRoll();
      AttributeRollResult f2Result = f2.getInitiativeRoll();
      if (f1Result.getMargin() == f2Result.getMargin()) {
        if (f1Result.getScore() == f2Result.getScore())
          return Long.compare(f1.getFightJoinTime().get(), f2.getFightJoinTime().get());
        return Integer.compare(f2Result.getScore(), f1Result.getScore());
      }
      return Integer.compare(f2Result.getMargin(), f1Result.getMargin());
    });
  }

  public void stop() {
    for (Fighter f : this.fighters) {
      f.leaveFight(Optional.of(Text.of(TextColors.YELLOW, "Le combat est fini.")));
    }
    this.fighters.clear();
  }

  public boolean add(Fighter fighter) {
    if (!this.banFighters.contains(fighter)) {
      this.fighters.add(fighter);
      fighter.joinFight(this.id);
      return true;
    }
    return false;
  }

  public void remove(Fighter fighter) {
    this.fighters.remove(fighter);
    fighter.leaveFight(Optional.of(Text.of(TextColors.YELLOW, "Vous avez quitté le combat.")));
  }

  public void start() {

  }

  public void ban(McFrPlayer fighter) {
    this.fighters.remove(fighter);
    fighter.leaveFight(Optional.of(Text.of(TextColors.YELLOW, "Vous avez été banni du combat.")));
    this.banFighters.add(fighter);
  }

  public void setTurn(int index) {
    this.index = index;
  }

  public void next() {
    this.index = (this.index + 1) % this.fighters.size();
  }

  public void skip() {

  }

  public void interrupt(Fighter fighter) {
    // TODO Auto-generated method stub

  }

  public void resume(Fighter fighter) {
    // TODO Auto-generated method stub

  }

  public void changeLeader(McFrPlayer leader) {
    if (this.fighters.contains(leader)) {
      this.leader = leader;
    } else
      throw new IllegalArgumentException("Le nouveau responsable doit faire parti du combat.");
  }
}