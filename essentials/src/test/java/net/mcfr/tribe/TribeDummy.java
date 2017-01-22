package net.mcfr.tribe;

import java.util.HashMap;
import java.util.LinkedList;

import net.mcfr.tribe.resources.ConfortResourceN1Dummy;
import net.mcfr.tribe.resources.ConfortResourceN2Dummy;
import net.mcfr.tribe.resources.ConstructionResourceDummy;
import net.mcfr.tribe.resources.ForgeResourceDummy;
import net.mcfr.tribe.resources.Resource;
import net.mcfr.tribe.resources.VitalResourceDummy;
import net.mcfr.tribe.resources.Weapon;
import net.mcfr.tribe.resources.WeaponDummy;
import net.mcfr.tribe.resources.lists.ConfortResourceN1List;
import net.mcfr.tribe.resources.lists.ConfortResourceN2List;
import net.mcfr.tribe.resources.lists.ConstructionResourceList;
import net.mcfr.tribe.resources.lists.ForgeResourceList;
import net.mcfr.tribe.resources.lists.VitalResourceList;

public class TribeDummy extends Tribe {
  private static HashMap<String, Float> map;
  private static float[][] confortCostTable;
  private static LinkedList<Resource> resources;
  
  static {
    map = new HashMap<>();
    confortCostTable = new float[][] { { 1, 2 }, { 3, 4 } };
    resources = new LinkedList<>();
    
    map.put("weaponEfficiency", 1.05f);
    map.put("fightersEfficiency", 1.1f);
    map.put("fightersFormationPerDay", 0.5f);
    map.put("exchangeMinimumReputation", 50f);
    map.put("priceFlexibility", 0.05f);
    map.put("cupidity", 0.1f);
    map.put("tribes", 2f);
    
    resources.add(new VitalResourceDummy());
    resources.add(new VitalResourceDummy());
    resources.add(new ForgeResourceDummy());
    resources.add(new ForgeResourceDummy());
    resources.add(new ConstructionResourceDummy());
    resources.add(new ConstructionResourceDummy());
    resources.add(new WeaponDummy());
    resources.add(new ConfortResourceN1Dummy());
    resources.add(new ConfortResourceN1Dummy());
    resources.add(new ConfortResourceN2Dummy());
    resources.add(new ConfortResourceN2Dummy());
  }
  
  public TribeDummy() {
    super(0, "TribeTest", map, confortCostTable, resources, true);
    this.population = new PopulationDummy();
  }
  
  public int getId() {
    return this.id;
  }
  
  public String getName() {
    return this.name;
  }
  
  public float getWeaponEfficiency() {
    return this.weaponEfficiency;
  }
  
  public float getFightersEfficiency() {
    return this.fightersEfficiency;
  }
  
  public float getFightersFormationPerDay() {
    return this.fightersFormationPerDay;
  }
  
  public float getExchangeMinimumReputation() {
    return this.exchangeMinimumReputation;
  }
  
  public float getPriceFlexibility() {
    return this.priceFlexibility;
  }
  
  public float getCupidity() {
    return this.cupidity;
  }
  
  public float getFightersNeed() {
    return this.fightersNeed;
  }
  
  public float getThreat() {
    return this.threat;
  }
  
  public VitalResourceList getVitalResources() {
    return this.vitalResources;
  }
  
  public ForgeResourceList getForgeResources() {
    return this.forgeResources;
  }
  
  public ConstructionResourceList getConstructionResources() {
    return this.constructionResources;
  }
  
  public ConfortResourceN1List getConfortResourcesN1() {
    return this.confortResourcesN1;
  }
  
  public ConfortResourceN2List getConfortResourcesN2() {
    return this.confortResourcesN2;
  }
  
  public Weapon getWeapon() {
    return this.weapon;
  }
  
  public float[][] getExchanges() {
    return this.exchanges;
  }
  
  public boolean[][] getWars() {
    return this.wars;
  }
  
  public Population getPopulation() {
    return this.population;
  }
}
