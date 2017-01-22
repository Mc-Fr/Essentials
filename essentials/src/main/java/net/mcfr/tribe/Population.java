package net.mcfr.tribe;
import java.util.Map;

import net.mcfr.tribe.resources.lists.VitalResourceList;

public class Population {
	/** Total des membres de la population */
	protected int inhabitants;
	/** Nombre d'habitants dédiés au combat */
	private int fighters;
	/** Nombre de ressources vitales manquantes */
	protected int vitalResourcesLacks;
	
	/** Progrès du prochain combattant en formation */
	protected float fighterProgress;
	/** Coefficient de natalité de la population */
	protected float natality;
	/** Progrès actuel du prochain nouveau né */
	protected float birthProgress;
	/** Coefficient de mortalité de la population */
	protected float mortality;
	/** Progrès actuel du prochain décès */
	protected float deathProgress;
	/** Influence de la famine sur le taux de mortalité */
	protected float starvationInfluence;
	
	/** Possibilité de faire progresser la natalité */
	protected boolean isBirthPossible;
	
	public Population (Map<String, Float> data) {
		this.inhabitants = Math.round(data.get("inhabitants"));
		this.fighters = Math.round(data.get("fighters"));
		this.natality = data.get("natality");
		this.mortality = data.get("mortality");
		this.starvationInfluence = data.get("starvationInfluence");
		
		fighterProgress = 0;
		birthProgress = 0;
		deathProgress = 0;
		vitalResourcesLacks = 0;
		isBirthPossible = false;
	}
	
	/** Calcule la progression du prochain décès de la population */
	public void calculateDeaths () {
        deathProgress += mortality * inhabitants * (1 + starvationInfluence * vitalResourcesLacks);
        inhabitants -= Math.floor(deathProgress);
        deathProgress -= Math.floor(deathProgress);
        
        if (inhabitants < fighters) {	fighters = inhabitants;	}
	}
	
	/** Calcule la progression du prochain nouveau né de la population */
	public void calculateBirths () {
		if (isBirthPossible && inhabitants >=2) {
			birthProgress += natality * inhabitants;
			inhabitants += Math.floor(birthProgress);
			birthProgress -= Math.floor(birthProgress);
		}
	}
	
	/** Effectue l'entrainement des combattants
	 * 
	 * @param trainingResources			Liste des ressources utilisées pour l'entrainement
	 * @param fightersFormationPerDay	Progrès journalier des combattants
	 * @param fightersNeed				Besoin de la tribu en combattants
	 */
	public void trainNewFighters (VitalResourceList trainingResources, float fightersFormationPerDay, float fightersNeed) {
		if (trainingResources.isAvailableForTraining(fightersFormationPerDay)) {
			float temp = fighterProgress;
			fighterProgress += fightersFormationPerDay;
			fighterProgress = Math.min(temp + fightersNeed, fighterProgress);
			trainingResources.consumeForTraining(fighterProgress - temp);
		}
		fighters += Math.floor(fighterProgress);
		fighterProgress -= Math.floor(fighterProgress);
	}

	public int getInhabitants() {
		return inhabitants;
	}

	public int getFighters() {
		return fighters;
	}
	
	public int getWorkers () {
		return inhabitants - fighters;
	}

	public void setVitalResourcesLacks(int vitalResourcesLacks) {
		this.vitalResourcesLacks = vitalResourcesLacks;
	}
	
	public void increaseVitalResourcesLacks() {
		vitalResourcesLacks++;
	}

	public void setBirthPossible(boolean isBirthPossible) {
		this.isBirthPossible = isBirthPossible;
	}
	
	public String toString () {
		String result = "\t\tInhabitants :\t" + inhabitants + "\tFighters :\t" + fighters;
		result += "\tVitals Lacks :\t" + vitalResourcesLacks + "\tBirth Poss. :\t" + String.valueOf(isBirthPossible) + "\n\t\t";
		result += "Natality :\t" + natality + "\tMortality :\t" + mortality + "\tStarv.Inf. :\t\t" + starvationInfluence + "\n";
		
		return result;
	}
}
