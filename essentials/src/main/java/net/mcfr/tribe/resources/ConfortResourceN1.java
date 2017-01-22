package net.mcfr.tribe.resources;
import static net.mcfr.tribe.Coefficients.*;

import java.util.Map;

import net.mcfr.tribe.Population;
import net.mcfr.tribe.resources.lists.ConfortResourceN2List;

public class ConfortResourceN1 extends ConsumableResource {
	private boolean isDisponible;
	private float[] costTable;
	
	public ConfortResourceN1 (Map<String, Float> data, String name) {
		super (data, name);
		
		isDisponible = false;
	}
	
	public void setCostTable (float[] costTable) {
		this.costTable = costTable;
	}
	
	public void consumeForFabrication(Population population, ConfortResourceN2 r, int i) {
		removeFromStock( r.getProduction() * population.getWorkers() * costTable[i]);
	}
	
	public void calculateValue (Population population, float priceFlexibility, ConfortResourceN2List confortResourcesN2) {
		super.calculateValue(priceFlexibility);
		saleableQuantity = Math.max(0f, stock - consumption * population.getInhabitants() * exchangeThreshold - confortResourcesN2.getFabricationQuantity(population, costTable));
	}
	
	public void calculateNeed (ConfortResourceN2List confortResourcesN2, Population population) {
		float[] costsNeeds = confortResourcesN2.getCostsNeeds(costTable, this, population);
		need = securityThreshold * CONFORT_N1_NEED_COEF.getValue() - stock / (1 + consumption * population.getInhabitants()) + costsNeeds[0];
		isDisponible = (stock >= consumption * (1 + securityThreshold) * population.getInhabitants());
	}

	public boolean isDisponible () {
		return isDisponible;
	}
	
	public float getCost (int index) {
		return costTable[index];
	}
	
	public String toString () {
		return "\t-- ConfortRessourceN1 -- " + super.toString() + "\tDisponibility :\t" + String.valueOf(isDisponible) + "\n";
	}
}
