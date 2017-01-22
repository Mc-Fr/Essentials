package net.mcfr.roleplay;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import net.mcfr.utils.McFrConnection;

  //TODO : pSQL et ReqPrep

public class Skills {
  private static HashMap<String, Skills> skills = new HashMap<>();

  private String name;
  private String displayName;
  private Attributes attribute;
  private int difficulty;

  public Skills(String name, String displayName, Attributes attribute, int difficulty) {
    this.name = name;
    this.displayName = displayName.toLowerCase();
    this.attribute = attribute;
    this.difficulty = -difficulty - 1;
  }

  public String getName() {
    return this.name;
  }

  public String getDisplayName() {
    return this.displayName;
  }

  public int getDifficulty() {
    return this.difficulty;
  }

  public static void loadFromDatabase() {
    try {
      ResultSet skillData = McFrConnection.getJdrConnection()
          .executeQuery("SELECT name, displayName, baseAttribute, difficulty FROM fiche_perso_competence");
      while (skillData.next()) {
        skills.put(skillData.getString(1), new Skills(skillData.getString(1), skillData.getString(2),
            Attributes.getAttributeFromString(skillData.getString(3)), skillData.getInt(4)));
      }
      skillData.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public Attributes getAttribute() {
    return this.attribute;
  }

  public static Map<String, Skills> getSkills() {
    return skills;
  }
}
