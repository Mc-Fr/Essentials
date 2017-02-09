package net.mcfr.roleplay;

public enum Attributes {
	FORCE("Force"),
	DEXTERITE("Dextérité"),
	INTELLECT("Intellect"),
	ENDURANCE("Endurance");
  
  private String name;
  
  private Attributes(String name) {
    this.name = name;
  }
	
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

  public String getName() {
    return this.name;
  }
}