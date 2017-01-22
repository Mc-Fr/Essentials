package net.mcfr.tribe.resources;

import java.util.HashMap;

public class ConfortResourceN2Dummy extends ConfortResourceN2 {
  private static HashMap<String, Float> map;

  static {
    map = new HashMap<>();
    map.put("consumption", 0.2f);
    map.put("securityThreshold", 3f);
    map.put("stock", 20f);
    map.put("production", 0.5f);
    map.put("exchangeThreshold", 5f);
  }

  public ConfortResourceN2Dummy() {
    super(map, "ConfortResourceN2Test");
  }

  public float getConsumption() {
    return this.consumption;
  }

  public float getSecurityThreshold() {
    return this.securityThreshold;
  }

  public float getExchangeThreshold() {
    return this.exchangeThreshold;
  }

  public String getName() {
    return this.name;
  }

  public float getSaleableQuantity() {
    return this.saleableQuantity;
  }
}
