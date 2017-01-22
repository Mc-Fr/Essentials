package net.mcfr.tribe.resources;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import net.mcfr.tribe.PopulationDummy;

public class ResourceTest {
	private ResourceDummy resource;
	private PopulationDummy population;
	
	@Before
	public void setUp() throws Exception {
		resource = new ResourceDummy();
		population = new PopulationDummy();
	}

	@Test
	public void testNewResource() {
		assertEquals("ResourceTest", resource.getName());
		assertEquals(0f, resource.getNeed(), 0.0001f);
		assertEquals(0.2f, resource.getProduction(), 0.0001f);
		assertEquals(0f, resource.getSaleableQuantity(), 0.0001f);
		assertEquals(20f, resource.getStock(), 0.0001f);
		assertEquals(0f, resource.getValue(), 0.0001f);
		assertEquals(5f, resource.getExchangeThreshold(), 0.0001f);
	}
	
	@Test
	public void testAddToStock() {
		resource.addToStock(5);
		assertEquals(25f, resource.getStock(), 0.0001f);
		resource.removeFromStock(30);
		assertEquals(0f, resource.getStock(), 0.0001f);
	}
	
	@Test
	public void testCalculateValue() {
		resource.calculateValue(0.2f);
		assertEquals(0f, resource.getValue(), 0.0001f);
		resource.setNeed(8);
		resource.calculateValue(0.2f);
		assertEquals(1.6f, resource.getValue(), 0.0001f);
	}
	
	@Test
	public void testProductResource() {
		resource.productResource(population);
		assertEquals(21.6f, resource.getStock(), 0.0001f);
	}
}
