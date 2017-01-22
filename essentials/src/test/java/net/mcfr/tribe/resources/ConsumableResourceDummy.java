package net.mcfr.tribe.resources;

import java.util.HashMap;

public class ConsumableResourceDummy extends ConsumableResource {
  private static HashMap<String, Float> map;

  static {
    map = new HashMap<>();
    map.put("stock", 20f);
    map.put("production", 2f);
    map.put("exchangeThreshold", 5f);
    map.put("consumption", 1f);
    map.put("securityThreshold", 3f);
  }

  public ConsumableResourceDummy() {
    super(map, "ConsumableResourceTest");
  }

  public String getName() {
    return this.name;
  }

  public float getConsumption() {
    return this.consumption;
  }

  public float getExchangeThreshold() {
    return this.exchangeThreshold;
  }

  public void setValue(float value) {
    this.value = value;
  }
}
