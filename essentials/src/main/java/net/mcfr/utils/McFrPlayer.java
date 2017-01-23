package net.mcfr.utils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.data.property.block.LightEmissionProperty;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.whitelist.WhitelistService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import net.mcfr.babel.Language;
import net.mcfr.burrows.Burrow;
import net.mcfr.chat.ChatType;
import net.mcfr.roleplay.Attributes;
import net.mcfr.roleplay.Skills;

public class McFrPlayer {
  private static List<McFrPlayer> players = new ArrayList<>();
  private final static PreparedStatement killCharacter, incrementDeaths, changeDescription, changeName, getPseudonym, getUserId, getCharacterSheetId,
      getCharacterSheet, getAttributes, getAdvantages;

  static {
    killCharacter = McFrConnection.getJdrConnection().prepare("INSERT INTO fiche_perso_personnage_avantage VALUES (?, ?, ?)");
    incrementDeaths = McFrConnection.getServerConnection().prepare("UPDATE Player SET deaths = ? WHERE pseudonym = ?");
    changeDescription = McFrConnection.getServerConnection().prepare("UPDATE Player SET description = ? WHERE pseudonym = ?");
    changeName = McFrConnection.getServerConnection().prepare("UPDATE Player SET name = ? WHERE pseudonym = ?");
    getPseudonym = McFrConnection.getServerConnection().prepare("SELECT * FROM Player WHERE pseudonym = ?");
    getUserId = McFrConnection.getJdrConnection()
        .prepare("SELECT user_id FROM phpbb_users PU JOIN account_link AL ON AL.forum = PU.username WHERE AL.minecraft = ?");
    getCharacterSheetId = McFrConnection.getJdrConnection().prepare("SELECT id FROM fiche_perso_personnage WHERE id_user = ? AND active = 1");
    getCharacterSheet = McFrConnection.getJdrConnection()
        .prepare("SELECT * FROM fiche_perso_personnage_competence WHERE id_fiche_perso_personnage = ?");
    getAttributes = McFrConnection.getJdrConnection()
        .prepare("SELECT attribut,level FROM fiche_perso_personnage_attribut WHERE id_fiche_perso_personnage = ?");
    getAdvantages = McFrConnection.getJdrConnection()
        .prepare("SELECT avantage,value FROM fiche_perso_personnage_avantage WHERE id_fiche_perso_personnage = ?");
  }
  private Player player;
  private int deaths;
  /**
   * Représente tous les booléens présents dans la classe.
   * <table style="border-collapse: collapse">
   * <tr>
   * <td style="border: 1px solid black">hasCharacter</td>
   * <td style="border: 1px solid black">isInCareCenterEffectArea</td>
   * <td style="border: 1px solid black">seeBurrows</td>
   * <td style="border: 1px solid black">wantRealName</td>
   * <td style="border: 1px solid black">wantTeam</td>
   * <td style="border: 1px solid black">wantMP</td>
   * <td style="border: 1px solid black">spyMP</td>
   * <td style="border: 1px solid black">god</td>
   * <td style="border: 1px solid black">mute</td>
   * </tr>
   * <tr>
   * <td style="border: 1px solid black">0b1_0000_0000</td>
   * <td style="border: 1px solid black">0b0_1000_0000</td>
   * <td style="border: 1px solid black">0b0_0100_0000</td>
   * <td style="border: 1px solid black">0b0_0010_0000</td>
   * <td style="border: 1px solid black">0b0_0001_0000</td>
   * <td style="border: 1px solid black">0b0_0000_1000</td>
   * <td style="border: 1px solid black">0b0_0000_0100</td>
   * <td style="border: 1px solid black">0b0_0000_0010</td>
   * <td style="border: 1px solid black">0b0_0000_0001</td>
   * </tr>
   * </table>
   */
  private int booleans;
  private Optional<Burrow> selectedBurrow;
  private String name;
  private Optional<String> description;
  private ChatType defaultChat;
  private Language language;
  private Player lastCorrespondent;
  private int sheetId;
  private HashMap<Skills, Integer> skills;
  private HashMap<Attributes, Integer> attributes;
  private HashMap<String, Integer> traits;
  private Location<World> previousLocation;

  public static void addPlayer(McFrPlayer player) {
    players.add(player);
  }

  public static void removePlayer(Player player) {
    players.remove(new McFrPlayer(player));
  }

  public static McFrPlayer getMcFrPlayer(String name) {
    return getMcFrPlayer(Sponge.getServer().getPlayer(name).get());
  }

