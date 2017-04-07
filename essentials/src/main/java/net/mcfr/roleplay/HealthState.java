package net.mcfr.roleplay;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.mcfr.utils.McFrConnection;
import net.mcfr.utils.McFrPlayer;

public class HealthState {
  private int healthValue;
  private int fatigueValue;
  private int endMax;
  private boolean isMalusReduced;
  private boolean isMalusIncreased;

  public HealthState(int initValue) {
    this.healthValue = initValue;
    this.fatigueValue = initValue;
    this.endMax = initValue;
    this.isMalusReduced = false;
    this.isMalusIncreased = false;
  }

  public int getHealthValue() {
    return this.healthValue;
  }
  
  public int getFatigueValue() {
    return this.fatigueValue;
  }

  public int getMax() {
    return this.endMax;
  }

  public void refresh(McFrPlayer owner) {
    this.endMax = owner.getAttributePoints(Attributes.ENDURANCE);
    this.healthValue = Math.min(this.healthValue, this.endMax);
    this.fatigueValue = Math.min(this.fatigueValue, this.endMax);
    this.isMalusReduced = owner.hasTrait("haute_resistance_a_la_douleur");
    this.isMalusReduced = owner.hasTrait("sensible_a_la_douleur");
  }

  private void save(McFrPlayer owner) {
    try (Connection jdrConnection = McFrConnection.getConnection()) {
      PreparedStatement setHealth = jdrConnection.prepareStatement("UPDATE `fiche_perso_personnage` SET `health`=?,`fatigue`=? WHERE `id`=?");
      setHealth.setInt(1, this.healthValue);
      setHealth.setInt(2, this.fatigueValue);
      setHealth.setInt(3, owner.getSheetId());
      setHealth.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void addHealth(McFrPlayer owner, int addingValue) {
    this.healthValue = Math.min(this.healthValue + addingValue, this.endMax);
    this.save(owner);
  }

  public void setHealth(McFrPlayer owner, int value) {
    this.healthValue = Math.min(value, this.endMax);
    this.save(owner);
  }
  
  public void addFatigue(McFrPlayer owner, int addingValue) {
    this.fatigueValue = Math.min(this.fatigueValue + addingValue, this.endMax);
    this.save(owner);
  }
  
  public void setFatigue(McFrPlayer owner, int value) {
    this.fatigueValue = Math.min(value, this.endMax);
    this.save(owner);
  }

  public int getMalus(Attributes attribute) {
    int malus = getHealthMalus();
    if (attribute.equals(Attributes.INTELLECT)) {
      malus /= 2;
    }    
    if (attribute.equals(Attributes.DEXTERITE) || attribute.equals(Attributes.INTELLECT)) {
      malus += getFatigueMalus();
    }
    return malus;
  }

  public int getHealthMalus() {
    return Math.min(this.healthValue - 2 * this.endMax / 3 + (this.isMalusReduced ? 2 : 0) - (this.isMalusIncreased ? 2 : 0), 0);
  }
  
  public int getFatigueMalus() {
    int malus = 0;
    if (this.fatigueValue <= this.endMax / 2) {
      malus -= 2;
      if (this.fatigueValue <= this.endMax / 3) {
        malus -= 2;
      }
    }
    return malus;
  }
}
