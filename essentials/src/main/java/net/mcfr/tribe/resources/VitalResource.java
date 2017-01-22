package net.mcfr.tribe.resources;
import static net.mcfr.tribe.Coefficients.*;

import java.util.Map;

import net.mcfr.tribe.Population;

public class VitalResource extends ConsumableResource {
	protected boolean isDisponible;
	protected float formationCost;
	
	public VitalResource (Map<String, Float> data, String name) {
		super (data, name);
		
		isDisponible = false;
		formationCost = data.get("formationCost");
	}
	
	public void calculateValue (Population population, float priceFlexibility) {
		super.calculateValue(priceFlexibility); 
		saleableQuantity = Math.max(0, stock - consumption * population.getInhabitants() * exchangeThreshold);
	}
	
	public void calculateDisponibility (Population population) {
		isDisponible = !(stock <= consumption * (securityThreshold + 1) * population.getInhabitants());
	}
	
	public void calculateNeed (Population population, float fightersNeed) {
		need = securityThreshold * VITAL_NEED_COEF.getValue() - stock / (1 + consumption * population.getInhabitants()) + fightersNeed * formationCost / (1 + production * population.getWorkers());
		if (stock == 0) {	population.increaseVitalResourcesLacks();}
	}
	
	public boolean isDisponible() {
		return isDisponible;
	}
	
	public boolean isAvailableForTraining (float fightersFormationPerDay) {
		return ((isDisponible && !(stock < fightersFormationPerDay * formationCost)) || formationCost == 0f);
	}
	
	public void consumeForTraining(float fighterProgress) {
		removeFromStock(formationCost * fighterProgress);
	}
	
	public String toString () {
		return "\t-- VitalRessource -- " + super.toString() + "\tDisponibility :\t" + String.valueOf(isDisponible) + "\tForm. Cost :\t" + String.valueOf(formationCost) + "\n";
	}
}
