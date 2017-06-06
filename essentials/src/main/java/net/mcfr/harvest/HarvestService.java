package net.mcfr.harvest;

import java.util.List;

import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import net.mcfr.dao.HarvestTools;
import net.mcfr.roleplay.Skill;
import net.mcfr.utils.McFrPlayer;

public interface HarvestService {
  
  public void loadFromDatabase();
  
  public boolean isNameFree(String name);
  
  public void addArea(String name, String displayName, Location<World> loc, Skill skill, HarvestTools tool, int toolDamage);
  
  public void removeArea(HarvestArea area);
  
  public void addItemEntry(ItemStack item, HarvestArea area);
  
  public void addRareItemEntry(ItemStack item, double probability, HarvestArea area);
  
  public void removeItemEntry(ItemStack item, HarvestArea area);
  
  public void removeRareItemEntry(ItemStack item, HarvestArea area);
  
  public List<HarvestArea> getAreasForPlayer(McFrPlayer p);
  
  public List<HarvestArea> getAreasAround(McFrPlayer p);
  
  public void harvest(McFrPlayer p, HarvestArea area);
}
