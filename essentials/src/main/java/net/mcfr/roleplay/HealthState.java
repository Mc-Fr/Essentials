package net.mcfr.roleplay;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.mcfr.utils.McFrConnection;
import net.mcfr.utils.McFrPlayer;

public class HealthState {
  private int healthValue;
  private int healthMax;
  private int end;
  private boolean isMalusReduced;
  private boolean isMalusIncreased;

  public HealthState(int initValue) {
    this.healthValue = initValue;
    this.healthMax = initValue;
    this.end = initValue;
    this.isMalusReduced = false;
    this.isMalusIncreased = false;
  }

  public int getValue() {
    return this.healthValue;
  }

  public int getMax() {
    return this.healthMax;
  }

  public void refresh(McFrPlayer owner) {
    this.end = owner.getAttributePoints(Attribute.ENDURANCE);
    this.healthMax = this.end * 2;
    this.healthValue = Math.min(this.healthValue, this.healthMax);
    this.isMalusReduced = owner.hasTrait("haute_resistance_a_la_douleur");
    this.isMalusIncreased = owner.hasTrait("sensible_a_la_douleur");
  }

  private void save(McFrPlayer owner) {
    try (Connection jdrConnection = McFrConnection.getConnection()) {
      PreparedStatement setHealth = jdrConnection.prepareStatement("UPDATE `fiche_perso_personnage` SET `health`=? WHERE `id`=?");
      setHealth.setInt(1, this.healthValue);
      setHealth.setInt(2, owner.getSheetId());
      setHealth.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void add(McFrPlayer owner, int addingValue) {
    this.healthValue = Math.min(this.healthValue + addingValue, this.healthMax);
    this.save(owner);
  }

  public void set(McFrPlayer owner, int value) {
    this.healthValue = Math.min(value, this.healthMax);
    this.save(owner);
  }

  public int getMalus(Attribute attribute) {
    int malus = 0;
    float spacing = 0f;
    
    if (this.healthValue <= 0)
      malus--;
    
    if (this.isMalusReduced) {
      malus++;
      spacing++;
    } else if (this.isMalusIncreased) {
      spacing -= 0.5f;
    }
    
    if (this.end <= 7) {
      spacing += 1.5f;
    } else if (this.end <= 11) {
      spacing += 2f;
    } else {
      spacing += 3f;
    }
    
    malus -= Math.floor(-1f * this.healthValue / spacing);
    
    if (attribute.equals(Attribute.INTELLECT))
      malus /= 2;
    
    return Math.min(malus, 0);
  }
}
