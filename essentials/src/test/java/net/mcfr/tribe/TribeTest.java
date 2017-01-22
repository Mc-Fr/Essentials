package net.mcfr.tribe;

import static net.mcfr.tribe.Coefficients.*;
import static org.junit.Assert.*;

import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import net.mcfr.tribe.resources.VitalResourceDummy;

public class TribeTest {
  private TribeDummy tribe;
  private LinkedList<Tribe> tribes;
  private float[] constructionCost;
  
  @Before
  public void setUp() throws Exception {
    this.tribe = new TribeDummy();
    this.tribes = new LinkedList<>();
    this.tribes.add(this.tribe);
    this.tribes.add(new TribeDummy());
    this.constructionCost = new float[] { 10, 20 };
  }
  
  @Test
  public void testNewTribe() {
    assertEquals(0, this.tribe.getId());
    assertEquals("TribeTest", this.tribe.getName());
    assertEquals(1.05f, this.tribe.getWeaponEfficiency(), 0.0001f);
    assertEquals(1.1f, this.tribe.getFightersEfficiency(), 0.0001f);
    assertEquals(0.5f, this.tribe.getFightersFormationPerDay(), 0.0001f);
    assertEquals(50f, this.tribe.getExchangeMinimumReputation(), 0.0001f);
    assertEquals(0.05f, this.tribe.getPriceFlexibility(), 0.0001f);
    assertEquals(0.1f, this.tribe.getCupidity(), 0.0001f);
    
    assertEquals(0f, this.tribe.getFightersNeed(), 0.0001f);
    assertEquals(0f, this.tribe.getThreat(), 0.0001f);
    
    assertEquals(2, this.tribe.getVitalResources().size());
    assertEquals(2, this.tribe.getForgeResources().size());
    assertEquals(2, this.tribe.getConstructionResources().size());
    assertEquals(2, this.tribe.getConfortResourcesN1().size());
    assertEquals(2, this.tribe.getConfortResourcesN2().size());
    
    assertEquals(11, this.tribe.getExchanges().length);
    assertEquals(2, this.tribe.getExchanges()[0].length);
    
    assertEquals(2, this.tribe.getWars().length);
    assertEquals(2, this.tribe.getWars()[0].length);
  }
  
  @Test
  public void testCalculateFightersNeed() {
    this.tribe.setWar(1, true, true);
    this.tribe.calculateThreat(this.tribes);
    this.tribe.calculateFightersNeed();
    assertEquals(0.2f + FIGHTERS_NEED_COEF.getValue(), this.tribe.getFightersNeed(), 0.0001f);
  }
  
  @Test
  public void testSetWar() {
    this.tribe.setWar(1, true, true);
    this.tribe.setWar(0, false, true);
    assertEquals(true, this.tribe.getWars()[0][1]);
    assertEquals(false, this.tribe.getWars()[1][1]);
    assertEquals(true, this.tribe.getWars()[1][0]);
    assertEquals(false, this.tribe.getWars()[0][0]);
  }
  
  @Test
  public void testCalculateThreat() {
    this.tribe.setWar(1, true, true);
    this.tribe.calculateThreat(this.tribes);
    assertEquals(2.2f, this.tribe.getThreat(), 0.0001f);
    
    this.tribe.setWar(1, true, false);
    this.tribe.calculateThreat(this.tribes);
    assertEquals(0f, this.tribe.getThreat(), 0.0001f);
  }
  
  @Test
  public void testGetMilitaryPower() {
    assertEquals(2.2f, this.tribe.getMilitaryPower(), 0.0001f);
    this.tribe.getWeapon().removeFromStock(19f);
    assertEquals(1.05f, this.tribe.getMilitaryPower(), 0.0001f);
    this.tribe.getWeapon().addToStock(19f);
  }
  
