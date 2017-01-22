package net.mcfr.tribe.resources;

import static net.mcfr.tribe.Coefficients.WEAPONS_NEED_COEF;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import net.mcfr.tribe.PopulationDummy;

public class ForgeResourceTest {
	private ForgeResourceDummy resource;
	private PopulationDummy population;
	private WeaponDummy weapon;
	
	@Before
	public void setUp() throws Exception {
		resource = new ForgeResourceDummy();
		population = new PopulationDummy();
		weapon = new WeaponDummy();
	}

	@Test
	public void testNewForgeResource() {
		assertEquals("ForgeResourceTest", resource.getName());
		assertEquals(0f, resource.getNeed(), 0.0001f);
		assertEquals(0.2f, resource.getProduction(), 0.0001f);
		assertEquals(0f, resource.getSaleableQuantity(), 0.0001f);
		assertEquals(20f, resource.getStock(), 0.0001f);
		assertEquals(0f, resource.getValue(), 0.0001f);
		assertEquals(5f, resource.getExchangeThreshold(), 0.0001f);
		assertEquals(1f, resource.getForgeCost(), 0.0001f);
	}
	
	@Test
	public void testCalculateDisponibility() {
		resource.calculateDisponibility(population, weapon);
		assertEquals(true, resource.isDisponible());
		
		resource.removeFromStock(19);
		resource.calculateDisponibility(population, weapon);
		assertEquals(false, resource.isDisponible());
	}
	
	@Test
	public void testCalculateNeed() {
		weapon.calculateWeaponNeed(5f);
		resource.calculateNeed(population, weapon);
		assertEquals(0.38461f * WEAPONS_NEED_COEF.getValue() - 13.46153f, resource.getNeed(), 0.0001f);
		
		resource.removeFromStock(10);
		resource.calculateNeed(population, weapon);
		assertEquals(0.38461f * WEAPONS_NEED_COEF.getValue() - 9.61538f, resource.getNeed(), 0.0001f);
	}
	
	@Test
	public void testCalculateValue() {
		resource.calculateValue(0f);
		assertEquals(15f, resource.getSaleableQuantity(), 0.0001f);
		
		resource.removeFromStock(10);
		resource.calculateValue(0.5f);
		assertEquals(5f, resource.getSaleableQuantity(), 0.0001f);
		
		resource.removeFromStock(7);
		resource.calculateValue(0.5f);
		assertEquals(0f, resource.getSaleableQuantity(), 0.0001f);
	}
	
	@Test
	public void testConsume() {
		resource.consume(population, weapon);
		assertEquals(18.4f, resource.getStock(), 0.0001f);
	}
}