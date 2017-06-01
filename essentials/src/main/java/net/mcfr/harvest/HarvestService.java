package net.mcfr.harvest;

import java.util.List;

import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import net.mcfr.roleplay.Skill;
import net.mcfr.utils.McFrPlayer;

public interface HarvestService {
  
  public void loadFromDatabase();
  
  public List<HarvestArea> getHarvestAreas();
  
  public void addArea(String name, Location<World> loc, Skill skill);
  
  public void removeArea(HarvestArea area);
  
  public void addItemEntry(ItemStack item, HarvestArea area);
  
  public void addRareItemEntry(ItemStack item, float probability, HarvestArea area);
  
  public void removeItemEntry(ItemStack item, HarvestArea area);
  
  public void removeRareItemEntry(ItemStack item, HarvestArea area);
  
  public List<HarvestArea> getAreasForPlayer(McFrPlayer p);
  
  public void askForHarvest(McFrPlayer p, HarvestArea area);
  
  public void harvest(McFrPlayer p, HarvestArea area);
}