  public static McFrPlayer getMcFrPlayer(UUID uniqueId) {
    return getMcFrPlayer(Sponge.getServer().getPlayer(uniqueId).get());
  }

  public static McFrPlayer getMcFrPlayer(Player player) {
    return players.stream().filter(p -> p.getPlayer().equals(player)).findFirst().get();
  }

  public static double distance(Player p1, Player p2) {
    return p1.getLocation().getBlockPosition().distance(p2.getLocation().getBlockPosition());
  }

  public static void setLastCorrespondents(Player emitter, Player recipient) {
    getMcFrPlayer(emitter).setLastCorrespondent(recipient);
    getMcFrPlayer(recipient).setLastCorrespondent(emitter);
  }

  public static boolean toBoolean(int i) {
    return i > 0;
  }

  public McFrPlayer(Player player) {
    Objects.requireNonNull(player);
    this.player = player;
    this.booleans = 0;
    this.name = player.getName();
    this.description = Optional.empty();
    this.defaultChat = ChatType.MEDIUM;
    this.sheetId = -1;
    this.skills = new HashMap<>();
    this.attributes = new HashMap<>();
    this.traits = new HashMap<>();
    this.previousLocation = null;
    this.selectedBurrow = Optional.empty();
  }

  public Player getPlayer() {
    return this.player;
  }

  public boolean isInCareCenterEffectArea() {
    return toBoolean(this.booleans & 0b0_1000_0000);
  }

  public boolean hasCharacter() {
    return toBoolean(this.booleans & 0b1_0000_0000);
  }

  public void toggleSeesBurrows() {
    if (seesBurrows()) {
      this.booleans |= 0b0_0100_0000;
    } else {
      this.booleans ^= 0b0_0100_0000;
    }
  }

  public boolean seesBurrows() {
    return toBoolean(this.booleans & 0b0_0100_0000);
  }

  public void selectBurrow(Burrow burrow) {
    unselectBurrow();
    this.selectedBurrow = Optional.of(burrow);
    if (seesBurrows()) {
      burrow.setVisible(this.player);
    }
  }

  public void unselectBurrow() {
    boolean hasSelectedBurrow = this.selectedBurrow.isPresent();
    Burrow burrow = null;
    if (hasSelectedBurrow && seesBurrows()) {
      burrow = this.selectedBurrow.get();
    }
    this.selectedBurrow = Optional.empty();
    if (hasSelectedBurrow && seesBurrows()) {
      burrow.setVisible(this.player);
    }
  }

  public Optional<Burrow> getSelectedBurrow() {
    return this.selectedBurrow;
  }

  public void setInCareCenterEffectArea(boolean inCareCenterEffectArea) {
    if (inCareCenterEffectArea) {
      this.booleans |= 0b0_1000_0000;
    } else {
      this.booleans ^= 0b0_1000_0000;
    }
  }

  public void toggleInCareCenterEffectArea() {
    setInCareCenterEffectArea(!isInCareCenterEffectArea());
  }

  public boolean isMuted() {
    return toBoolean(this.booleans & 0b0_0000_0001);
  }

  public void toggleMute() {
    if (isMuted()) {
      this.booleans ^= 0b0_0000_0001;
    } else {
      this.booleans |= 0b0_0000_0001;
    }
  }

  public boolean isGod() {
    return toBoolean(this.booleans & 0b0_0000_0010);
  }

  public void toggleGod() {
    if (isGod()) {
      this.booleans ^= 0b0_0000_0010;
    } else {
      this.booleans |= 0b0_0000_0010;
    }
  }

  public boolean spiesMp() {
    return toBoolean(this.booleans & 0b0_0000_0100);
  }

  public void toggleSpyMp() {
    if (spiesMp()) {
      this.booleans ^= 0b0_0000_0100;
    } else {
      this.booleans |= 0b0_0000_0100;
    }
  }

  public boolean wantsMP() {
    return toBoolean(this.booleans & 0b0_0000_1000);
  }

  public void toggleWantMp() {
    if (wantsMP()) {
      this.booleans ^= 0b0_0000_1000;
    } else {
      this.booleans |= 0b0_0000_1000;
    }
  }

  public boolean wantsTeam() {
    return toBoolean(this.booleans & 0b0_0001_0000);
  }

  public void toggleWantTeam() {
    if (wantsTeam()) {
      this.booleans ^= 0b0_0001_0000;
    } else {
      this.booleans |= 0b0_0001_0000;
    }
  }

  public boolean wantsRealName() {
    return toBoolean(this.booleans & 0b0_0010_0000);
  }

