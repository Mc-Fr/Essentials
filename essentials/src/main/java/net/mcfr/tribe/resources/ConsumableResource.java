package net.mcfr.tribe.resources;
import java.util.Map;

import net.mcfr.tribe.Population;

public abstract class ConsumableResource extends Resource {
	/** Consommation par jour et par habitant */
	protected float consumption;
	/** Nombre de jours de réserve à conserver */
	protected float securityThreshold;
	
	public ConsumableResource (Map<String, Float> data, String name) {
		super (data, name);
		
		consumption = data.get("consumption");
		securityThreshold = data.get("securityThreshold");
	}

	public void consume(Population population) {
		removeFromStock( consumption * population.getInhabitants());
	}
	
	public String toString () {
		return super.toString() + "\n\t\tConsumption :\t" + consumption + "\tSec. Thres. :\t" + securityThreshold;
	}
}
