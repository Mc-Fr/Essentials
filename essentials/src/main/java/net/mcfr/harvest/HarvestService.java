package net.mcfr.harvest;

import java.util.List;

import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import net.mcfr.roleplay.Skill;
import net.mcfr.utils.McFrPlayer;

public interface HarvestService {
  
  public void loadFromDatabase();
  
  public List<HarvestArea> getHarvestAreas();
  
  public void addArea(String name, Location<World> loc, Skill skill);
  
  public List<HarvestArea> getAreasForPlayer(McFrPlayer p);
  
  public void askForHarvest(McFrPlayer p, HarvestArea area);
  
  public void harvest(McFrPlayer p, HarvestArea area);
}
