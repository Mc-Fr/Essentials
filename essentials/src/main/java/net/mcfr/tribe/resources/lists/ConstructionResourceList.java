package net.mcfr.tribe.resources.lists;

import net.mcfr.tribe.Population;
import net.mcfr.tribe.resources.ConstructionResource;

public class ConstructionResourceList extends ResourceList<ConstructionResource> {
	
	public void setConstructionCost (float[] constructionCost) {
		for (int i = 0; i < constructionCost.length; i++) {
			get(i).setConstructionCost(constructionCost[i]);
		}
	}
	
	public void calculateNeeds (Population population) {
		it = iterator();
		while (it.hasNext()) {
			it.next().calculateNeed(population);
		}
	}

	public String constructionCostsToString() {
		String result = "\tConstruction Cost :\n";
		for (int i = 0; i < size(); i++) {
			result += "\t\t\t" + (i+1) + "\t" + get(i).getConstructionProjectCost() + "\n";
		}
		return result;
	}
}
