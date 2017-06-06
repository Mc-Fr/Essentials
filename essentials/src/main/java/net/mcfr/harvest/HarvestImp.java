package net.mcfr.harvest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import net.mcfr.dao.DaoFactory;
import net.mcfr.dao.HarvestTools;
import net.mcfr.roleplay.Skill;
import net.mcfr.utils.McFrPlayer;

public class HarvestImp implements HarvestService {
  private final static int SKILL_HARVESTING_LEVEL = 12;

  private static Map<String, HarvestArea> harvestAreas = new HashMap<>();

  public HarvestImp() {
  }
  
  public static Map<String, HarvestArea> getHarvestAreas() {
    return harvestAreas;
  }

  @Override
  public void loadFromDatabase() {
    for (HarvestArea a : DaoFactory.getHarvestDao().getAll())
      harvestAreas.put(a.getName(), a);
  }
  
  @Override
  public boolean isNameFree(String name) {
    for (HarvestArea a : harvestAreas.values())
      if (a.getName().equals(name))
        return false;
    
    return true;
  }

  @Override
  public void addArea(String name, String displayName, Location<World> loc, Skill skill, HarvestTools tool, int toolDamage) {
    HarvestArea newArea = new HarvestArea(name, displayName, loc, skill, tool, toolDamage);
    DaoFactory.getHarvestDao().create(newArea);
    harvestAreas.put(newArea.getName(), newArea);
  }

  @Override
  public void removeArea(HarvestArea area) {
    DaoFactory.getHarvestDao().delete(area);
    harvestAreas.remove(area.getName());
  }

  @Override
  public void addItemEntry(ItemStack item, HarvestArea area) {
    DaoFactory.getHarvestDao().addItemEntry(item, area);
    area.addItem(item);
  }

  @Override
  public void addRareItemEntry(ItemStack item, double probability, HarvestArea area) {
    RareItemEntry entry = new RareItemEntry(probability, item);
    DaoFactory.getHarvestDao().addRareItemEntry(entry, area);
    area.addRareItem(entry);
  }

  @Override
  public void removeItemEntry(ItemStack item, HarvestArea area) {
    DaoFactory.getHarvestDao().removeItemEntry(item, area);
    area.removeItem(item);
  }

  @Override
  public void removeRareItemEntry(ItemStack item, HarvestArea area) {
    DaoFactory.getHarvestDao().removeRareItemEntry(item, area);
    area.removeRareItem(item);
  }

  @Override
  public List<HarvestArea> getAreasForPlayer(McFrPlayer p) {
    List<HarvestArea> areas = new ArrayList<>();

    for (HarvestArea a : harvestAreas.values()) {
      if (a.isInRange(p.getPlayer()) && p.getSkillLevel(a.getSkill(), Optional.empty()) >= SKILL_HARVESTING_LEVEL)
        areas.add(a);
    }

    return areas;
  }
  
  /**
   * Ne prend pas en compte les compétences de récolte.
   * @param p
   * @return La liste des {@code HarvestArea}s aux alentours.
   */
  @Override
  public List<HarvestArea> getAreasAround(McFrPlayer p) {
    List<HarvestArea> areas = new ArrayList<>();

    for (HarvestArea a : harvestAreas.values()) {
      if (a.isInRange(p.getPlayer()))
        areas.add(a);
    }

    return areas;
  }
  
  @Override
  public void harvest(McFrPlayer p, HarvestArea area) {
    if (p.getHarvestTokens() > 0) {
      Optional<ItemStack> optItem = p.getPlayer().getItemInHand(HandTypes.MAIN_HAND);
      if (optItem.isPresent() && area.isToolCorrect(optItem.get().getItem())) {

        List<ItemStack> items = area.getHarvest();
        float tokenValue = 0.01f * p.getTokenValue();
        Inventory inventory = p.getPlayer().getInventory();
        
        for (ItemStack stack : items) {
          stack.setQuantity((int) Math.ceil(tokenValue * stack.getQuantity()));
          inventory.offer(stack);
        }
        
        p.setHarvestTokens(p.getHarvestTokens() - 1);
        
        ItemStack usedTool = area.useTool(optItem.get());
        
        if (usedTool == null) {
          p.getPlayer().playSound(SoundTypes.ENTITY_ITEM_BREAK, p.getPlayer().getLocation().getPosition(), 1);
        }
        
        p.getPlayer().setItemInHand(HandTypes.MAIN_HAND, usedTool);
        
      } else {
        p.sendMessage(Text.of(TextColors.GREEN, "Vous devez avoir un outil correspondant à votre récolte en main."));
      }
    } else {
      p.sendMessage(Text.of(TextColors.GREEN, "Vous n'avez plus de jeton de récolte. Attendez demain !"));
    }
  }
}
