package net.mcfr.harvest;

import org.spongepowered.api.item.inventory.ItemStack;

public class RareItemEntry {
  private double probability;
  private ItemStack item;
  
  public RareItemEntry(double p, ItemStack item) {
    this.probability = p;
    this.item = item;
  }
  
  public double getProbability() {
    return this.probability;
  }
  
  public ItemStack getItemStack() {
    return this.item;
  }
}
