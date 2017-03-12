package net.mcfr.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import net.mcfr.babel.Language;
import net.mcfr.burrows.Burrow;
import net.mcfr.chat.ChatType;
import net.mcfr.expedition.ExpeditionSystem;
import net.mcfr.roleplay.Attributes;
import net.mcfr.roleplay.Health;
import net.mcfr.roleplay.Skills;

public class McFrPlayer {
  private static List<McFrPlayer> players = new ArrayList<>();
  private final static int EFFECT_DURATION = 800000;

  private Player player;
  private int deaths;

  /**
   * Représente tous les booléens présents dans la classe.
   * <table style="border-collapse: collapse">
   * <tr>
   * <td style="border: 1px solid black">isAuthorizedToLeaveArea</td>
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
   * <td style="border: 1px solid black">0b10_0000_0000</td>
   * <td style="border: 1px solid black">0b01_0000_0000</td>
   * <td style="border: 1px solid black">0b00_1000_0000</td>
   * <td style="border: 1px solid black">0b00_0100_0000</td>
   * <td style="border: 1px solid black">0b00_0010_0000</td>
   * <td style="border: 1px solid black">0b00_0001_0000</td>
   * <td style="border: 1px solid black">0b00_0000_1000</td>
   * <td style="border: 1px solid black">0b00_0000_0100</td>
   * <td style="border: 1px solid black">0b00_0000_0010</td>
   * <td style="border: 1px solid black">0b00_0000_0001</td>
   * </tr>
   * </table>
   */
  private int booleans;
  private ExpeditionSystem.States expeditionState;
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
  private Health health;
  private Location<World> previousLocation;
  private long lastBreathTime;
  private long readDescriptionTime;

  public static void addPlayer(McFrPlayer player) {
    players.add(player);
  }

  public static void removePlayer(Player player) {
    players.remove(new McFrPlayer(player));
  }

