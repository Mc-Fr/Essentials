package net.mcfr.roleplay;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.mcfr.utils.McFrConnection;
import net.mcfr.utils.McFrPlayer;

public class Health {
  private int value;
  private int max;
  private boolean isMalusReduced;

  public Health(int initValue) {
    this.value = initValue;
    this.max = initValue;
    this.isMalusReduced = false;
  }

  public Health(McFrPlayer owner, int initValue) {
    this(initValue);
    this.refresh(owner);
  }

  public int getValue() {
    return this.value;
  }

  public int getMax() {
    return this.max;
  }

  public void refresh(McFrPlayer owner) {
    this.max = owner.getAttributePoints(Attributes.ENDURANCE);
    this.value = Math.min(this.value, this.max);
    this.isMalusReduced = owner.hasTrait("haute_resistance_a_la_douleur");
  }

  private void save(McFrPlayer owner) {
    try (Connection jdrConnection = McFrConnection.getJdrConnection()) {
      PreparedStatement setHealth = jdrConnection.prepareStatement("UPDATE `fiche_perso_personnage` SET `health`=? WHERE `id`=?");
      setHealth.setInt(1, this.value);
      setHealth.setInt(2, owner.getSheetId());
      setHealth.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void add(McFrPlayer owner, int addingValue) {
    this.value = Math.min(this.value + addingValue, this.max);
    this.save(owner);
  }

  public void set(McFrPlayer owner, int value) {
    this.value = value;
    this.save(owner);
  }

  public int getMalus(Attributes attribute) {
    int malus = Math.min(this.value - 2 * this.max / 3 + (this.isMalusReduced ? 2 : 0), 0);
    if (attribute.equals(Attributes.INTELLECT)) {
      return malus / 2;
    } else {
      return malus;
    }
  }

  public int getMalus() {
    return this.getMalus(Attributes.ENDURANCE);
  }
}
