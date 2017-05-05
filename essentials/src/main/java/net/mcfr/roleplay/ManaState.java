package net.mcfr.roleplay;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.mcfr.utils.McFrConnection;
import net.mcfr.utils.McFrPlayer;

public class ManaState {
  private int manaValue;
  private int manaMax;

  public ManaState(int initValue) {
    this.manaValue = initValue;
    this.manaMax = initValue;
  }

  public int getValue() {
    return this.manaValue;
  }

  public int getMax() {
    return this.manaMax;
  }

  public void refresh(McFrPlayer owner) {
    int magicLevel = owner.getTraitLevel("magie");
    this.manaMax = magicLevel > 0 ? owner.getAttributePoints(Attribute.INTELLECT) + magicLevel : 0;
    this.manaValue = Math.min(this.manaValue, this.manaMax);
  }

  private void save(McFrPlayer owner) {
    try (Connection jdrConnection = McFrConnection.getConnection()) {
      PreparedStatement setHealth = jdrConnection.prepareStatement("UPDATE `fiche_perso_personnage` SET `mana`=? WHERE `id`=?");
      setHealth.setInt(1, this.manaValue);
      setHealth.setInt(2, owner.getSheetId());
      setHealth.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public int add(McFrPlayer owner, int addingValue) {
    this.manaValue = Math.min(this.manaValue + addingValue, this.manaMax);
    this.save(owner);
    int value = 0;
    if (this.manaValue < 0) {
      value = this.manaValue;
      this.manaValue = 0;
    }
    return value;
  }

  public void set(McFrPlayer owner, int value) {
    this.manaValue = Math.min(value, this.manaMax);
    this.save(owner);
  }
}
