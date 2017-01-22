package net.mcfr.tribe.resources.lists;

import net.mcfr.tribe.Population;
import net.mcfr.tribe.resources.VitalResource;

public class VitalResourceList extends ResourceList<VitalResource> {
	
	public boolean isAvailableForTraining (float fightersFormationPerDay) {
		boolean result = true;
		it = iterator();
		while (it.hasNext()) {
			if (!it.next().isAvailableForTraining(fightersFormationPerDay)) {	result = false;	}
		}
		return result;
	}
	
	public void consumeForTraining (float fighterProgress) {
		it = iterator();
		while (it.hasNext()) {
			it.next().consumeForTraining(fighterProgress);
		}
	}
	
	public void productResources (Population population) {
		VitalResource r;
		
		population.setBirthPossible(true);
		it = iterator();
		while (it.hasNext()) {
			r = it.next();
			r.productResource(population);
			r.calculateDisponibility(population);
			if (!r.isDisponible()) {
				population.setBirthPossible(false);
			}
		}
	}
	
	public void calculateNeeds (Population population, float fightersNeed) {
		it = iterator();
		while (it.hasNext()) {
			it.next().calculateNeed(population, fightersNeed);
		}
	}
	
	public void calculateDisponibilities (Population population) {
		it = iterator();
		while (it.hasNext()) {
			it.next().calculateDisponibility(population);
		}
	}
}
