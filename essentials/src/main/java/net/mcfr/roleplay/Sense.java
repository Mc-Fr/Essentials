package net.mcfr.roleplay;

public enum Sense {
  VISION("vision"),
  OUIE("ouie"),
  GOUT("gout"),
  ODORAT("odorat"),
  TOUCHER("toucher");
  
  private String name;
  
  private Sense (String name) {
    this.name = name;
  }
  
  public String getName() {
    return this.name;
  }
}