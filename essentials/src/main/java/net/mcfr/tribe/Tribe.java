package net.mcfr.tribe;
import static net.mcfr.tribe.Coefficients.*;

import java.util.LinkedList;
import java.util.Map;

import net.mcfr.tribe.resources.*;
import net.mcfr.tribe.resources.lists.*;

public class Tribe {
	/** Numéro d'identification de la tribu */
	protected final int id;
	/** Nom de la tribu */
	protected final String name;
	
	// Ressources et paramètres de ressource
	/** Liste des ressources vitales */
	protected VitalResourceList vitalResources;
	/** Liste des ressources de forge */
	protected ForgeResourceList forgeResources;
	/** Liste des ressources de construction */
	protected ConstructionResourceList constructionResources;
	/** Liste des ressources de confort de niveau 1 */
	protected ConfortResourceN1List confortResourcesN1;
	/** Liste des ressources de confort de niveau 2 */
	protected ConfortResourceN2List confortResourcesN2;
	/** Arme de la tribu */
	protected Weapon weapon;
	
	// Jauges, amenées à varier durant la progression
	/** Nombre de combattants requis par la tribu */
	protected float fightersNeed;
	/** Niveau de menace de toutes les autres tribus */
	protected float threat;
	
	/** Population de la tribu */
	protected Population population;
	
	// Paramètres, resteront constants sans intervention extérieure
	// Combat
	/** Efficacité des armes utilisées par la tribu */
	protected float weaponEfficiency;
	/** Efficacité des combattants formés par la tribu */
	protected float fightersEfficiency;
	/** Proportion de combattants formable par jour par la tribu */
	protected float fightersFormationPerDay;
	
	// Commerce
	/** Réputation minimum d'un joueur pour qu'il puisse commercer avec la tribu */
	protected float exchangeMinimumReputation;
	/** Coefficient de flexibilité des prix de la tribu */
	protected float priceFlexibility;
	/** Cupidité de la tribu, déséquilibre forcé des valeurs des échanges */
	protected float cupidity;
	
	// Événements
	/** Tableau croisé de la forme : [Ressource][Tribu] = Quantité d'une ressource échangeable à un tribu */
	protected float[][] exchanges;
	/** Tableau de deux lignes. Ligne 1 : Attaques, Ligne 2 : Défenses, Colonnes : Tribus */
	protected boolean[][] wars;
	
	public Tribe(int identification, String n, Map<String, Float> data, float[][] confortCostTableData, LinkedList<Resource> resources) {
		this(identification, n, data, confortCostTableData, resources, false);
		population = new Population(data);
	}
	
	public Tribe(int identification, String n, Map<String, Float> data, float[][] confortCostTableData, LinkedList<Resource> resources, boolean isForTest) {
		id = identification;
		name = n;
		
		// Paramètres uniques
		weaponEfficiency = data.get("weaponEfficiency");
		fightersEfficiency = data.get("fightersEfficiency");
		fightersFormationPerDay = data.get("fightersFormationPerDay");
		exchangeMinimumReputation = data.get("exchangeMinimumReputation");
		priceFlexibility = data.get("priceFlexibility");
		cupidity = data.get("cupidity");
		
		vitalResources = new VitalResourceList();
		forgeResources = new ForgeResourceList();
		constructionResources = new ConstructionResourceList();
		confortResourcesN1 = new ConfortResourceN1List();
		confortResourcesN2 = new ConfortResourceN2List();
		
		// Ressources
		int i = 0;
		for (Resource r : resources) {
	    	if (r instanceof VitalResource) {
	    		vitalResources.add((VitalResource)r);
	    	} else if (r instanceof ForgeResource) {
	    		forgeResources.add((ForgeResource)r);
	    	} else if (r instanceof ConstructionResource) {
	    		constructionResources.add((ConstructionResource)r);
	    	} else if (r instanceof ConfortResourceN1) {
	    		confortResourcesN1.add((ConfortResourceN1)r);
	    		((ConfortResourceN1)r).setCostTable(confortCostTableData[i]);
	    		i++;
	    	} else if (r instanceof ConfortResourceN2) {
	    		confortResourcesN2.add((ConfortResourceN2)r);
	    	} else if (r instanceof Weapon) {
	    		weapon = (Weapon)r;
	    	}
	    }
		
		// Initialisation
		fightersNeed = 0;
		threat = 0;
		exchanges = new float[resources.size()][data.get("tribes").intValue()];
		wars = new boolean[2][data.get("tribes").intValue()];
	}
	
	/** Calcule le niveau de menace du point de vue de la tribu actuelle
	 * @param	tribes	Liste de toutes les tribus */
	public void calculateThreat (LinkedList<Tribe> tribes) {
		threat = 0;
        for (Tribe t : tribes) {
        	if (!this.equals(t) && (wars[t.id][0] || wars[t.id][1])) {
        		threat += t.getMilitaryPower();
        	}
        }
	}
	
	/** Retourne la puissance militaire d'une tribu */
	public float getMilitaryPower () {
		return Math.min(weapon.getStock() * weaponEfficiency, population.getFighters() * fightersEfficiency);
	}
	
