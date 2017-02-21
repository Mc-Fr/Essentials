package net.mcfr.fight;

import java.util.HashMap;
import java.util.Map;

import net.mcfr.utils.McFrPlayer;

public class FightImp implements FightService {

  private Map<String, Fight> fights;

  public FightImp() {
    this.fights = new HashMap<>();
  }

  @Override
  public void createFight(String fightId, McFrPlayer creator) {
    Fight fight = new Fight(fightId, creator);
    this.fights.put(fightId, fight);

  }

  @Override
  public void stopFight(String fightId) {
    Fight fight = this.fights.get(fightId);
    if (fight != null) {
      fight.stop();
      this.fights.remove(fight);
    }

  }

  @Override
  public void addFighter(String fightId, Fighter fighter) {
    Fight fight = this.fights.get(fightId);
    if (fight != null) {
      fight.add(fighter);
    }
  }

  @Override
  public void removeFighter(String fightId, Fighter fighter) {
    Fight fight = this.fights.get(fightId);
    if (fight != null) {
      fight.remove(fighter);
    }
  }

  @Override
  public void startFight(String fightId) {
    Fight fight = this.fights.get(fightId);
    if (fight != null) {
      fight.start();
    }
  }

  @Override
  public void addSpectator(String fightId, McFrPlayer spectator) {
    Fight fight = this.fights.get(fightId);
    if (fight != null) {
      fight.remove(spectator);
    }
  }

  @Override
  public void displayMaluses(String fightId, McFrPlayer player) {
    // TODO Auto-generated method stub

  }

  @Override
  public void hideMaluses(String fightId, McFrPlayer player) {
    // TODO Auto-generated method stub

  }

  @Override
  public void next(String fightId) throws IllegalStateException {
    // TODO Auto-generated method stub

  }

  @Override
  public void wait(String fightId) throws IllegalStateException {
    // TODO Auto-generated method stub

  }

  @Override
  public void interrupt(String fightId, Fighter fighter) throws IllegalStateException {
    // TODO Auto-generated method stub

  }

  @Override
  public void resume(String fightId, Fighter fighter) throws IllegalStateException {
    // TODO Auto-generated method stub

  }

  @Override
  public void changeLeader(String fightId, McFrPlayer newLeader) throws IllegalArgumentException {
    // TODO Auto-generated method stub

  }

  @Override
  public void kickFighter(String fightId, McFrPlayer fighter) {
    // TODO Auto-generated method stub

  }

  @Override
  public void banFighter(String fightId, McFrPlayer fighter) {
    // TODO Auto-generated method stub

  }

  @Override
  public void insertFighter(String fightId, Fighter fighter, int index) {
    // TODO Auto-generated method stub

  }

  @Override
  public void setTurn(String fightId, Fighter fighter) {
    // TODO Auto-generated method stub

  }

}
