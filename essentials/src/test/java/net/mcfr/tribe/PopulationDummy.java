package net.mcfr.tribe;

import java.util.HashMap;

public class PopulationDummy extends Population {
  private static HashMap<String, Float> map;
  
  static {
    map = new HashMap<>();
    map.put("inhabitants", 10f);
    map.put("fighters", 2f);
    map.put("natality", 0.05f);
    map.put("mortality", 0.15f);
    map.put("starvationInfluence", 1f);
  }
  
  public PopulationDummy() {
    super(map);
  }
  
  public Object isBirthPossible() {
    return this.isBirthPossible;
  }
  
  public float getNatality() {
    return this.natality;
  }
  
  public float getMortality() {
    return this.mortality;
  }
  
  public float getStarvationInfluence() {
    return this.starvationInfluence;
  }
  
  public float getVitalResourcesLacks() {
    return this.vitalResourcesLacks;
  }
  
  public float getDeathProgress() {
    return this.deathProgress;
  }
  
  public float getBirthProgress() {
    return this.birthProgress;
  }
  
  public float getFighterProgress() {
    return this.fighterProgress;
  }
  
  public void setInhabitants(int inhabitants) {
    this.inhabitants = inhabitants;
  }
}
