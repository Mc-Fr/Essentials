package net.mcfr.harvest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import net.mcfr.roleplay.Skill;

public class HarvestArea {
  private final static float RADIUS = 10f;

  private String name;
  private String displayName;
  private Location<World> location;
  private Skill skill;
  private HarvestTools tool;
  private int toolDamage;
  private List<ItemStack> itemList;
  private List<RareItemEntry> rareItemList;

  public HarvestArea(String name, String displayName, Location<World> loc, Skill skill, HarvestTools tool, int toolDamage) {
    this.name = name;
    this.displayName = displayName;
    this.location = loc;
    this.skill = skill;
    this.itemList = new ArrayList<>();
    this.rareItemList = new ArrayList<>();
    this.tool = tool;
    this.toolDamage = toolDamage;
  }

  public String getName() {
    return this.name;
  }
  
  public String getDisplayName() {
    return this.displayName;
  }

  public Skill getSkill() {
    return this.skill;
  }
  
  public HarvestTools getTool() {
    return this.tool;
  }
  
  public int getToolDamage() {
    return this.toolDamage;
  }

  public Location<World> getLocation() {
    return this.location;
  }

  public boolean isToolCorrect(ItemType tool) {
    return this.tool.isToolCorrect(tool);
  }
  
  public ItemStack useTool(ItemStack tool) {
    tool.offer(Keys.ITEM_DURABILITY, tool.get(Keys.ITEM_DURABILITY).get() - this.toolDamage);
    
    if (tool.get(Keys.ITEM_DURABILITY).get() <= 0)
      return null;
    
    return tool;
  }

  public void clearItemList() {
    this.itemList.clear();
  }

  public void addItem(ItemStack item) {
    this.itemList.add(item.copy());
  }

  /**
   * Removes from itemList any ItemStack that contains the same ItemType and
   * UnsafeDamage value as the one passed as parameter.
   * 
   * @param item
   * @return The number of removed stacks
   */
  public int removeItem(ItemStack item) {
    List<ItemStack> toRemove = new ArrayList<>();
    int metaData = (int) item.toContainer().get(DataQuery.of("UnsafeDamage")).orElse(0);

    for (ItemStack i : this.itemList) {
      if (i.getItem().equals(item.getItem()))
        if ((int) i.toContainer().get(DataQuery.of("UnsafeDamage")).orElse(0) == metaData)
          toRemove.add(i);
    }

    int removed = 0;
    for (ItemStack i : toRemove) {
      this.itemList.remove(i);
      removed++;
    }
    return removed;
  }

  public void clearRareItemList() {
    this.rareItemList.clear();
  }

  public void addRareItem(RareItemEntry entry) {
    this.rareItemList.add(entry);
  }

  /**
   * Removes from rareItemList any ItemStack that contains the same ItemType and
   * UnsafeDamage value as the one passed as parameter.
   * 
   * @param item
   * @return The number of removed stacks
   */
  public int removeRareItem(ItemStack item) {
    List<RareItemEntry> toRemove = new ArrayList<>();
    int metaData = (int) item.toContainer().get(DataQuery.of("UnsafeDamage")).orElse(0);

    for (RareItemEntry e : this.rareItemList) {
      if (e.getItemStack().getItem().equals(item.getItem()))
        if ((int) e.getItemStack().toContainer().get(DataQuery.of("UnsafeDamage")).orElse(0) == metaData)
          toRemove.add(e);
    }

    int removed = 0;
    for (RareItemEntry e : toRemove) {
      this.rareItemList.remove(e);
      removed++;
    }
    return removed;
  }

  public boolean isInRange(Player p) {
    return this.location.getExtent().equals(p.getLocation().getExtent())
        && this.location.getPosition().distance(p.getLocation().getPosition()) <= RADIUS;
  }

  public List<ItemStack> getHarvest() {
    List<ItemStack> harvest = new ArrayList<>();
    
    for (ItemStack stack : this.itemList) {
      harvest.add(stack.copy());
    }

    float rand = new Random().nextFloat();
    float cumulatedProbability = 0f;

    for (RareItemEntry e : this.rareItemList) {
      cumulatedProbability += e.getProbability();
      if (rand <= cumulatedProbability) {
        harvest.add(e.getItemStack().copy());
        break;
      }
    }

    return harvest;
  }
}
