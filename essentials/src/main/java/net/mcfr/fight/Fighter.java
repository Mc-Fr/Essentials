package net.mcfr.fight;

import java.util.Optional;

import net.mcfr.roleplay.rollResults.AttributeRollResult;

public interface Fighter {

  AttributeRollResult getInitiativeRoll();

  Optional<Long> getFightJoinTime();

  Optional<String> getFight();

  void joinFight(String fight);

  void leaveFight(Optional<String> message);

}
