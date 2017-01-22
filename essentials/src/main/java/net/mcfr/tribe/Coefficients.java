package net.mcfr.tribe;

public enum Coefficients {
	VITAL_NEED_COEF(3),
	CONFORT_N1_NEED_COEF(3),
	CONFORT_N2_NEED_COEF(3),
	FIGHTERS_NEED_COEF(3),
	WEAPONS_NEED_COEF(3),
	REPUTATION_INFLUENCE_COEF(3);
	
	private final float value;
	
	Coefficients (float value) {
		this.value = value;
	}
	
	public float getValue () {
		return value;
	}
}