package net.mcfr.tribe.resources;

import java.util.HashMap;

public class ForgeResourceDummy extends ForgeResource {
  private static HashMap<String, Float> map;

  static {
    map = new HashMap<>();
    map.put("stock", 20f);
    map.put("production", 0.2f);
    map.put("exchangeThreshold", 5f);
    map.put("forgeCost", 1f);
  }

  public ForgeResourceDummy() {
    super(map, "ForgeResourceTest");
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

  public float getForgeCost() {
    return this.forgeCost;
  }
}
