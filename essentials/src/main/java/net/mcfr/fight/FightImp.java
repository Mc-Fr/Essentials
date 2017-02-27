package net.mcfr.fight;

import java.util.HashMap;
import java.util.Map;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

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
    } else
      throw new IllegalArgumentException(String.format("Le combat \"%s\" n'existe pas !", fightId));
  }

  @Override
  public void addFighter(String fightId, Fighter fighter) {
    Fight fight = this.fights.get(fightId);
    if (fight != null) {
      if (!fight.add(fighter))
        throw new IllegalArgumentException("Le joueur est banni de ce combat !");
    } else
      throw new IllegalArgumentException(String.format("Le combat \"%s\" n'existe pas !", fightId));
  }

  @Override
  public void removeFighter(String fightId, Fighter fighter) {
    Fight fight = this.fights.get(fightId);
    if (fight != null) {
      fight.remove(fighter);
    } else
      throw new IllegalArgumentException(String.format("Le combat \"%s\" n'existe pas !", fightId));
  }

  @Override
  public void startFight(String fightId) {
    Fight fight = this.fights.get(fightId);
    if (fight != null) {
      fight.start();
    } else
      throw new IllegalArgumentException(String.format("Le combat \"%s\" n'existe pas !", fightId));
  }

  @Override
  public void addSpectator(String fightId, McFrPlayer spectator) {
    Fight fight = this.fights.get(fightId);
    if (fight != null) {
      fight.remove(spectator); //TODO
    } else
      throw new IllegalArgumentException(String.format("Le combat \"%s\" n'existe pas !", fightId));
  }

  @Override
  public void displayMaluses(String fightId, McFrPlayer player) {

  }

  @Override
  public void hideMaluses(String fightId, McFrPlayer player) {

  }

  @Override
  public void next(String fightId) throws IllegalStateException {
    Fight fight = this.fights.get(fightId);
    if (fight != null) {
      fight.next();
    } else
      throw new IllegalArgumentException(String.format("Le combat \"%s\" n'existe pas !", fightId));
  }

  @Override
  public void wait(String fightId) throws IllegalStateException {
    Fight fight = this.fights.get(fightId);
    if (fight != null) {
      fight.skip();
    } else
      throw new IllegalArgumentException(String.format("Le combat \"%s\" n'existe pas !", fightId));
  }

  @Override
  public void interrupt(String fightId, Fighter fighter) throws IllegalStateException {
    Fight fight = this.fights.get(fightId);
    if (fight != null) {
      fight.interrupt(fighter);
    } else
      throw new IllegalArgumentException(String.format("Le combat \"%s\" n'existe pas !", fightId));
  }

  @Override
  public void resume(String fightId, Fighter fighter) throws IllegalStateException {
    Fight fight = this.fights.get(fightId);
    if (fight != null) {
      fight.resume(fighter);
    } else
      throw new IllegalArgumentException(String.format("Le combat \"%s\" n'existe pas !", fightId));
  }

  @Override
  public void changeLeader(String fightId, McFrPlayer newLeader) throws IllegalArgumentException {
    Fight fight = this.fights.get(fightId);
    if (fight != null) {
      fight.changeLeader(newLeader);
    } else
      throw new IllegalArgumentException(String.format("Le combat \"%s\" n'existe pas !", fightId));
  }

  @Override
  public void kickFighter(String fightId, McFrPlayer fighter) {
    Fight fight = this.fights.get(fightId);
    if (fight != null) {
      fight.remove(fighter);
      fighter.getPlayer().sendMessage(Text.of(TextColors.YELLOW, "Vous avez été expulsé du combat."));
    } else
      throw new IllegalArgumentException(String.format("Le combat \"%s\" n'existe pas !", fightId));
  }

  @Override
  public void banFighter(String fightId, McFrPlayer fighter) {
    Fight fight = this.fights.get(fightId);
    if (fight != null) {
      fight.remove(fighter);
      fight.ban(fighter);
      fighter.getPlayer().sendMessage(Text.of(TextColors.YELLOW, "Vous avez été banni du combat."));
    } else
      throw new IllegalArgumentException(String.format("Le combat \"%s\" n'existe pas !", fightId));
  }

  @Override
  public void insertFighter(String fightId, Fighter fighter, int index) {

  }

  @Override
  public void setTurn(String fightId, int index) {
    Fight fight = this.fights.get(fightId);
    if (fight != null) {
      fight.setTurn(index);
    } else
      throw new IllegalArgumentException(String.format("Le combat \"%s\" n'existe pas !", fightId));
  }
}