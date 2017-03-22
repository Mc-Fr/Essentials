package net.mcfr.roleplay;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import net.mcfr.utils.McFrConnection;
import net.mcfr.utils.McFrPlayer;

public class Skills {
  private static Map<String, Skills> skills = new HashMap<>();
  private static Map<String, Skills> combatSkills = new HashMap<>();

  private String name;
  private String displayName;
  private Attributes attribute;
  private int difficulty;
  private Map<Skills, Integer> dependencies;

  private Skills(String name, String displayName, Attributes attribute, int difficulty) {
    this.name = name;
    this.displayName = displayName.toLowerCase();
    this.attribute = attribute;
    this.difficulty = -difficulty;
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
    try (Connection jdrConnection = McFrConnection.getConnection()){
      ResultSet skillData = jdrConnection.prepareStatement("SELECT name, displayName, baseAttribute, difficulty, category FROM Skills").executeQuery();
      
      while (skillData.next()) {
        Skills skill = new Skills(skillData.getString("name"), skillData.getString("displayName"),
            Attributes.getAttributeFromString(skillData.getString("baseAttribute")), skillData.getInt("difficulty"));
        skills.put(skill.getName(), skill);

        if (skillData.getString("category").equals("combat")) {
          combatSkills.put(skill.getName(), skill);
        }
      }
      skillData.close();

      ResultSet dependenciesData = jdrConnection.prepareStatement("SELECT skill1, skill2, score FROM Dependances").executeQuery();
      
      while (dependenciesData.next()) {
        Skills skill1 = skills.get(dependenciesData.getString("skill1"));
        Skills skill2 = skills.get(dependenciesData.getString("skill2"));
        int score = dependenciesData.getInt("score");
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

  public static Map<String, Skills> getCombatSkills() {
    return combatSkills;
  }

  public static Skills getSkillByName(String name) {
    return skills.get(name);
  }

  /**
   * Détermine la compétence de combat à utiliser par le joueur en fonction de l'item qu'il a en main principale.
   * @param player Le joueur dont la compétence de combat est déterminée
   * @return La compétence de combat à utiliser
   */
  public static Skills getWeaponSkill(Player player) {
    Optional<ItemStack> optUsedWeapon = player.getItemInHand(HandTypes.MAIN_HAND);
    McFrPlayer mcFrPlayer = McFrPlayer.getMcFrPlayer(player);
    
    if (!optUsedWeapon.isPresent()) {
      optUsedWeapon = player.getItemInHand(HandTypes.OFF_HAND);
      if (!optUsedWeapon.isPresent()) {
        return mcFrPlayer.getBestSkill(combatSkills.get("pugilat"), combatSkills.get("lutte"), combatSkills.get("arts_martiaux"));
      }
    }

    String usedWeaponName = optUsedWeapon.get().getItem().getName().toLowerCase();
    if (usedWeaponName.contains("shield"))
      return combatSkills.get("bouclier");
    if (usedWeaponName.contains("dagger"))
      return combatSkills.get("dague");
    if (usedWeaponName.contains("rapier"))
      return combatSkills.get("rapiere");
    if (usedWeaponName.contains("scimitar"))
      return combatSkills.get("sabre");
    if (usedWeaponName.contains("bastard"))
      return combatSkills.get("epee_batarde");
    if (usedWeaponName.contains("long_sword"))
      return combatSkills.get("epee_a_deux_mains");
    if (usedWeaponName.contains("sword"))
      return combatSkills.get("epee_courte");
    if (usedWeaponName.contains("flail"))
      return combatSkills.get("fleau_a_une_main");
    if (usedWeaponName.contains("whip"))
      return combatSkills.get("fouet");
    if (usedWeaponName.contains("battle_axe") || usedWeaponName.contains("war_hammer"))
      return combatSkills.get("hache/masse_a_deux_mains");
    if (usedWeaponName.contains("axe") || usedWeaponName.contains("mace"))
      return combatSkills.get("hache/masse_a_une_main");
    if (usedWeaponName.contains("halberd"))
      return combatSkills.get("hallebarde");
    if (usedWeaponName.contains("spear") || usedWeaponName.contains("pointy"))
      return combatSkills.get("lance");
    if (usedWeaponName.contains("bow"))
      return combatSkills.get("arc");
    if (usedWeaponName.contains("staff"))
      return combatSkills.get("baton");
    if (usedWeaponName.contains("gun"))
      return combatSkills.get("armes_a_feu");

    return mcFrPlayer.getBestSkill(combatSkills.get("pugilat"), combatSkills.get("lutte"), combatSkills.get("arts_martiaux"));
  }
}