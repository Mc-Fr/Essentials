package net.mcfr.babel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.mcfr.utils.McFrConnection;

public class Language {
  private static Map<String, Language> languages = new HashMap<>();
  private static Random rand = new Random();
  private final static PreparedStatement loadLanguages;

  static {
    loadLanguages = McFrConnection.getJdrConnection().prepare("SELECT symbol FROM fiche_perso_langue_symbole WHERE lang = ?");
  }

  private String name;
  private String displayName;
  private String alias;
  private ArrayList<Character> symbols;

  public Language(String name, String displayName, String alias, ArrayList<Character> symbols) {
    this.name = name;
    this.displayName = displayName;
    this.alias = alias;
    this.symbols = symbols;
  }

  public String getName() {
    return this.name;
  }

  public String getDisplayName() {
    return this.displayName;
  }

  public String getAlias() {
    return this.alias;
  }

  public static void loadFromDatabase() {
    try {
      ResultSet langData = McFrConnection.getJdrConnection().executeQuery("SELECT * FROM fiche_perso_langue");
      while (langData.next()) {
        loadLanguages.setString(1, langData.getString(1));
        ResultSet symbolsData = loadLanguages.executeQuery();

        ArrayList<Character> symbols = new ArrayList<>();
        while (symbolsData.next()) {
          symbols.add(symbolsData.getString(1).charAt(0));
        }
        if (symbols.size() == 0) {
          symbols.add('ϑ');
          symbols.add('ε');
          symbols.add('ρ');
          symbols.add('σ');
        }

        languages.put(langData.getString(3), new Language(langData.getString(1), langData.getString(2), langData.getString(3), symbols));

        symbolsData.close();
      }
      langData.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static Map<String, Language> getLanguages() {
    return languages;
  }

  public static Collection<Language> getLanguagesList() {
    return languages.values();
  }

  public String transformMessage(String text, int languageLevel) {
    String result = "";
    String word = "";
    char[] characters = text.toCharArray();
    List<Character> separators = Arrays.asList(' ', '.', ',', ';', '?', '!', '~', '\'', ':', '-');

    for (char c : characters) {
      if (separators.contains(c)) {
        result += transformWord(word, languageLevel) + c;
        word = "";
      } else {
        word += c;
      }
    }
    result += transformWord(word, languageLevel);
    return result;
  }

  private String transformWord(String word, int languageLevel) {
    String result = "";
    int wordLength = word.length();
    float jamming = computeJamming(languageLevel, wordLength);
    
    for(char c : word.toCharArray()) {
      if (rand.nextFloat() < jamming) {
        result += this.symbols.get(rand.nextInt(this.symbols.size()));
      } else {
        result += c;
      }
    }
    
    return result;
  }

  public float computeJamming(int languageLevel, int wordLength) {
    double value;
    
    switch(languageLevel) {
    case 0:
      return 1.0F;
    case 1:
      value = -0.5F * wordLength + 3;
      break;
    case 2:
      value = -0.7F * wordLength + 7;
      break;
    default:
      return 0.0F;
    }
    
    return (float) (1.0F/(1.0F + Math.exp(value)));
  }
}
