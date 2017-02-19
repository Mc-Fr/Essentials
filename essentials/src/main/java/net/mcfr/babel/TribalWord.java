package net.mcfr.babel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;

import net.mcfr.utils.McFrConnection;

public class TribalWord {
  private static List<TribalWord> words = new LinkedList<>();
  private final static Random rand = new Random();
  private String word;
  private String translation;
  private int level;

  private TribalWord(String word, String translation, int level) {
    this.word = word;
    this.translation = translation;
    this.level = level;
  }

  public String getWord() {
    return this.word;
  }

  public String getTranslation() {
    return this.translation;
  }

  public int getLevel() {
    return this.level;
  }

  public String getTranslationString() {
    return this.word + " = " + this.translation;
  }

  public void registerToDatabase() {
    try {
      PreparedStatement addWord = McFrConnection.getJdrConnection().getConnection().prepareStatement("INSERT INTO langue_tribale VALUES (?, ?, ?)");
      addWord.setString(1, this.word);
      addWord.setString(2, this.translation);
      addWord.setInt(3, this.level);
      addWord.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void loadFromDatabase() {
    try {
      ResultSet tribalData = McFrConnection.getJdrConnection().executeQuery("SELECT tribal, commun, level FROM TribalDictionnary");

      while (tribalData.next()) {
        words.add(new TribalWord(tribalData.getString("tribal"), tribalData.getString("commun"), tribalData.getInt("level")));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static List<TribalWord> getByLevel(int level) {
    List<TribalWord> result = new LinkedList<>();
    words.stream().filter(w -> w.getLevel() == level).forEach(w -> result.add(w));
    return result;
  }

  public static List<TribalWord> getRandomsByLevel(int numberOfWords, int level) {
    List<TribalWord> levelWords = getByLevel(level);
    List<TribalWord> result = new LinkedList<>();

    numberOfWords = Math.min(numberOfWords, levelWords.size());

    while (result.size() < numberOfWords) {
      TribalWord word = levelWords.get(rand.nextInt(levelWords.size()));
      result.add(word);
      levelWords.remove(word);
    }

    return result;
  }

  public static Optional<TribalWord> add(String tribal, String common, int level) {
    if (getTribalTranslation(common).isPresent() || getCommonTranslation(tribal).isPresent())
      return Optional.empty();
    TribalWord word = new TribalWord(tribal, common, level);
    words.add(word);
    word.registerToDatabase();
    return Optional.of(word);
  }

  public static Optional<String> getTribalTranslation(String common) {
    Optional<TribalWord> optWord = words.stream().filter(w -> normalize(w.getTranslation()).equals(normalize(common))).findFirst();
    if (optWord.isPresent())
      return Optional.of(optWord.get().getWord());
    else
      return Optional.empty();
  }

  public static Optional<String> getCommonTranslation(String tribal) {
    Optional<TribalWord> optWord = words.stream().filter(w -> normalize(w.getWord()).equals(normalize(tribal))).findFirst();
    if (optWord.isPresent())
      return Optional.of(optWord.get().getTranslation());
    else
      return Optional.empty();
  }

  public static String normalize(String input) {
    return StringUtils.stripAccents(input).toLowerCase();
  }
}
