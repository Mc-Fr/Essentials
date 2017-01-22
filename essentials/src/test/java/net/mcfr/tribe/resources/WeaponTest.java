package net.mcfr.tribe.resources;

import static net.mcfr.tribe.Coefficients.WEAPONS_NEED_COEF;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import net.mcfr.tribe.PopulationDummy;
import net.mcfr.tribe.resources.lists.ForgeResourceList;

public class WeaponTest {
	private WeaponDummy weapon;
	private PopulationDummy population;
	private ForgeResourceList forgeResources;
	
	@Before
	public void setUp() throws Exception {
		weapon = new WeaponDummy();
		population = new PopulationDummy();
		forgeResources = new ForgeResourceList();
		forgeResources.add(new ForgeResourceDummy());
		forgeResources.add(new ForgeResourceDummy());
	}

	@Test
	public void testNewWeapon() {
		assertEquals("WeaponTest", weapon.getName());
		assertEquals(0f, weapon.getNeed(), 0.0001f);
		assertEquals(0.2f, weapon.getProduction(), 0.0001f);
		assertEquals(0f, weapon.getSaleableQuantity(), 0.0001f);
		assertEquals(20f, weapon.getStock(), 0.0001f);
		assertEquals(0f, weapon.getValue(), 0.0001f);
		assertEquals(5f, weapon.getExchangeThreshold(), 0.0001f);
		assertEquals(0.1f, weapon.getBreakingCoefficient(), 0.0001f);
	}
	
	@Test
	public void testBreakWeapons() {
		weapon.breakWeapons(population);
		assertEquals(19.8f, weapon.getStock(), 0.0001f);
	}
	
	@Test
	public void testCalculateValue() {
		weapon.calculateValue(population, 0f, 10f, 1.1f);
		assertEquals(5.9091f, weapon.getSaleableQuantity(), 0.0001f);
	}
	
	@Test
	public void testCalculateWeaponNeed() {
		weapon.calculateWeaponNeed(10f);
		assertEquals(-10f + WEAPONS_NEED_COEF.getValue(), weapon.getNeed(), 0.0001f);
	}
	
	@Test
	public void testProductResource() {
		forgeResources.calculateDisponibilities(population, weapon);
		weapon.productResource(population, forgeResources);
		assertEquals(18.4f, forgeResources.get(0).getStock(), 0.0001f);
		assertEquals(18.4f, forgeResources.get(1).getStock(), 0.0001f);
		assertEquals(21.6f, weapon.getStock(), 0.0001f);
		
		forgeResources.get(0).removeFromStock(17.4f);
		forgeResources.calculateDisponibilities(population, weapon);
		weapon.productResource(population, forgeResources);
		assertEquals(1f, forgeResources.get(0).getStock(), 0.0001f);
		assertEquals(18.4f, forgeResources.get(1).getStock(), 0.0001f);
		assertEquals(21.6f, weapon.getStock(), 0.0001f);
	}
}
