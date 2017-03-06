package net.mcfr.roleplay;

public enum Attributes {
  FORCE("Force"),
  DEXTERITE("Dextérité"),
  INTELLECT("Intellect"),
  ENDURANCE("Endurance"),
  PERCEPTION("Perception");

  private String name;

  private Attributes(String name) {
    this.name = name;
  }

  public static Attributes getAttributeFromString(String name) {
    for (Attributes att : values()) {
      if (att.getName().toLowerCase().substring(0, 3).equals(name))
        return att;
    }
    return DEXTERITE;
  }

  public String getName() {
    return this.name;
  }
}