  public void toggleWantRealName() {
    if (wantsRealName()) {
      this.booleans ^= 0b0_0010_0000;
    } else {
      this.booleans |= 0b0_0010_0000;
    }
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    try {
      changeName.setString(1, name);
      changeName.setString(2, this.player.getName());
      changeName.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    this.name = name;
  }

  public String getDescription() {
    return this.description.orElse("Un nouveau colon");
  }

  public void setDescription(String description) {
    try {
      changeDescription.setString(1, description);
      changeDescription.setString(2, this.player.getName());
      changeDescription.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    this.description = Optional.of(description);
  }

  public ChatType getDefaultChat() {
    return this.defaultChat;
  }

  public void setDefaultChat(ChatType defaultChat) {
    this.defaultChat = defaultChat;
  }

  public void setLastCorrespondent(Player recipient) {
    this.lastCorrespondent = recipient;
  }

  public Optional<Player> getLastCorrespondent() {
    return Sponge.getServer().getOnlinePlayers().contains(this.lastCorrespondent) ? Optional.of(this.lastCorrespondent) : Optional.empty();
  }

  public Location<World> getPreviousLocation() {
    return this.previousLocation == null ? this.player.getLocation() : this.previousLocation;
  }

  public void setPreviousLocation(Location<World> location) {
    this.previousLocation = location;
  }

  public void loadFromDataBase() {
    try {
      this.skills.clear();
      this.attributes.clear();
      this.traits.clear();
      McFrConnection jdrDatabase = McFrConnection.getJdrConnection();

      getPseudonym.setString(1, this.player.getName());
      ResultSet playerData = getPseudonym.executeQuery();

      if (playerData.next()) {
        this.name = playerData.getString(2);
        String description = playerData.getString(3);
        this.description = description == null ? Optional.empty() : Optional.of(description);
        this.deaths = playerData.getInt(9);
      }
      playerData.close();

      int userId = -1;
      getUserId.setString(1, this.player.getName());
      ResultSet user = getUserId.executeQuery();
      if (user.next()) {
        userId = user.getInt(1);
      }
      user.close();

      getCharacterSheetId.setInt(1, userId);
      ResultSet characterSheet = getCharacterSheetId.executeQuery();

      if (characterSheet.next()) {
        this.booleans |= 0b1_0000_0000;
        this.sheetId = characterSheet.getInt(1);
        getCharacterSheet.setInt(1, this.sheetId);
        ResultSet skillData = getCharacterSheet.executeQuery();

        while (skillData.next()) {
          this.skills.put(Skills.getSkills().get(skillData.getString(2)), skillData.getInt(3));
        }
        skillData.close();

        getAttributes.setInt(1, this.sheetId);
        ResultSet attributeData = getAttributes.executeQuery();
        while (attributeData.next()) {
          this.attributes.put(Attributes.getAttributeFromString(attributeData.getString(1)), attributeData.getInt(2));
        }
        attributeData.close();

        getAdvantages.setInt(1, this.sheetId);
        ResultSet traitData = getAdvantages.executeQuery();

        while (traitData.next()) {
          this.traits.put(traitData.getString(1), traitData.getInt(2));
        }
        traitData.close();

        boolean hasLanguage = false;
        int maxLangLevel = 0;
        int currentLevel = 0;
        for (Language lang : Language.getLanguagesList()) {
          currentLevel = getLanguageLevel(lang);
          if (currentLevel > maxLangLevel) {
            setLanguage(lang);
            hasLanguage = true;
            maxLangLevel = currentLevel;
          }
        }
        if (!hasLanguage) {
          this.language = Language.getLanguages().get("commun");
        }

        applyJdrEffects();
      } else {
        this.player.sendMessage(Text.of(TextColors.YELLOW, "Attention, vous n'avez pas de fiche de personnage active !"));
      }
      characterSheet.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private void applyJdrEffects() {
    if (this.player.get(PotionEffectData.class).isPresent()) {
      Sponge.getCommandManager().process(Sponge.getServer().getConsole(), String.format("effect %s clear", this.player.getName()));
    }

    PotionEffectData effects = this.player.getOrCreate(PotionEffectData.class).get();

    if (hasTrait("saut_ameliore")) {
      PotionEffect effect = PotionEffect.builder().potionType(PotionEffectTypes.JUMP_BOOST).duration(50000).particles(false).build();
      effects.addElement(effect);
    }
    if (hasTrait("guerison_rapide_surnaturelle")) {
      PotionEffect effect = PotionEffect.builder().potionType(PotionEffectTypes.REGENERATION).duration(50000).amplifier(1).particles(false).build();
      effects.addElement(effect);
    }
    if (hasTrait("armure_naturelle")) {
      PotionEffect effect = PotionEffect.builder().potionType(PotionEffectTypes.RESISTANCE).duration(50000)
          .amplifier(getTraitLevel("armure_naturelle")).particles(false).build();
      effects.addElement(effect);
    }
    if (getTraitLevel("vision_dans_la_nuit") > 3 || hasTrait("vision_dans_le_noir")) {
      PotionEffect effect = PotionEffect.builder().potionType(PotionEffectTypes.NIGHT_VISION).duration(50000).particles(false).build();
      effects.addElement(effect);
    }
    if (hasTrait("boiteux_jambe_en_moins")) {
      PotionEffect effect = PotionEffect.builder().potionType(PotionEffectTypes.SLOWNESS).duration(50000).amplifier(2).particles(false).build();
      effects.addElement(effect);
    } else if (hasTrait("boiteux_jambe_abimee")) {
      PotionEffect effect = PotionEffect.builder().potionType(PotionEffectTypes.SLOWNESS).duration(50000).amplifier(1).particles(false).build();
      effects.addElement(effect);
    }
    if (hasTrait("aveugle")) {
      PotionEffect effect = PotionEffect.builder().potionType(PotionEffectTypes.BLINDNESS).duration(50000).particles(false).build();
      effects.addElement(effect);
    }
    this.player.offer(effects);
  }

  /**
   * Tue le personnage et désactive sa fiche de personnage.
   * 
   * @param player
   *          Joueur dont il faut le personnage.
   * @return
   */
  public void killCharacter(String reason) {
    addTrait("mort", 1);
    Sponge.getServiceManager().provide(WhitelistService.class).get().removeProfile(this.player.getProfile());
    this.player.kick(Text.of("Vous êtes mort." + (reason.equals("") ? "" : " (" + reason + ")")));
  }

  public int getLightLevelAtHeadLocation() {
    Location<World> loc = this.player.getLocation();
    Location<World> newLoc = new Location<>(loc.getExtent(), loc.getBlockX(), loc.getBlockY() + 1, loc.getBlockZ());
    System.out.println(newLoc.getProperty(LightEmissionProperty.class).get().getValue());
    return newLoc.getProperty(LightEmissionProperty.class).get().getValue();
  }

  public int getSkillLevel(Skills skill) {
    int skillLevel = -3;
    if (this.skills.containsKey(skill)) {
      skillLevel = this.skills.get(skill);
    }
    return skillLevel;
  }

  public int getLanguageLevel(Language lang) {
    String langName = lang.getName();

    if (hasTrait(langName + "_natif"))
      return 3;
    else if (hasTrait(langName + "_courant"))
      return 2;
    else if (hasTrait(langName + "_basique"))
      return 1;

    return 0;
  }

  public void setLanguage(Language language) {
    this.language = language;
  }

  public Language getLanguage() {
    return this.language;
  }

  public int getAttributePoints(Attributes attribute) {
    return this.attributes.get(attribute);
  }

  public boolean hasTrait(String trait) {
    return this.traits.containsKey(trait);
  }

  public int getTraitLevel(String trait) {
    if (hasTrait(trait))
      return this.traits.get(trait);
    return 0;
  }

  public void addTrait(String trait, int level) {
    if (!hasTrait(trait)) {
      this.traits.put(trait, level);
      try {
        killCharacter.setInt(1, this.sheetId);
        killCharacter.setString(2, trait);
        killCharacter.setInt(3, level);
        killCharacter.execute();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  public int getNumberOfDeaths() {
    return this.deaths;
  }

  public void incrementNumberOfDeaths() {
    this.deaths++;
    try {
      incrementDeaths.setInt(1, this.deaths);
      incrementDeaths.setString(2, this.player.getName());
      incrementDeaths.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public String getUsedWeapon() {
    return "pugilat"; // TODO
  }

  public int getArmorModifier() {
    return 0; // TODO
  }

  public int getLightMalus() {
    int lightLevel = getLightLevelAtHeadLocation();

    if (lightLevel >= 0 && lightLevel <= 3) {
      lightLevel = -9;
    } else if (lightLevel >= 12 && lightLevel <= 15) {
      lightLevel = 0;
    } else {
      lightLevel -= 12;
    }

    return lightLevel;
  }

  @Override
  public int hashCode() {
    return 31 + (this.player == null ? 0 : this.player.hashCode());
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;

    return this.player.equals(((McFrPlayer) obj).player);
  }

  @Override
  public String toString() {
    return this.player.toString();
  }
}