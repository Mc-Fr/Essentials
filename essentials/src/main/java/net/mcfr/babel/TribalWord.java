package net.mcfr.babel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import net.mcfr.utils.McFrConnection;

public class TribalWord {
  private static List<TribalWord> words = new LinkedList<>();
  private final static Random rand = new Random();
  private final static PreparedStatement addWord  = McFrConnection.getJdrConnection().prepare("INSERT INTO langue_tribale VALUES (?, ?, ?)");;
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
      ResultSet tribalData = McFrConnection.getJdrConnection().executeQuery("SELECT * FROM langue_tribale");

      while (tribalData.next()) {
        words.add(new TribalWord(tribalData.getString(1), tribalData.getString(2), tribalData.getInt(3)));
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
    if (getTribalTranslation(common).isPresent() || getCommonTranslation(tribal).isPresent()) {
      return Optional.empty();
    }
    TribalWord word = new TribalWord(tribal, common, level);
    words.add(word);
    word.registerToDatabase();
    return Optional.of(word);
  }
  
  public static Optional<String> getTribalTranslation(String common) {
    Optional<TribalWord> optWord = words.stream().filter(w -> w.getTranslation().equals(common)).findFirst();
    if (optWord.isPresent()) {
      return Optional.of(optWord.get().getWord());
    } else {
      return Optional.empty();
    }
  }
  
  public static Optional<String> getCommonTranslation(String tribal) {
    Optional<TribalWord> optWord = words.stream().filter(w -> w.getWord().equals(tribal)).findFirst();
    if (optWord.isPresent()) {
      return Optional.of(optWord.get().getTranslation());
    } else {
      return Optional.empty();
    }
  }
}
