package net.mcfr.tribe.resources;

import static net.mcfr.tribe.Coefficients.CONFORT_N2_NEED_COEF;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import net.mcfr.tribe.PopulationDummy;

public class ConfortResourceN2Test {
	private ConfortResourceN2Dummy resource;
	private ConfortResourceN1Dummy resourceN1;
	private PopulationDummy population;

	@Before
	public void setUp() throws Exception {
		resource = new ConfortResourceN2Dummy();
		resourceN1 = new ConfortResourceN1Dummy();
		population = new PopulationDummy();
	}
	
	@Test
	public void testNewConfortResourceN2() {
		assertEquals(20f, resource.getStock(), 0.0001f);
		assertEquals(0.2f, resource.getConsumption(), 0.0001f);
		assertEquals(0.5f, resource.getProduction(), 0.0001f);
		assertEquals(3f, resource.getSecurityThreshold(), 0.0001f);
		assertEquals(5f, resource.getExchangeThreshold(), 0.0001f);
		assertEquals("ConfortResourceN2Test", resource.getName());		
	}
	
	@Test
	public void testCalculateNeed() {
		resource.calculateNeed(population);
		assertEquals(3f * CONFORT_N2_NEED_COEF.getValue() - 6.66667f, resource.getNeed(), 0.0001f);
	}
	
	@Test
	public void testCalculateValue() {
		resource.calculateValue(population, 0f);
		assertEquals(10f, resource.getSaleableQuantity(), 0.0001f);
	}
	
	@Test
	public void testGetCostsNeed() {
		assertEquals(0.70588f, resource.getCostsNeed(2f, resourceN1, population)[0], 0.0001f);
		assertEquals(16f, resource.getCostsNeed(2f, resourceN1, population)[1], 0.0001f);
	}
	
	@Test
	public void testGetFabricationQuantity() {
		assertEquals(20f, resource.getFabricationQuantity(population, 2f), 0.0001f);
		assertEquals(10f, resource.getFabricationQuantity(population, 1f), 0.0001f);
	}
}
