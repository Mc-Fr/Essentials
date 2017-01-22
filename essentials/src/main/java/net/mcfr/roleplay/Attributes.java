package net.mcfr.roleplay;

public enum Attributes {
	FORCE,
	DEXTERITE,
	INTELLECT,
	ENDURANCE;
	
	public static Attributes getAttributeFromString(String name) {
		switch (name) {
		case "for":
			return FORCE;
		case "dex":
			return DEXTERITE;
		case "int":
			return INTELLECT;
		case "end":
			return ENDURANCE;
		default :
			return DEXTERITE;
		}
	}
}