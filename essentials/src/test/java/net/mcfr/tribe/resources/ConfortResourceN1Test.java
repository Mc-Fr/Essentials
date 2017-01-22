package net.mcfr.tribe.resources;

import static net.mcfr.tribe.Coefficients.CONFORT_N1_NEED_COEF;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import net.mcfr.tribe.PopulationDummy;
import net.mcfr.tribe.resources.lists.ConfortResourceN2ListDummy;

public class ConfortResourceN1Test {
	private ConfortResourceN1Dummy resource;
	private ConfortResourceN2Dummy resourceN2;
	private ConfortResourceN2ListDummy resourceN2List;
	private PopulationDummy population;
	private float[] costTable;
	
	@Before
	public void setUp() throws Exception {
		resource = new ConfortResourceN1Dummy();
		resourceN2 = new ConfortResourceN2Dummy();
		resourceN2List = new ConfortResourceN2ListDummy();
		population = new PopulationDummy();
		costTable = new float[]{1f, 2f};
	}

	@Test
	public void testNewConfortResourceN1() {
		assertEquals(20f, resource.getStock(), 0.0001f);
		assertEquals(1f, resource.getConsumption(), 0.0001f);
		assertEquals(2f, resource.getProduction(), 0.0001f);
		assertEquals(3f, resource.getSecurityThreshold(), 0.0001f);
		assertEquals(5f, resource.getExchangeThreshold(), 0.0001f);
		assertEquals("ConfortResourceN1Test", resource.getName());
	}
	
	@Test
	public void testCalculateNeed() {
		resource.setCostTable(costTable);
		resource.calculateNeed(resourceN2List, population);
		assertEquals(3f * CONFORT_N1_NEED_COEF.getValue() - 0.75936f, resource.getNeed(), 0.0001f);
		assertEquals(false, resource.isDisponible());
	}
	
	@Test
	public void testCalculateValue() {
		resource.setCostTable(costTable);
		resource.calculateValue(population, 0f, resourceN2List);
		assertEquals(0f, resource.getSaleableQuantity(), 0.0001f);
		
		resource.addToStock(70f);
		resource.calculateValue(population, 0f, resourceN2List);
		assertEquals(10f, resource.getSaleableQuantity(), 0.0001f);
	}
	
	@Test
	public void testConsumeForFabrication() {
		resource.setCostTable(costTable);
		resource.consumeForFabrication(population, resourceN2, 0);
		assertEquals(16f, resource.getStock(), 0.0001f);
		
		resource.consumeForFabrication(population, resourceN2, 1);
		assertEquals(8f, resource.getStock(), 0.0001f);
	}
	
	@Test
	public void testSetCostTableAndGetCost() {
		resource.setCostTable(costTable);
		assertEquals(1f, resource.getCost(0), 0.0001f);
		assertEquals(2f, resource.getCost(1), 0.0001f);
	}
}
