package net.mcfr.tribe.resources;
import java.util.Map;

import net.mcfr.tribe.Population;

public class ConstructionResource extends Resource {
	private float constructionProjectCost;
	
	public ConstructionResource (Map<String, Float> data, String name) {
		super (data, name);
		constructionProjectCost = 0;
	}
	
	public void calculateValue (float priceFlexibility) {
		super.calculateValue(priceFlexibility);
		saleableQuantity = Math.max(0, stock - exchangeThreshold * constructionProjectCost);
	}
	
	public void calculateNeed (Population population) {
		need = (constructionProjectCost - stock) / (1 + population.getWorkers() * production);
	}

	public float getConstructionProjectCost () {
		return constructionProjectCost;
	}

	public void setConstructionCost (float constructionProjectCost) {
		this.constructionProjectCost = constructionProjectCost;
	}
	
	public String toString () {
		return "\t-- ConstructionRessource -- " + super.toString() + "\n";
	}
}
