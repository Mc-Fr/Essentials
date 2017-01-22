package net.mcfr.tribe.resources;

import java.util.HashMap;

public class VitalResourceDummy extends VitalResource {
  private static HashMap<String, Float> map;

  static {
    map = new HashMap<>();
    map.put("stock", 20f);
    map.put("production", 0.2f);
    map.put("exchangeThreshold", 5f);
    map.put("formationCost", 1f);
    map.put("consumption", 0.1f);
    map.put("securityThreshold", 3f);
  }

  public VitalResourceDummy() {
    super(map, "VitalResourceTest");
  }

  public String getName() {
    return this.name;
  }

  public float getExchangeThreshold() {
    return this.exchangeThreshold;
  }

  public float getSaleableQuantity() {
    return this.saleableQuantity;
  }

  public float getFormationCost() {
    return this.formationCost;
  }

  public void setFormationCost(float formationCost) {
    this.formationCost = formationCost;
  }

  public void setProduction(float production) {
    this.production = production;
  }
}
