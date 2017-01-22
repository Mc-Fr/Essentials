package net.mcfr.tribe.resources;
import static net.mcfr.tribe.Coefficients.*;

import java.util.Map;

import net.mcfr.tribe.Population;

public class ConfortResourceN2 extends ConsumableResource {
	
	public ConfortResourceN2 (Map<String, Float> data, String name) {
		super (data, name);
	}
	
	public void calculateValue (Population population, float priceFlexibility) {
		super.calculateValue(priceFlexibility);
		saleableQuantity = stock - consumption * population.getInhabitants() * exchangeThreshold;
	}
	
	public void calculateNeed (Population population) {
		need = securityThreshold * CONFORT_N2_NEED_COEF.getValue() - stock / (1 + consumption * population.getInhabitants());
	}
	
	public float[] getCostsNeed (float cost, ConfortResourceN1 r1, Population population) {
		float[] result = {0, 0};
		
		result[0] = securityThreshold * consumption * population.getInhabitants() * cost / (1 + r1.getProduction() * population.getWorkers());
		result[1] = cost * consumption * (1 + securityThreshold) * population.getInhabitants();
		
		return result;
	}

	public float getFabricationQuantity(Population population, float cost) {
		return consumption * population.getInhabitants() * cost * exchangeThreshold;
	}
	
	public String toString () {
		return "\t-- ConfortRessourceN2 -- " + super.toString() + "\n";
	}
}
