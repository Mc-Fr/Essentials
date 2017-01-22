package net.mcfr.tribe;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import net.mcfr.tribe.resources.lists.VitalResourceListDummy;

public class PopulationTest {
	private PopulationDummy population;
	private VitalResourceListDummy vitalResources;
	
	@Before
	public void setUp() throws Exception {
		population = new PopulationDummy();
		vitalResources = new VitalResourceListDummy();
	}

	@Test
	public void testNewPopulation() {
		assertEquals(10, population.getInhabitants());
		assertEquals(2, population.getFighters());
		assertEquals(0.05f, population.getNatality(), 0.0001f);
		assertEquals(0.15f, population.getMortality(), 0.0001f);
		assertEquals(1f, population.getStarvationInfluence(), 0.0001f);
		
		assertEquals(0f, population.getFighterProgress(), 0.0001f);
		assertEquals(0f, population.getBirthProgress(), 0.0001f);
		assertEquals(0f, population.getDeathProgress(), 0.0001f);
		assertEquals(0f, population.getVitalResourcesLacks(), 0.0001f);
		assertEquals(false, population.isBirthPossible());
	}
	
	@Test
	public void testCalculateBirth() {
		population.calculateBirths();
		assertEquals(10, population.getInhabitants());
		assertEquals(0f, population.getBirthProgress(), 0.0001f);
		
		population.setBirthPossible(true);
		population.calculateBirths();
		assertEquals(10, population.getInhabitants());
		assertEquals(0.5f, population.getBirthProgress(), 0.0001f);
		
		population.calculateBirths();
		assertEquals(11, population.getInhabitants());
		assertEquals(0f, population.getBirthProgress(), 0.0001f);
		
		population.setInhabitants(1);
		population.calculateBirths();
		assertEquals(1, population.getInhabitants());
		assertEquals(0f, population.getBirthProgress(), 0.0001f);
	}
	
	@Test
	public void testCalculateDeaths() {
		population.calculateDeaths();
		assertEquals(9, population.getInhabitants());
		assertEquals(0.5f, population.getDeathProgress(), 0.0001f);
		
		population.setInhabitants(10);
		population.setVitalResourcesLacks(2);
		population.calculateDeaths();
		assertEquals(5, population.getInhabitants());
		assertEquals(0f, population.getDeathProgress(), 0.0001f);
	}
	
	@Test
	public void testGetWorkers() {
		assertEquals(8, population.getWorkers());
	}
	
	@Test
	public void testIncreaseVitalResourcesLacks() {
		population.increaseVitalResourcesLacks();
		assertEquals(1f, population.getVitalResourcesLacks(), 0.0001f);
	}
	
	@Test
	public void testTrainNewFighters() {
		vitalResources.calculateDisponibilities(population);
		population.trainNewFighters(vitalResources, 30f, 5f);
		assertEquals(0f, population.getFighterProgress(), 0.0001f);
		assertEquals(2, population.getFighters());
		assertEquals(20f, vitalResources.get(0).getStock(), 0.0001f);

		vitalResources.calculateDisponibilities(population);
		population.trainNewFighters(vitalResources, 0.5f, 5f);
		assertEquals(0.5f, population.getFighterProgress(), 0.0001f);
		assertEquals(19.5f, vitalResources.get(0).getStock(), 0.0001f);
		
		vitalResources.calculateDisponibilities(population);
		population.trainNewFighters(vitalResources, 0.6f, 5f);
		assertEquals(0.1f, population.getFighterProgress(), 0.0001f);
		assertEquals(3, population.getFighters());

		vitalResources.calculateDisponibilities(population);
		population.trainNewFighters(vitalResources, 0.5f, 0.1f);
		assertEquals(0.2f, population.getFighterProgress(), 0.0001f);
		assertEquals(3, population.getFighters());
		assertEquals(18.8f, vitalResources.get(0).getStock(), 0.0001f);
	}
}
