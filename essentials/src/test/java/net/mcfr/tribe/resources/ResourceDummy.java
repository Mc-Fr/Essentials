package net.mcfr.tribe.resources;

import java.util.HashMap;

public class ResourceDummy extends Resource {
  private static HashMap<String, Float> map;

  static {
    map = new HashMap<>();
    map.put("stock", 20f);
    map.put("production", 0.2f);
    map.put("exchangeThreshold", 5f);
  }

  public ResourceDummy() {
    super(map, "ResourceTest");
  }

  public ResourceDummy(String name) {
    super(map, name);
  }

  public float getSaleableQuantity() {
    return this.saleableQuantity;
  }

  public String getName() {
    return this.name;
  }

  public float getExchangeThreshold() {
    return this.exchangeThreshold;
  }

  public void setNeed(float need) {
    this.need = need;
  }
}