  public static List<McFrPlayer> getMcFrPlayers() {
    return new ArrayList<>(players);
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

  public McFrPlayer(Player player) {
    Objects.requireNonNull(player);
    this.player = player;
    this.booleans = 0b00_0001_1000;
    this.name = player.getName();
    this.description = Optional.empty();
    this.expeditionState = ExpeditionSystem.States.IN_AREA;
    this.defaultChat = ChatType.MEDIUM;
    this.sheetId = -1;
    this.skills = new HashMap<>();
    this.attributes = new HashMap<>();
    this.traits = new HashMap<>();
    this.previousLocation = null;
    this.selectedBurrow = Optional.empty();
    this.lastBreathTime = 0;
    this.readDescriptionTime = 0;
    this.health = new Health(1000);
  }

  public Player getPlayer() {
    return this.player;
  }

  public ExpeditionSystem.States getExpeditionState() {
    return this.expeditionState;
  }

  public void setExpeditionState(ExpeditionSystem.States state) {
    this.expeditionState = state;
  }

  public boolean isInCareCenterEffectArea() {
    return (this.booleans & 0b00_1000_0000) == 0b00_1000_0000;
  }

  public boolean hasCharacter() {
    return (this.booleans & 0b01_0000_0000) == 0b01_0000_0000;
  }

  public void toggleSeesBurrows() {
    this.booleans ^= 0b00_0100_0000;
  }

  public boolean seesBurrows() {
    return (this.booleans & 0b00_0100_0000) == 0b00_0100_0000;
  }

  public void selectBurrow(Burrow burrow) {
    unselectBurrow();
    this.selectedBurrow = Optional.of(burrow);
    if (seesBurrows()) {
      burrow.setVisible(this.player);
    }
  }

  public void unselectBurrow() {
    Optional<Burrow> burrow = this.selectedBurrow;
    this.selectedBurrow = Optional.empty();
    if (burrow.isPresent() && seesBurrows()) {
      burrow.get().setVisible(this.player);
    }
  }

  public Optional<Burrow> getSelectedBurrow() {
    return this.selectedBurrow;
  }

  public void setInCareCenterEffectArea(boolean inCareCenterEffectArea) {
    if (!(inCareCenterEffectArea == isInCareCenterEffectArea())) {
      this.booleans ^= 0b00_1000_0000;
    }
  }

  public void toggleInCareCenterEffectArea() {
    setInCareCenterEffectArea(!isInCareCenterEffectArea());
  }

  public boolean isMuted() {
    return (this.booleans & 0b00_0000_0001) == 0b00_0000_0001;
  }

  public void toggleMute() {
    this.booleans ^= 0b00_0000_0001;
  }

  public boolean isGod() {
    return (this.booleans & 0b00_0000_0010) == 0b00_0000_0010;
  }

  public void toggleGod() {
    this.booleans ^= 0b00_0000_0010;
  }

  public boolean spiesMp() {
    return (this.booleans & 0b00_0000_0100) == 0b00_0000_0100;
  }

  public void toggleSpyMp() {
    this.booleans ^= 0b00_0000_0100;
  }

  public boolean wantsMP() {
    return (this.booleans & 0b00_0000_1000) == 0b00_0000_1000;
  }

  public void toggleWantMp() {
    this.booleans ^= 0b00_0000_1000;
  }

  public boolean wantsTeam() {
    return (this.booleans & 0b00_0001_0000) == 0b00_0001_0000;
  }

  public void toggleWantTeam() {
    this.booleans ^= 0b00_0001_0000;
  }

  public boolean wantsRealName() {
    return (this.booleans & 0b00_0010_0000) == 0b00_0010_0000;
  }

  public void toggleWantRealName() {
    this.booleans ^= 0b00_0010_0000;
  }

  public boolean isAuthorizedToLeaveArea() {
    return (this.booleans & 0b10_0000_0000) == 0b10_0000_0000;
  }

  public void toggleAuthorizedToLeaveArea() {
    this.booleans ^= 0b10_0000_0000;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    try (Connection serverConnection = McFrConnection.getConnection()) {
      PreparedStatement changeName = serverConnection.prepareStatement("UPDATE Player SET name = ? WHERE pseudonym = ?");
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
    try (Connection serverConnection = McFrConnection.getConnection()) {

      PreparedStatement changeDescription = serverConnection.prepareStatement("UPDATE Player SET description = ? WHERE pseudonym = ?");
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
      Connection serverConnection = McFrConnection.getConnection();
      Connection jdrConnection = McFrConnection.getConnection();
      PreparedStatement getPseudonym = serverConnection.prepareStatement("SELECT * FROM Player WHERE pseudonym = ?");
      PreparedStatement getUserId = jdrConnection
          .prepareStatement("SELECT user_id FROM phpbb_users PU, account_link AL WHERE AL.forum = PU.username AND AL.minecraft = ?");
      PreparedStatement getCharacterSheetId = jdrConnection
          .prepareStatement("SELECT id, health FROM fiche_perso_personnage WHERE id_user = ? AND active = 1");
      PreparedStatement getCharacterSheet = jdrConnection
          .prepareStatement("SELECT * FROM fiche_perso_personnage_competence WHERE id_fiche_perso_personnage = ?");
      PreparedStatement getAttributes = jdrConnection
          .prepareStatement("SELECT attribut, level FROM fiche_perso_personnage_attribut WHERE id_fiche_perso_personnage = ?");
      PreparedStatement getAdvantages = jdrConnection
          .prepareStatement("SELECT avantage,value FROM fiche_perso_personnage_avantage WHERE id_fiche_perso_personnage = ?");
      PreparedStatement registerPlayer = serverConnection
          .prepareStatement("INSERT INTO Player(uuid, pseudonym, name, description, gender, race, deaths) VALUES (?,?,?,?,?,?,?)");

      getPseudonym.setString(1, this.player.getName());
      ResultSet playerData = getPseudonym.executeQuery();

      if (playerData.next()) {
        this.name = playerData.getString(3);
        String description = playerData.getString(4);
        this.description = description == null ? Optional.empty() : Optional.of(description);
        this.deaths = playerData.getInt(7);
      } else {
        this.name = this.player.getName();
        this.description = Optional.of("Un nouveau colon");
        this.deaths = 0;

        registerPlayer.setString(1, this.player.getUniqueId().toString());
        registerPlayer.setString(2, this.name);
        registerPlayer.setString(3, this.name);
        registerPlayer.setString(4, this.description.get());
        registerPlayer.setString(5, "M");
        registerPlayer.setString(6, "human");
        registerPlayer.setInt(7, this.deaths);
        registerPlayer.execute();
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
        int currentHealth = characterSheet.getInt(2);
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

        if (getLanguageLevel(Language.getLanguages().get("commun")) > 0) {
          setLanguage(Language.getLanguages().get("commun"));
        } else {
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
        }

        applyJdrEffects();
        this.health.refresh(this);
        this.health.set(this, currentHealth);
      } else {
        this.player.sendMessage(Text.of(TextColors.YELLOW, "Attention, vous n'avez pas de fiche de personnage active !"));
      }
      characterSheet.close();

      serverConnection.close();
      jdrConnection.close();
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
      PotionEffect effect = PotionEffect.builder().potionType(PotionEffectTypes.JUMP_BOOST).duration(EFFECT_DURATION).particles(false).build();
      effects.addElement(effect);
    }
    if (hasTrait("guerison_rapide_surnaturelle")) {
      PotionEffect effect = PotionEffect.builder().potionType(PotionEffectTypes.REGENERATION).duration(EFFECT_DURATION).amplifier(1).particles(false)
          .build();
      effects.addElement(effect);
    }
    if (hasTrait("armure_naturelle")) {
      PotionEffect effect = PotionEffect.builder().potionType(PotionEffectTypes.RESISTANCE).duration(EFFECT_DURATION)
          .amplifier(getTraitLevel("armure_naturelle")).particles(false).build();
      effects.addElement(effect);
    }
    if (getTraitLevel("vision_dans_la_nuit") > 3 || hasTrait("vision_dans_le_noir")) {
      PotionEffect effect = PotionEffect.builder().potionType(PotionEffectTypes.NIGHT_VISION).duration(EFFECT_DURATION).particles(false).build();
      effects.addElement(effect);
    }
    if (hasTrait("boiteux_jambe_en_moins")) {
      PotionEffect effect = PotionEffect.builder().potionType(PotionEffectTypes.SLOWNESS).duration(EFFECT_DURATION).particles(false).build();
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
    this.player.kick(Text.of("Vous êtes mort." + (reason.equals("") ? "" : " (" + reason + ")")));
  }

  public int getSkillLevel(Skills skill, Optional<Attributes> optAttribute) {
    List<Integer> scores = new ArrayList<>();
    if (this.skills.containsKey(skill)) {
      scores.add(getAttributePoints(optAttribute.orElse(skill.getAttribute())) + this.skills.get(skill));
    } else {
      scores.add(getAttributePoints(optAttribute.orElse(skill.getAttribute())) - 3 + skill.getDifficulty());
    }

    for (Map.Entry<Skills, Integer> dependency : skill.getDependencies().entrySet()) {
      Skills depSkill = dependency.getKey();
      int depScore = dependency.getValue();

      if (this.skills.containsKey(depSkill)) {
        scores.add(getAttributePoints(optAttribute.orElse(depSkill.getAttribute())) + this.skills.get(depSkill) + depScore);
      } else {
        scores.add(getAttributePoints(optAttribute.orElse(depSkill.getAttribute())) - 3 + depSkill.getDifficulty() + depScore);
      }
    }

    return scores.stream().max((s1, s2) -> Integer.compare(s1, s2)).get();
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

  public String getTraitsString() {
    String result = "Traits :";

    for (Entry<String, Integer> entry : this.traits.entrySet()) {
      result += "\n- " + entry.getKey() + " : " + entry.getValue();
    }

    return result;
  }

  public String getSkillsString() {
    String result = "Compétences :";

    for (Skills skill : this.skills.keySet()) {
      result += "\n- " + skill.getDisplayName() + " : " + getSkillLevel(skill, Optional.empty());
    }

    return result;
  }

  public String getAttributesString() {
    String result = "Attributs :";

    for (Attributes attribute : this.attributes.keySet()) {
      result += "\n- " + attribute.getName() + " : " + getAttributePoints(attribute);
    }

    return result;
  }

  public void addTrait(String trait, int level) {
    if (!hasTrait(trait)) {
      this.traits.put(trait, level);
      try (Connection jdrConnection = McFrConnection.getConnection()) {
        PreparedStatement addTrait = jdrConnection.prepareStatement("INSERT INTO fiche_perso_personnage_avantage VALUES (?, ?, ?)");
        addTrait.setInt(1, this.sheetId);
        addTrait.setString(2, trait);
        addTrait.setInt(3, level);
        addTrait.execute();
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
    try (Connection serverConnection = McFrConnection.getConnection()) {
      PreparedStatement incrementDeaths = serverConnection.prepareStatement("UPDATE Player SET deaths = ? WHERE pseudonym = ?");
      incrementDeaths.setInt(1, this.deaths);
      incrementDeaths.setString(2, this.player.getName());
      incrementDeaths.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public Health getHealth() {
    return this.health;
  }

  public int getArmorModifier() {
    return 0; // TODO
  }

  public int getSheetId() {
    return this.sheetId;
  }

  public long getReadDescriptionTime() {
    return this.readDescriptionTime;
  }

  public void updateReadDescriptionTime() {
    this.readDescriptionTime = Calendar.getInstance().getTime().getTime();
  }

  public long getLastBreathTime() {
    return this.lastBreathTime;
  }

  public void updateLastBreathTime() {
    this.lastBreathTime = Calendar.getInstance().getTime().getTime();
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