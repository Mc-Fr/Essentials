package net.mcfr.tribe.resources;
import static net.mcfr.tribe.Coefficients.*;

import java.util.Map;

import net.mcfr.tribe.Population;
import net.mcfr.tribe.resources.lists.ForgeResourceList;

public class Weapon extends Resource {
	protected float breakingCoefficient;
	
	public Weapon (Map<String, Float> data, String name) {
		super (data, name);
		
		breakingCoefficient = data.get("breakingCoefficient");
	}
	
	public void productResource (Population population, ForgeResourceList forgeResources) {
		forgeResources.calculateDisponibilities(population, this);
		if (forgeResources.isAvailableForForge()) {
			stock += production * population.getWorkers();
			forgeResources.consumeForForge(population, this);
		}
	}
	
	public void breakWeapons (Population population) {
		this.removeFromStock( breakingCoefficient * population.getFighters());
	}
	
	public void calculateValue (Population population, float priceFlexibility, float threat, float weaponEfficiency) {
		super.calculateValue(priceFlexibility);
		saleableQuantity = stock - exchangeThreshold - threat / weaponEfficiency;
	}
	
	public void calculateWeaponNeed (float threat) {
		need = threat + WEAPONS_NEED_COEF.getValue() - stock;
	}
	
	public String toString () {
		return "\t-- Weapon -- " + super.toString() + "\n\t\tDestruct.Coef :\t" + breakingCoefficient + "\n";
	}
}
