package net.mcfr.harvest;

import org.spongepowered.api.item.inventory.ItemStack;

public class RareItemEntry {
  private float probability;
  private ItemStack item;
  
  public RareItemEntry(float p, ItemStack item) {
    this.probability = p;
    this.item = item;
  }
  
  public float getProbability() {
    return this.probability;
  }
  
  public ItemStack getItemStack() {
    return this.item;
  }
}
