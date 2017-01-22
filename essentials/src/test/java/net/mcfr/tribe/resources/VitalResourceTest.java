package net.mcfr.tribe.resources;

import static net.mcfr.tribe.Coefficients.VITAL_NEED_COEF;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import net.mcfr.tribe.PopulationDummy;

public class VitalResourceTest {
	private VitalResourceDummy resource;
	private PopulationDummy population;
	
	@Before
	public void setUp() throws Exception {
		resource = new VitalResourceDummy();
		population = new PopulationDummy();
	}

	@Test
	public void testNewVitalResource() {
		assertEquals("VitalResourceTest", resource.getName());
		assertEquals(0f, resource.getNeed(), 0.0001f);
		assertEquals(0.2f, resource.getProduction(), 0.0001f);
		assertEquals(0f, resource.getSaleableQuantity(), 0.0001f);
		assertEquals(20f, resource.getStock(), 0.0001f);
		assertEquals(0f, resource.getValue(), 0.0001f);
		assertEquals(5f, resource.getExchangeThreshold(), 0.0001f);
		assertEquals(1f, resource.getFormationCost(), 0.0001f);
	}
	
	@Test
	public void testCalculateDisponibility() {
		resource.calculateDisponibility(population);
		assertEquals(true, resource.isDisponible());
		
		resource.removeFromStock(18);
		resource.calculateDisponibility(population);
		assertEquals(false, resource.isDisponible());
	}
	
	@Test
	public void testCalculateNeed() {
		resource.calculateNeed(population, 3f);
		assertEquals(VITAL_NEED_COEF.getValue() * 3f - 8.84615f, resource.getNeed(), 0.0001f);
		
		resource.removeFromStock(10);
		resource.calculateNeed(population, 3f);
		assertEquals(VITAL_NEED_COEF.getValue() * 3f - 3.84615f, resource.getNeed(), 0.0001f);
		
		resource.calculateNeed(population, 0f);
		assertEquals(VITAL_NEED_COEF.getValue() * 3f - 5f, resource.getNeed(), 0.0001f);
	}
	
	@Test
	public void testCalculateValue() {
		resource.calculateValue(population, 0f);
		assertEquals(15f, resource.getSaleableQuantity(), 0.0001f);
		
		resource.removeFromStock(10);
		resource.calculateValue(population, 0.5f);
		assertEquals(5f, resource.getSaleableQuantity(), 0.0001f);
		
		resource.removeFromStock(7);
		resource.calculateValue(population, 0.5f);
		assertEquals(0f, resource.getSaleableQuantity(), 0.0001f);
	}
	
	@Test
	public void testConsumeForTraining() {
		resource.consumeForTraining(0f);
		assertEquals(20f, resource.getStock(), 0.0001f);
		
		resource.consumeForTraining(2.5f);
		assertEquals(17.5f, resource.getStock(), 0.0001f);
	}
	
	@Test
	public void testAvailabilityForTraining () {
		resource.calculateDisponibility(population);
		assertEquals(true, resource.isAvailableForTraining(1f));
		
		resource.removeFromStock(15);
		resource.calculateDisponibility(population);
		assertEquals(false, resource.isAvailableForTraining(7f));
		assertEquals(true, resource.isAvailableForTraining(0.1f));
		
		resource.removeFromStock(2);
		resource.calculateDisponibility(population);
		assertEquals(false, resource.isAvailableForTraining(0f));
	}
}
