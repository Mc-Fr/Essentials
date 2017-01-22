package net.mcfr.tribe.resources.lists;

import static net.mcfr.tribe.Coefficients.CONFORT_N1_NEED_COEF;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import net.mcfr.tribe.PopulationDummy;
import net.mcfr.tribe.resources.ConfortResourceN1Dummy;
import net.mcfr.tribe.resources.ConfortResourceN2Dummy;

public class ConfortResourceN1ListTest {
	private ConfortResourceN1ListDummy resourceList;
	private ConfortResourceN2ListDummy confortResourcesN2;
	private ConfortResourceN2Dummy resourceN2;
	private PopulationDummy population;

	@Before
	public void setUp() throws Exception {
		resourceList = new ConfortResourceN1ListDummy();
		confortResourcesN2 = new ConfortResourceN2ListDummy();
		resourceN2 = new ConfortResourceN2Dummy();
		population = new PopulationDummy();
	}

	@Test
	public void testCalculateNeeds() {
		resourceList.calculateNeeds(population, confortResourcesN2);
		assertEquals(3f * CONFORT_N1_NEED_COEF.getValue() - 0.75936f, resourceList.get(0).getNeed(), 0.0001f);
		assertEquals(3f * CONFORT_N1_NEED_COEF.getValue() - 0.75936f, resourceList.get(1).getNeed(), 0.0001f);
	}
	
	@Test
	public void testCalculateValues() {
		resourceList.get(0).addToStock(70f);
		resourceList.calculateValues(population, 0f, confortResourcesN2);
		assertEquals(10f, ((ConfortResourceN1Dummy)resourceList.get(0)).getSaleableQuantity(), 0.0001f);
		assertEquals(0f, ((ConfortResourceN1Dummy)resourceList.get(1)).getSaleableQuantity(), 0.0001f);
	}
	
	@Test
	public void testConsumeForFabrication() {
		resourceList.consumeForFabrication(population, resourceN2, 0);
		assertEquals(16f, resourceList.get(0).getStock(), 0.0001f);
		assertEquals(16f, resourceList.get(1).getStock(), 0.0001f);
	}
	
	@Test
	public void testIsAvailableForFabrication() {
		resourceList.get(1).addToStock(70f);
		resourceList.calculateNeeds(population, confortResourcesN2);
		assertEquals(false, resourceList.isAvailableForFabrication());
		
		resourceList.get(0).addToStock(70f);
		resourceList.calculateNeeds(population, confortResourcesN2);
		assertEquals(true, resourceList.isAvailableForFabrication());
	}
}
