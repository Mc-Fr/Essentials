package net.mcfr.tribe;

import java.util.LinkedList;

public class TribeCore {
	private LinkedList<Tribe> tribes;
	//private TribeDataReader reader;
	//private TribeDataWriter writer;
	
	public TribeCore () {
		//reader = new SimulationReader("ressources/ressources.txt");
		//tribes = reader.readData();
	}
	
	public void nextTurn(boolean isEvening) {
		for (Tribe t : tribes) {
			t.nextTurn(tribes,  true);
		}
		Util.trocIntertribal(tribes);
		
	}
	
	public void reload() {
		
	}
}
