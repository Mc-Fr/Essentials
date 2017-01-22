package net.mcfr.tribe.resources.lists;

import net.mcfr.tribe.Population;
import net.mcfr.tribe.resources.ConfortResourceN1;
import net.mcfr.tribe.resources.ConfortResourceN2;

public class ConfortResourceN1List extends ResourceList<ConfortResourceN1> {
	
	public boolean isAvailableForFabrication () {
		boolean result = true;
		it = iterator();
		while (it.hasNext()) {
			if (!it.next().isDisponible()) {	result = false;	}
		}
		return result;
	}
	
	public void consumeForFabrication (Population population, ConfortResourceN2 r2, int i) {
		it = iterator();
		while (it.hasNext()) {
			it.next().consumeForFabrication(population, r2, i);
		}
	}
	
	public void calculateNeeds (Population population, ConfortResourceN2List confortResourcesN2) {
		it = iterator();
		while (it.hasNext()) {
			it.next().calculateNeed(confortResourcesN2, population);
		}
	}

	public void calculateValues(Population population, float priceFlexibility, ConfortResourceN2List confortResourcesN2) {
		it = iterator();
		while (it.hasNext()) {
			it.next().calculateValue(population, priceFlexibility, confortResourcesN2);
		}
	}

	public String confortCostsToString(ConfortResourceN2List confortResourcesN2) {
		String result = "\tConfort Ressources Cost Table :\n";
		
		for (int i = -1; i<size(); i++) {
			result += "\t\t\t";
			if (i != -1) {
				result += "N1" + (i+1);
			}
			for (int j = 0; j<confortResourcesN2.size(); j++) {
				result += "\t";
				if (i == -1) {
					result += "N2" + (j+1);
				} else {
					result += get(i).getCost(j);
				}
			}
			result += "\n";
		}
		
		return result;
	}
}
