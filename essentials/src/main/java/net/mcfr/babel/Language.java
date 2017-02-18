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
      ResultSet langData = McFrConnection.getJdrConnection().executeQuery("SELECT name, displayName, alias FROM Languages");
      while (langData.next()) {
        String langName = langData.getString(1);
        String langDisplayName = langData.getString(2);
        String langAlias = langData.getString(3);

        PreparedStatement loadLanguages = McFrConnection.getJdrConnection().getConnection()
            .prepareStatement("SELECT symbol FROM fiche_perso_langue_symbole WHERE lang = ?");

        loadLanguages.setString(1, langName);
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

        languages.put(langAlias, new Language(langName, langDisplayName, langAlias, symbols));

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
    boolean isTranslating = true;
    char[] characters = text.toCharArray();
    List<Character> separators = Arrays.asList(' ', '.', ',', ';', '?', '!', '~', '\'', ':', '-');

    for (char c : characters) {
      if (c == '[' && isTranslating) {
        result += transformWord(word, languageLevel);
        word = "";
        isTranslating = false;
        
      } else if (c == ']' && !isTranslating) {
        result += word;
        word = "";
        isTranslating = true;
        
      } else if (isTranslating && separators.contains(c)) {
        result += transformWord(word, languageLevel) + c;
        word = "";
        
      } else {
        word += c;
      }
    }
    if (isTranslating) {
      result += transformWord(word, languageLevel);
    } else {
      result += word;
    }
    return result;
  }

  private String transformWord(String word, int languageLevel) {
    String result = "";
    int wordLength = word.length();
    float jamming = computeJamming(languageLevel, wordLength);

    for (char c : word.toCharArray()) {
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

    switch (languageLevel) {
    case 0:
      return 1.0F;
    case 1:
      value = -0.6F * wordLength + 4;
      break;
    case 2:
      value = -0.8F * wordLength + 8.5F;
      break;
    default:
      return 0.0F;
    }

    return 0.9F * (1.0F / (1.0F + (float) Math.exp(value))) + 0.1F;
  }
}
