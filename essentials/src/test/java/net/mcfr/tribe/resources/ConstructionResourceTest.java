package net.mcfr.tribe.resources;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import net.mcfr.tribe.PopulationDummy;

public class ConstructionResourceTest {
	private ConstructionResourceDummy resource;
	private PopulationDummy population;

	@Before
	public void setUp() throws Exception {
		resource = new ConstructionResourceDummy();
		population = new PopulationDummy();
	}

	@Test
	public void testNewConstructionResource() {
		assertEquals("ConstructionResourceTest", resource.getName());
		assertEquals(20f, resource.getStock(), 0.0001f);
		assertEquals(2f, resource.getProduction(), 0.0001f);
		assertEquals(5f, resource.getExchangeThreshold(), 0.0001f);
		assertEquals(0f, resource.getConstructionProjectCost(), 0.0001f);
	}
	
	@Test
	public void testCalculateNeed() {
		resource.setConstructionCost(50f);
		resource.calculateNeed(population);
		assertEquals(1.7647f, resource.getNeed(), 0.0001f);
	}
	
	@Test
	public void testCalculateValue() {
		resource.setConstructionCost(50f);
		resource.calculateValue(2f);
		assertEquals(0f, resource.getSaleableQuantity(), 0.0001f);
		
		resource.addToStock(500f);
		resource.calculateValue(2f);
		assertEquals(270f, resource.getSaleableQuantity(), 0.0001f);
	}
}
