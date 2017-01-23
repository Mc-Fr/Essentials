package net.mcfr.roleplay;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import net.mcfr.utils.McFrConnection;

public class Skills {
  private static Map<String, Skills> skills = new HashMap<>();

  private String name;
  private String displayName;
  private Attributes attribute;
  private int difficulty;
  private Map<Skills, Integer> dependencies;

  private Skills(String name, String displayName, Attributes attribute, int difficulty) {
    this.name = name;
    this.displayName = displayName.toLowerCase();
    this.attribute = attribute;
    this.difficulty = - difficulty;
    this.dependencies = new HashMap<>();
  }
  
  private void addDependency(Skills otherSkill, int score) {
    this.dependencies.put(otherSkill, score);
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
  
  public Map<Skills, Integer> getDependencies() {
    return this.dependencies;
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
      
      ResultSet dependenciesData = McFrConnection.getJdrConnection()
          .executeQuery("SELECT skill1, skill2, default FROM fiche_perso_dependances");
      while (dependenciesData.next()) {
        Skills skill1 = skills.get(dependenciesData.getString(1));
        Skills skill2 = skills.get(dependenciesData.getString(2));
        int score = dependenciesData.getInt(3);
        skill1.addDependency(skill2, score);
        skill2.addDependency(skill1, score);
      }
      dependenciesData.close();
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

  public static Skills getWeaponSkill(Player player) {
    Optional<ItemStack> optUsedWeapon = player.getItemInHand(HandTypes.MAIN_HAND);
    if (!optUsedWeapon.isPresent()) {
      optUsedWeapon = player.getItemInHand(HandTypes.OFF_HAND);
      if (!optUsedWeapon.isPresent())
        return skills.get("pugilat");
    }

    String usedWeaponName = optUsedWeapon.get().getItem().getName();
    if (usedWeaponName.contains("dagger"))
      return skills.get("dague");
    if (usedWeaponName.contains("rapier"))
      return skills.get("rapiere");
    if (usedWeaponName.contains("scimitar"))
      return skills.get("sabre");
    if (usedWeaponName.endsWith("sword"))
      return skills.get("epee_courte");
    if (usedWeaponName.contains("bastard"))
      return skills.get("epee_a_deux_mains");
    if (usedWeaponName.contains("mace") || usedWeaponName.contains("war_hammer"))
      return skills.get("hache/masse_a_une_main");
    if (usedWeaponName.contains("battle_axe"))
      return skills.get("hache/masse_a_deux_mains");
    if (usedWeaponName.contains("halberd"))
      return skills.get("hallebarde");
    if (usedWeaponName.contains("spear") || usedWeaponName.contains("pointy"))
      return skills.get("lance");
    if (usedWeaponName.contains("bow"))
      return skills.get("arc");
    if (usedWeaponName.contains("boStaff"))
      return skills.get("baton");

    return skills.get("pugilat");
  }
}