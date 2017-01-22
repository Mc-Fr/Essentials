package net.mcfr.tribe;

import java.util.LinkedList;

public class Util {
  
  public static void trocIntertribal(LinkedList<Tribe> tribes) {
    for (int i = 0; i < tribes.size(); i++) {
      for (int j = i + 1; j < tribes.size(); j++) {
        tribes.get(i).trocIntertribal(tribes.get(j));
      }
    }
  }
}
