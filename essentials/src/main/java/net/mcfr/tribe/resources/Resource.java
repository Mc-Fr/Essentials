package net.mcfr.tribe.resources;
import java.util.Map;

import net.mcfr.tribe.Population;

public abstract class Resource {
	/** Quantité de ressource en réserve */
	protected float stock;
	/** Production par jour et par habitant non combattant */
	protected float production;
	/** Nombre de jours de réserve à garder avant de vendre la ressource */
	protected float exchangeThreshold;
	/** Évaluation du besoin de la ressource */
	protected float need;
	/** Évaluation de la valeur de la ressource */
	protected float value;
	/** Quantité de ressource vendable */
	protected float saleableQuantity;
	/** Nom de la ressource */
	protected String name;
	
	public Resource (Map<String, Float> data, String name) {
		this.stock = data.get("stock");
		this.production = data.get("production");
		this.exchangeThreshold = data.get("exchangeThreshold");
		
		this.name = name;
		
		this.need = 0;
		this.value = 0;
		this.saleableQuantity = 0;
	}
	
	public void productResource (Population population) {
		addToStock(population.getWorkers() * production);
	}
	
	public void addToStock (float add) {
		this.stock += add;
		if (stock < 0) {	stock = 0;	}
	}
	
	public void removeFromStock (float remove) {
		this.addToStock(-remove);
	}
	
	public float getStock() {
		return stock;
	}

	public float getProduction() {
		return production;
	}

	public float getNeed() {
		return need;
	}
	
	public float getValue() {
		return value;
	}

	public void calculateValue(float priceFlexibility) {
		value = priceFlexibility * need;
	}
	
	public String toString() {
		return name + "\n\t\tStock :\t\t" + String.format("%.1f", stock) + "\tProduction :\t" + production + "\tEx. Threshold :\t" + exchangeThreshold;
	}

}
