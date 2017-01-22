package net.mcfr.tribe.resources.lists;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import net.mcfr.tribe.PopulationDummy;

public class ConstructionResourceListTest {
	private ConstructionResourceListDummy resourceList;
	private PopulationDummy population;
	private float[] costTable;
	
	
	@Before
	public void setUp() throws Exception {
		resourceList = new ConstructionResourceListDummy();
		population = new PopulationDummy();
		costTable = new float[]{50, 50};
	}

	@Test
	public void testSetConstructionCost() {
		resourceList.setConstructionCost(costTable);
		assertEquals(50f, resourceList.get(0).getConstructionProjectCost(), 0.0001f);
		assertEquals(50f, resourceList.get(1).getConstructionProjectCost(), 0.0001f);
	}
	
	@Test
	public void testCalculateNeeds() {
		resourceList.setConstructionCost(costTable);
		resourceList.calculateNeeds(population);
		assertEquals(1.7647f, resourceList.get(0).getNeed(), 0.0001f);
		assertEquals(1.7647f, resourceList.get(1).getNeed(), 0.0001f);
	}

}
