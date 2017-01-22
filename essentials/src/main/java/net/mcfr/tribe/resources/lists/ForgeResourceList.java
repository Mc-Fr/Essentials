package net.mcfr.tribe.resources.lists;

import net.mcfr.tribe.Population;
import net.mcfr.tribe.resources.ForgeResource;
import net.mcfr.tribe.resources.Weapon;

public class ForgeResourceList extends ResourceList<ForgeResource>{
	
	public boolean isAvailableForForge () {
		boolean result = true;
		it = iterator();
		while (it.hasNext()) {
			if (!it.next().isDisponible()) {	result = false;	}
		}
		return result;
	}
	
	public void consumeForForge (Population population, Weapon weapon) {
		it = iterator();
		while (it.hasNext()) {
			it.next().consume(population, weapon);
		}
	}
	
	public void calculateNeeds (Population population, Weapon weapon) {
		it = iterator();
		while (it.hasNext()) {
			it.next().calculateNeed(population, weapon);
		}
	}
	
	public void calculateDisponibilities (Population population, Weapon weapon) {
		it = iterator();
		while (it.hasNext()) {
			it.next().calculateDisponibility(population, weapon);
		}
	}
}
