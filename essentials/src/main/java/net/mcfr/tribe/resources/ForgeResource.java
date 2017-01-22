package net.mcfr.tribe.resources;
import java.util.Map;

import net.mcfr.tribe.Population;

public class ForgeResource extends Resource {
	private boolean isDisponible;
	protected float forgeCost;
	
	public ForgeResource (Map<String, Float> data, String name) {
		super (data, name);
		
		isDisponible = false;
		forgeCost = data.get("forgeCost");
	}
	
	public void calculateValue (float priceFlexibility) {
		super.calculateValue(priceFlexibility);
		saleableQuantity = Math.max(0, stock - forgeCost * exchangeThreshold);
	}
	
	public void consume (Population population, Weapon weapon) {
		removeFromStock( population.getWorkers() * forgeCost * weapon.getProduction());
	}
	
	public void calculateDisponibility (Population population, Weapon weapon) {
		isDisponible = !(stock < weapon.getProduction() * population.getWorkers() * forgeCost);
	}
	
	public void calculateNeed (Population population, Weapon weapon) {
		need = (weapon.getNeed() * forgeCost - stock) / (1 + production * population.getWorkers());
	}

	public boolean isDisponible() {
		return isDisponible;
	}
	
	public String toString () {
		return "\t-- ForgeRessource -- " + super.toString() + "\n\t\tForge Cost :\t" + forgeCost + "\tDisponibility :\t" + String.valueOf(isDisponible) + "\n";
	}
}
