package net.mcfr.tribe.resources;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import net.mcfr.tribe.PopulationDummy;

public class ConsumableResourceTest {
	private ConsumableResourceDummy resource;
	private PopulationDummy population;
	
	@Before
	public void setUp() throws Exception {
		resource = new ConsumableResourceDummy();
		population = new PopulationDummy();
	}

	@Test
	public void testNewConsumableResource () {
		assertEquals("ConsumableResourceTest", resource.getName());
		assertEquals(20f, resource.getStock(), 0.0001f);
		assertEquals(2f, resource.getProduction(), 0.0001f);
		assertEquals(5f, resource.getExchangeThreshold(), 0.0001f);
		assertEquals(1f, resource.getConsumption(), 0.0001f);
	}
	
	@Test
	public void testConsume () {
		resource.consume(population);
		assertEquals(10f, resource.getStock(), 0.0001f);
		
		resource.removeFromStock(5f);
		resource.consume(population);
		assertEquals(0f, resource.getStock(), 0.0001f);
	}
}