	/** Calcule le besoin de la tribu en combattants */
	public void calculateFightersNeed () {
		fightersNeed = threat + FIGHTERS_NEED_COEF.getValue() - population.getFighters();
	}
	
	/** Introduit les coûts du projet de construction dans toutes les ressources de construction
	 * @param	constructionCost	Tableau des coûts en ressources de construction du projet actuel */
	public void setConstructionCost(float[] constructionCost) {
		constructionResources.setConstructionCost(constructionCost);
	}
	
	public void setWar (int index, boolean attack, boolean war) {
		int line = 1;
		if (attack) { line = 0; }
		
		wars[line][index] = war;
	}
	
	/** Processus de calcul des consommations, productions, besoins de toutes les ressources de la tribu, à effectuer deux fois par jour
	 * 
	 * @param tribes	Liste de toutes les tribus du monde
	 * @param isEvening	Vrai si il s'agit du calcul du soir (19h)
	 */
	public void nextTurn (LinkedList<Tribe> tribes, boolean isEvening) {		
		population.setVitalResourcesLacks(0);
		
		vitalResources.productResources(population);
		forgeResources.productResources(population);
		constructionResources.productResources(population);
		confortResourcesN1.productResources(population);
        
        weapon.breakWeapons(population);
        
        calculateThreat(tribes);
        
        calculateFightersNeed();
        
        weapon.calculateWeaponNeed(threat);
        
        constructionResources.calculateNeeds(population);
        
        confortResourcesN1.calculateNeeds(population, confortResourcesN2);
        
        vitalResources.consume(population);
        
        population.trainNewFighters(vitalResources, fightersFormationPerDay, fightersNeed);
        
        calculateFightersNeed(); // Actualisation
        
        vitalResources.calculateNeeds(population, fightersNeed);
        
        weapon.productResource(population, forgeResources);
        
        weapon.calculateWeaponNeed(threat); // Actualisation
        
        forgeResources.calculateNeeds(population, weapon);
        
        confortResourcesN2.productResources(population, confortResourcesN1);
        
        confortResourcesN1.consume(population);
        
        confortResourcesN2.consume(population);
        
        confortResourcesN1.calculateNeeds(population, confortResourcesN2); // Actualisation
        
        confortResourcesN2.calculateNeeds(population);
        
        if (isEvening) {
        	population.calculateBirths();
        	population.calculateDeaths();
        }
        
        vitalResources.calculateValues(population, priceFlexibility);
        forgeResources.calculateValues(population, priceFlexibility);
        constructionResources.calculateValues(population, priceFlexibility);
        confortResourcesN2.calculateValues(population, priceFlexibility);
        weapon.calculateValue(population, priceFlexibility, threat, weaponEfficiency);
        confortResourcesN1.calculateValues(population, priceFlexibility, confortResourcesN2);
	}

	public void trocIntertribal(Tribe t2) {
		// exchanges
		
		// Tableau croisé de la forme : [Ressource][Tribu] = Quantité d'une ressource échangeable à un tribu
	}
	
	public String toString() {
		String result = "------------------- ID : " + id + " ----- NAME : " + name + " -------------------\n";
		
		result += "---- TRIBE VARIABLES ----\n";
		
		result +="\t-- Demography --\n" + population;
		
		result += "\t-- Military --\n\t\tThreat :\t" + String.format("%.2f", threat) + "\tFighters Need :\t" + String.format("%.2f", fightersNeed) + "\n\t\t";
		result += "Weap. Effic. :\t" + String.format("%.2f", weaponEfficiency) + "\tFight. Effic. :\t" + String.format("%.2f", fightersEfficiency);
		result += "\tForm.Per.Day :\t" + String.format("%.2f", fightersFormationPerDay) + "\n";
		
		result += "\t-- Exchanges --\n\t\tMin. Reput. :\t" + exchangeMinimumReputation + "\tFlexibility :\t" + priceFlexibility;
		result += "\tCupidity :\t" + cupidity + "\n";
		
		
		result += "---- WORLD VARIABLES ----\n";
		
		result += "\tExchanges :\t";
		
		for (int i = -1; i < exchanges.length; i++) {
			if (i != -1) {
				result += "\t\t\t" + (i+1);
			}
			for (int j = 0; j < exchanges[0].length; j++) {
				if (i == -1) {
					result += "\t T" + (j+1);
				} else {
					result += "\t" + exchanges[i][j];
				}
			}
			result += "\n";
		}
		
		result += "\tWars :\t\t";
		
		for (int i = -1; i < 2; i++) {
			if (i == 0) {
				result += "\t\t\tAtt.";
			} else if (i == 1) {
				result += "\t\t\tDef.";
			}
			for (int j = 0; j < wars[0].length ; j++) {
				if (i == -1) {
					result += "\t T" + (j+1);
				} else { 
					result += "\t" + wars[i][j];
				}
			}
			result += "\n";
		}
		
		result += constructionResources.constructionCostsToString();
		
		result += "---- RESSOURCES VARIABLES ----\n";
		
		result += confortResourcesN1.confortCostsToString(confortResourcesN2);
		
		result += "" + vitalResources + forgeResources + constructionResources + confortResourcesN1 + confortResourcesN2 + weapon;
		
		return result;
	}
}