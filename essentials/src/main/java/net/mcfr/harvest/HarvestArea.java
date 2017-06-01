package net.mcfr.harvest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.spongepowered.api.GameRegistry;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import net.mcfr.roleplay.Skill;

public class HarvestArea {
  private final static float RADIUS = 5f;
  private final static Map<Skill, List<ItemType>> toolMap = new HashMap<>();

  static {
    GameRegistry registry = Sponge.getGame().getRegistry();

    List<ItemType> woodcutterList = new ArrayList<>();
    List<ItemType> minerList = new ArrayList<>();
    List<ItemType> fishermanList = new ArrayList<>();
    List<ItemType> hunterList = new ArrayList<>();
    List<ItemType> farmerList = new ArrayList<>();
    List<ItemType> breederList = new ArrayList<>();

    woodcutterList.add(ItemTypes.DIAMOND_AXE);
    woodcutterList.add(ItemTypes.GOLDEN_AXE);
    woodcutterList.add(ItemTypes.IRON_AXE);

    minerList.add(ItemTypes.DIAMOND_PICKAXE);
    minerList.add(ItemTypes.GOLDEN_PICKAXE);
    minerList.add(ItemTypes.IRON_PICKAXE);

    fishermanList.add(ItemTypes.FISHING_ROD);
    fishermanList.add(registry.getType(ItemType.class, "mcfr_b_i:good_fishing_rod").get());
    fishermanList.add(registry.getType(ItemType.class, "mcfr_b_i:fishing_net").get());

    hunterList.add(ItemTypes.BOW);
    hunterList.add(registry.getType(ItemType.class, "mcfr_b_i:iron_bow").get());
    hunterList.add(registry.getType(ItemType.class, "mcfr_b_i:golden_bow").get());
    hunterList.add(registry.getType(ItemType.class, "mcfr_b_i:steel_bow").get());
    hunterList.add(registry.getType(ItemType.class, "mcfr_b_i:barbarian_bow").get());
    hunterList.add(registry.getType(ItemType.class, "mcfr_b_i:long_bow").get());
    hunterList.add(registry.getType(ItemType.class, "mcfr_b_i:hunter_bow").get());
    hunterList.add(registry.getType(ItemType.class, "mcfr_b_i:long_hunter_bow").get());
    hunterList.add(registry.getType(ItemType.class, "mcfr_b_i:ancient_bow").get());

    farmerList.add(ItemTypes.DIAMOND_HOE);
    farmerList.add(ItemTypes.GOLDEN_HOE);
    farmerList.add(ItemTypes.IRON_HOE);

    breederList.add(ItemTypes.SHEARS);

    toolMap.put(Skill.getSkillByName("bucheron"), woodcutterList);
    toolMap.put(Skill.getSkillByName("minage"), minerList);
    toolMap.put(Skill.getSkillByName("peche"), fishermanList);
    toolMap.put(Skill.getSkillByName("chasse"), hunterList);
    toolMap.put(Skill.getSkillByName("fermier"), farmerList);
    toolMap.put(Skill.getSkillByName("elevage"), breederList);
  }

  private String name;
  private Location<World> location;
  private Skill skill;
  private List<ItemStack> itemList;
  private List<RareItemEntry> rareItemList;

  public HarvestArea(String name, Location<World> loc, Skill skill) {
    this.name = name;
    this.location = loc;
    this.skill = skill;
    this.itemList = new ArrayList<>();
    this.rareItemList = new ArrayList<>();
  }

  public String getName() {
    return this.name;
  }

  public Skill getSkill() {
    return this.skill;
  }

  public Location<World> getLocation() {
    return this.location;
  }

  public boolean isToolCorrect(ItemType tool) {
    for (ItemType i : toolMap.get(this.skill)) {
      if (i.equals(tool))
        return true;
    }
    return false;
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

    harvest.addAll(this.itemList);

    float rand = new Random().nextFloat();
    float cumulatedProbability = 0f;

    for (RareItemEntry e : this.rareItemList) {
      cumulatedProbability += e.getProbability();
      if (rand <= cumulatedProbability) {
        harvest.add(e.getItemStack());
        break;
      }
    }

    return harvest;
  }
}
