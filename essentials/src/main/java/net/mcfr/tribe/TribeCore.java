package net.mcfr.tribe;

import java.util.LinkedList;

public class TribeCore {
  private LinkedList<Tribe> tribes;
  //private TribeDataReader reader;
  //private TribeDataWriter writer;

  public TribeCore() {
    //reader = new SimulationReader("ressources/ressources.txt");
    //tribes = reader.readData();
  }

  public void nextTurn(boolean isEvening) {
    for (Tribe t : this.tribes) {
      t.nextTurn(this.tribes, true);
    }
    trocIntertribal(this.tribes);

  }

  public static void trocIntertribal(LinkedList<Tribe> tribes) {
    for (int i = 0; i < tribes.size(); i++) {
      for (int j = i + 1; j < tribes.size(); j++) {
        tribes.get(i).trocIntertribal(tribes.get(j));
      }
    }
  }

  public void reload() {

  }
}
