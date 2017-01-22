package net.mcfr.babel;

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

  //TODO : pSQL et ReqPrep

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
      ResultSet langData = McFrConnection.getJdrConnection().executeQuery("SELECT * FROM fiche_perso_langue");
      while (langData.next()) {
        ArrayList<Character> symbols = new ArrayList<>();
        ResultSet symbolsData = McFrConnection.getJdrConnection()
            .executeQuery("SELECT symbol FROM fiche_perso_langue_symbole WHERE lang = \"" + langData.getString(1) + "\"");
        
        while (symbolsData.next()) {
          symbols.add(symbolsData.getString(1).charAt(0));
        }
        if (symbols.size() == 0) {
          symbols.add('#');
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

  public String transformWord(String word, int languageLevel) {
    String result = "";
    int symbolsSize = this.symbols.size();
    int wordLength = word.length();

    if (languageLevel == 0 || (languageLevel == 1 && (wordLength < 4 || wordLength > 7)) || (languageLevel == 2 && wordLength > 8)) {
      for (int i = 0; i < wordLength; i++) {
        result += this.symbols.get(rand.nextInt(symbolsSize));
      }
      return result;
    } else {
      return word;
    }
  }
}
