package net.mcfr.tribe.resources.lists;

import net.mcfr.tribe.Population;
import net.mcfr.tribe.resources.ConfortResourceN1;
import net.mcfr.tribe.resources.ConfortResourceN2;

public class ConfortResourceN2List extends ResourceList<ConfortResourceN2> {
	public void productResources (Population population, ConfortResourceN1List confortResourcesN1) {
		if (confortResourcesN1.isAvailableForFabrication()) {
			ConfortResourceN2 r;
			int i = 0;
			it = iterator();
			while (it.hasNext()) {
				r = it.next();
				r.productResource(population);
				confortResourcesN1.consumeForFabrication(population, r, i++);
			}
		}
	}
	
	public void calculateNeeds (Population population) {
		it = iterator();
		while (it.hasNext()) {
			it.next().calculateNeed(population);
		}
	}
	
	public float[] getCostsNeeds (float[] costTable, ConfortResourceN1 r1, Population population) {
		int i = 0;
		float[] result = {0, 0};
		float[] add;
		it = iterator();
		while (it.hasNext()) {
			add = it.next().getCostsNeed(costTable[i++], r1, population);
			result[0] += add[0];
			result[1] += add[1];
		}
		return result;
	}
	
	public float getFabricationQuantity (Population population, float[] costs) {
		float result = 0;
		int i = 0;
		it = iterator();
		while (it.hasNext()) {
			result += it.next().getFabricationQuantity(population, costs[i++]);
		}
		return result;
	}
}
