package net.mcfr.tribe.resources.lists;

import static net.mcfr.tribe.Coefficients.CONFORT_N2_NEED_COEF;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import net.mcfr.tribe.PopulationDummy;
import net.mcfr.tribe.resources.ConfortResourceN1Dummy;

public class ConfortResourceN2ListTest {
	private ConfortResourceN2ListDummy resourceList;
	private ConfortResourceN1Dummy resourceN1;
	private ConfortResourceN1ListDummy confortResourcesN1;
	private float[] costTable;
	private PopulationDummy population;
	
	@Before
	public void setUp() throws Exception {
		resourceList = new ConfortResourceN2ListDummy();
		resourceN1 = new ConfortResourceN1Dummy();
		confortResourcesN1 = new ConfortResourceN1ListDummy();
		population = new PopulationDummy();
		costTable = new float[]{1, 2};
	}

	@Test
	public void testCalculateNeeds() {
		resourceList.calculateNeeds(population);
		assertEquals(3f * CONFORT_N2_NEED_COEF.getValue() - 6.66667f, resourceList.get(0).getNeed(), 0.0001f);
		assertEquals(3f * CONFORT_N2_NEED_COEF.getValue() - 6.66667f, resourceList.get(1).getNeed(), 0.0001f);
	}
	
	@Test
	public void testGetCostsNeeds() {
		assertEquals(1.05882f, resourceList.getCostsNeeds(costTable, resourceN1, population)[0], 0.0001f);
		assertEquals(24f, resourceList.getCostsNeeds(costTable, resourceN1, population)[1], 0.0001f);
	}
	
	@Test
	public void testGetFabricationQuantity() {
		assertEquals(30f, resourceList.getFabricationQuantity(population, costTable), 0.0001f);
	}
	
	@Test
	public void testProductResources() {
		confortResourcesN1.get(0).addToStock(70f);
		confortResourcesN1.calculateNeeds(population, resourceList);
		resourceList.productResources(population, confortResourcesN1);
		assertEquals(90f, confortResourcesN1.get(0).getStock(), 0.0001f);
		assertEquals(20f, resourceList.get(0).getStock(), 0.0001f);
		assertEquals(20f, resourceList.get(1).getStock(), 0.0001f);
		
		confortResourcesN1.get(1).addToStock(70f);
		confortResourcesN1.calculateNeeds(population, resourceList);
		resourceList.productResources(population, confortResourcesN1);
		assertEquals(78f, confortResourcesN1.get(0).getStock(), 0.0001f);
		assertEquals(78f, confortResourcesN1.get(1).getStock(), 0.0001f);
		assertEquals(24f, resourceList.get(0).getStock(), 0.0001f);
		assertEquals(24f, resourceList.get(1).getStock(), 0.0001f);
	}
}