  @Test
  public void testNextTurn() {
    this.tribe.setConstructionCost(this.constructionCost);
    this.tribe.getConfortResourcesN1().get(0).addToStock(20f);
    this.tribe.getConfortResourcesN1().get(1).addToStock(20f);
    this.tribe.getVitalResources().get(1).removeFromStock(20f);
    ((VitalResourceDummy) this.tribe.getVitalResources().get(1)).setFormationCost(0f);
    ((VitalResourceDummy) this.tribe.getVitalResources().get(1)).setProduction(0f);
    
    this.tribe.nextTurn(this.tribes, true);
    
    // Vital Resources
    assertEquals(20.1f, this.tribe.getVitalResources().get(0).getStock(), 0.0001f);
    assertEquals(3f * VITAL_NEED_COEF.getValue() - 9.66538f, this.tribe.getVitalResources().get(0).getNeed(), 0.0001f);
    assertEquals(0.15f * VITAL_NEED_COEF.getValue() - 0.48327f, this.tribe.getVitalResources().get(0).getValue(), 0.0001f);
    
    // Forge Resources
    assertEquals(20f, this.tribe.getForgeResources().get(0).getStock(), 0.0001f);
    assertEquals(0.38461f * WEAPONS_NEED_COEF.getValue() - 15.92308f, this.tribe.getForgeResources().get(0).getNeed(), 0.0001f);
    assertEquals(0.01923f * WEAPONS_NEED_COEF.getValue() - 0.79615f, this.tribe.getForgeResources().get(0).getValue(), 0.0001f);
    
    // Construction Resources
    assertEquals(36f, this.tribe.getConstructionResources().get(0).getStock(), 0.0001f);
    assertEquals(-1.52941f, this.tribe.getConstructionResources().get(0).getNeed(), 0.0001f);
    assertEquals(-0.07647f, this.tribe.getConstructionResources().get(0).getValue(), 0.0001f);
    
    // Confort Resources N1
    assertEquals(34f, this.tribe.getConfortResourcesN1().get(0).getStock(), 0.0001f);
    assertEquals(3f * CONFORT_N1_NEED_COEF.getValue() - 2.03209f, this.tribe.getConfortResourcesN1().get(0).getNeed(), 0.0001f);
    assertEquals(0.15f * CONFORT_N1_NEED_COEF.getValue() - 0.10160f, this.tribe.getConfortResourcesN1().get(0).getValue(), 0.0001f);
    
    // Confort Resources N2
    assertEquals(22f, this.tribe.getConfortResourcesN2().get(0).getStock(), 0.0001f);
    assertEquals(3f * CONFORT_N2_NEED_COEF.getValue() - 7.33333f, this.tribe.getConfortResourcesN2().get(0).getNeed(), 0.0001f);
    assertEquals(0.15f * CONFORT_N2_NEED_COEF.getValue() - 0.36667f, this.tribe.getConfortResourcesN2().get(0).getValue(), 0.0001f);
    
    // Weapon
    assertEquals(21.4f, this.tribe.getWeapon().getStock(), 0.0001f);
    assertEquals(WEAPONS_NEED_COEF.getValue() - 21.4f, this.tribe.getWeapon().getNeed(), 0.0001f);
    assertEquals(0.05f * WEAPONS_NEED_COEF.getValue() - 1.07f, this.tribe.getWeapon().getValue(), 0.0001f);
    
    // Population
    assertEquals(7, this.tribe.getPopulation().getInhabitants());
    assertEquals(2, this.tribe.getPopulation().getFighters());
    assertEquals(0.5f, ((PopulationDummy) this.tribe.getPopulation()).getFighterProgress(), 0.0001f);
    assertEquals(1f, ((PopulationDummy) this.tribe.getPopulation()).getVitalResourcesLacks(), 0.0001f);
    assertEquals(0f, ((PopulationDummy) this.tribe.getPopulation()).getBirthProgress(), 0.0001f);
    assertEquals(0f, ((PopulationDummy) this.tribe.getPopulation()).getDeathProgress(), 0.0001f);
    
    // Test de non-actualisation de la démographie si c'est le jour
    this.tribe.nextTurn(this.tribes, false);
    assertEquals(7, this.tribe.getPopulation().getInhabitants());
    assertEquals(0f, ((PopulationDummy) this.tribe.getPopulation()).getBirthProgress(), 0.0001f);
    assertEquals(0f, ((PopulationDummy) this.tribe.getPopulation()).getDeathProgress(), 0.0001f);
  }
  
  @Test
  public void testSetConstructionCost() {
    this.tribe.setConstructionCost(this.constructionCost);
    assertEquals(10f, this.tribe.getConstructionResources().get(0).getConstructionProjectCost(), 0.0001f);
    assertEquals(20f, this.tribe.getConstructionResources().get(1).getConstructionProjectCost(), 0.0001f);
  }
}
