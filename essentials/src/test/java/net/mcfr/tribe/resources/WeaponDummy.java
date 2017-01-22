package net.mcfr.tribe.resources;

import java.util.HashMap;

public class WeaponDummy extends Weapon {
  private static HashMap<String, Float> map;

  static {
    map = new HashMap<>();
    map.put("stock", 20f);
    map.put("production", 0.2f);
    map.put("exchangeThreshold", 5f);
    map.put("breakingCoefficient", 0.1f);
  }

  public WeaponDummy() {
    super(map, "WeaponTest");
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

  public float getBreakingCoefficient() {
    return this.breakingCoefficient;
  }
}
