package net.mcfr.roleplay;

public enum Defense {
  PARADE("parade"),
  ESQUIVE("esquive"),
  BLOCAGE("blocage");
  
  private String name;
  
  private Defense (String name) {
    this.name = name;
  }
  
  public String getName() {
    return this.name;
  }
}