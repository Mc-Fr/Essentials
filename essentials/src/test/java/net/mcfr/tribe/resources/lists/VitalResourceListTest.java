package net.mcfr.tribe.resources.lists;

import static net.mcfr.tribe.Coefficients.VITAL_NEED_COEF;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import net.mcfr.tribe.PopulationDummy;

public class VitalResourceListTest {
	private VitalResourceListDummy resourceList;
	private PopulationDummy population;
	
	@Before
	public void setUp() throws Exception {
		resourceList = new VitalResourceListDummy();
		population = new PopulationDummy();
	}
	
	@Test
	public void testCalculateNeeds() {
		resourceList.calculateNeeds(population, 3f);
		assertEquals(VITAL_NEED_COEF.getValue() * 3f - 8.84615f, resourceList.get(0).getNeed(), 0.0001f);
		assertEquals(VITAL_NEED_COEF.getValue() * 3f - 8.84615f, resourceList.get(1).getNeed(), 0.0001f);
	}
	
	@Test
	public void testConsumeForTraining() {
		resourceList.consumeForTraining(2.5f);
		assertEquals(17.5f, resourceList.get(0).getStock(), 0.0001f);
		assertEquals(17.5f, resourceList.get(1).getStock(), 0.0001f);
	}
	
	@Test
	public void testIsAvailableForTraining() {
		resourceList.calculateDisponibilities(population);
		assertEquals(true, resourceList.isAvailableForTraining(0.5f));
		resourceList.get(0).removeFromStock(19f);
		resourceList.calculateDisponibilities(population);
		assertEquals(false, resourceList.isAvailableForTraining(0.5f));
	}
	
	@Test
	public void testProductResources() {
		resourceList.productResources(population);
		assertEquals(21.6f, resourceList.get(0).getStock(), 0.0001f);
		assertEquals(21.6f, resourceList.get(1).getStock(), 0.0001f);
		assertEquals(true, population.isBirthPossible());
	}
}
