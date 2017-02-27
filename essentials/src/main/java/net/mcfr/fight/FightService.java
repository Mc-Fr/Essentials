package net.mcfr.fight;

import net.mcfr.utils.McFrPlayer;

public interface FightService {

  void createFight(String fightId, McFrPlayer creator);

  void stopFight(String fightId);

  void addFighter(String fightId, Fighter fighter);

  void removeFighter(String fightId, Fighter fighter);

  void startFight(String fightId);

  void addSpectator(String fightId, McFrPlayer spectator);

  void displayMaluses(String fightId, McFrPlayer player);

  void hideMaluses(String fightId, McFrPlayer player);

  void next(String fightId) throws IllegalStateException;

  void wait(String fightId) throws IllegalStateException;

  void interrupt(String fightId, Fighter fighter) throws IllegalStateException;

  void resume(String fightId, Fighter fighter) throws IllegalStateException;

  void changeLeader(String fightId, McFrPlayer newLeader) throws IllegalArgumentException;

  void kickFighter(String fightId, McFrPlayer fighter);

  void banFighter(String fightId, McFrPlayer fighter);

  void insertFighter(String fightId, Fighter fighter, int index);

  void setTurn(String fightId, int index);

}
