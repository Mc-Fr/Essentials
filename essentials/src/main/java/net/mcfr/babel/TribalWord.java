package net.mcfr.babel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;

import net.mcfr.utils.McFrConnection;

/**
 * Entrée dans le dictionnaire de la langue tribale
 */
public class TribalWord {
  /**
   * Dictionnaire de toutes les entrées de la langue tribale.
   */
  private static List<TribalWord> dictionary = new ArrayList<>();
  private final static Random rand = new Random();
  /**
   * Mot dans la langue tribale
   */
  private String word;
  /**
   * Mot dans la langue commune
   */
  private String translation;
  /**
   * Niveau de difficulté du mot, de 0 à 3
   */
  private int level;

  private TribalWord(String word, String translation, int level) {
    this.word = word;
    this.translation = translation;
    this.level = level;
  }

  /**
   * @return Mot dans la langue tribale
   */
  public String getWord() {
    return this.word;
  }

  /**
   * @return Mot dans la langue commune
   */
  public String getTranslation() {
    return this.translation;
  }

  public int getLevel() {
    return this.level;
  }

  /**
   * Génère une chaîne de caractère à afficher pour donner la traduction d'un mot.
   */
  public String getTranslationString(boolean withLevel) {
    return this.word + " = " + this.translation + (withLevel ? " (" + this.level + ")" : "");
  }

  /**
   * Enregistre le mot dans la base de donnée.
   */
  private void registerInDatabase() {
    try (Connection jdrConnection = McFrConnection.getJdrConnection()){
      PreparedStatement addWord = jdrConnection.prepareStatement("INSERT INTO langue_tribale VALUES (?, ?, ?)");
      addWord.setString(1, this.word);
      addWord.setString(2, this.translation);
      addWord.setInt(3, this.level);
      addWord.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Charge tous les mots présents en base de donnée.
   */
  public static void loadFromDatabase() {
    try (Connection jdrConnection = McFrConnection.getJdrConnection()){
      ResultSet tribalData = jdrConnection.prepareStatement("SELECT tribal, commun, level FROM TribalDictionnary").executeQuery();

      while (tribalData.next()) {
        dictionary.add(new TribalWord(tribalData.getString("tribal"), tribalData.getString("commun"), tribalData.getInt("level")));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Génère une liste de tous les mots d'un certain niveau.
   * @param level Niveau des mots à rechercher
   * @return Liste des mots du niveau spécifié
   */
  public static List<TribalWord> getByLevel(int level) {
    List<TribalWord> result = new ArrayList<>();
    dictionary.stream().filter(w -> w.getLevel() == level).forEach(w -> result.add(w));
    return result;
  }

  /**
   * Génère une sélection aléatoire d'un nombre de mots du niveau spécifié.
   * @param numberOfWords Nombre de mots à sélectionner
   * @param level Niveau des mots à sélectionner
   * @return Liste aléatoire de mots du niveau spécifié
   */
  public static List<TribalWord> getRandomsByLevel(int numberOfWords, int level) {
    List<TribalWord> levelWords = getByLevel(level);
    List<TribalWord> result = new ArrayList<>();

    numberOfWords = Math.min(numberOfWords, levelWords.size());

    while (result.size() < numberOfWords) {
      TribalWord word = levelWords.get(rand.nextInt(levelWords.size()));
      result.add(word);
      levelWords.remove(word);
    }

    return result;
  }

  /**
   * Crée un nouveau mot, l'ajoute au dictionnaire et fais appel à registerToDatabase().
   * @param tribal Mot en langue tribale
   * @param common Mot en langue commune
   * @param level Niveau de difficulté du mot
   * @return Optionnel contenant le mot créé, ou vide si le mot existe déjà
   */
  public static Optional<TribalWord> add(String tribal, String common, int level) {
    if (getByCommonWord(common).isPresent() || getByTribalWord(tribal).isPresent())
      return Optional.empty();
    TribalWord word = new TribalWord(tribal, common, level);
    dictionary.add(word);
    word.registerInDatabase();
    return Optional.of(word);
  }
  
  /**
   * Trouve le TribalWord correspondant au mot en langue commune renseigné.
   * @param common Mot en langue commune
   * @return Optionnel contenant le mot trouvé, ou vide si le mot n'existe pas
   */
  public static Optional<TribalWord> getByCommonWord(String common) {
    return dictionary.stream().filter(w -> normalize(w.getTranslation()).equals(normalize(common))).findFirst();
  }

  /**
   * Trouve le TribalWord correspondant au mot en langue tribale renseigné.
   * @param tribal Mot en langue tribale
   * @return Optionnel contenant le mot trouvé, ou vide si le mot n'existe pas
   */
  public static Optional<TribalWord> getByTribalWord(String tribal) {
    return dictionary.stream().filter(w -> normalize(w.getWord()).equals(normalize(tribal))).findFirst();
  }

  /**
   * Retire les accents et passe en minuscule la chaîne de caractère renseignée.
   * @param input La chaîne à transformer
   * @return La chaîne transformée
   */
  private static String normalize(String input) {
    return StringUtils.stripAccents(input).toLowerCase();
  }
}
