package net.mcfr.fight;

import java.util.Optional;

import org.spongepowered.api.text.Text;

import net.mcfr.roleplay.rollResults.AttributeRollResult;

public interface Fighter {

  AttributeRollResult getInitiativeRoll();

  Optional<Long> getFightJoinTime();

  Optional<String> getFight();

  void joinFight(String fight);

  void leaveFight(Optional<Text> message);

}
