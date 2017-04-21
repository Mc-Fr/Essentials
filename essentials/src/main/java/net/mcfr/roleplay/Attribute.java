package net.mcfr.roleplay;

public enum Attribute {
  FORCE("Force"),
  DEXTERITE("Dextérité"),
  INTELLECT("Intellect"),
  ENDURANCE("Endurance"),
  PERCEPTION("Perception");

  private String name;

  private Attribute(String name) {
    this.name = name;
  }

  public static Attribute getAttributeFromString(String name) {
    for (Attribute att : values()) {
      if (att.getName().toLowerCase().substring(0, 3).equals(name))
        return att;
    }
    return DEXTERITE;
  }

  public String getName() {
    return this.name;
  }
}