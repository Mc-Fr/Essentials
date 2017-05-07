package net.mcfr.roleplay;

public enum Attribute {
  FORCE("force"),
  DEXTERITE("dextérité"),
  INTELLECT("intellect"),
  ENDURANCE("endurance"),
  PERCEPTION("perception");

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