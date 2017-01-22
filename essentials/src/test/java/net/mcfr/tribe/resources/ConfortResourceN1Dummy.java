package net.mcfr.tribe.resources;

import java.util.HashMap;

public class ConfortResourceN1Dummy extends ConfortResourceN1 {
  private static HashMap<String, Float> map;

  static {
    map = new HashMap<>();
    map.put("consumption", 1f);
    map.put("securityThreshold", 3f);
    map.put("stock", 20f);
    map.put("production", 2f);
    map.put("exchangeThreshold", 5f);
  }

  public ConfortResourceN1Dummy() {
    super(map, "ConfortResourceN1Test");
    setCostTable(new float[] { 1, 2 });
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
