package net.mcfr.tribe.resources;

import java.util.HashMap;

public class ConstructionResourceDummy extends ConstructionResource {
  private static HashMap<String, Float> map;

  static {
    map = new HashMap<>();
    map.put("stock", 20f);
    map.put("production", 2f);
    map.put("exchangeThreshold", 5f);
  }

  public ConstructionResourceDummy() {
    super(map, "ConstructionResourceTest");
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
}
