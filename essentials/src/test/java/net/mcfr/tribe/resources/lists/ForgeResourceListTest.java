package net.mcfr.tribe.resources.lists;

import static net.mcfr.tribe.Coefficients.WEAPONS_NEED_COEF;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import net.mcfr.tribe.PopulationDummy;
import net.mcfr.tribe.resources.ForgeResourceDummy;
import net.mcfr.tribe.resources.WeaponDummy;

public class ForgeResourceListTest {
	private ForgeResourceList resourceList;
	private PopulationDummy population;
	private WeaponDummy weapon;
	
	@Before
	public void setUp() throws Exception {
		resourceList = new ForgeResourceList();
		resourceList.add(new ForgeResourceDummy());
		resourceList.add(new ForgeResourceDummy());
		population = new PopulationDummy();
		weapon = new WeaponDummy();
	}

	@Test
	public void testCalculateDisponibilities() {
		resourceList.calculateDisponibilities(population, weapon);
		assertEquals(true, resourceList.get(0).isDisponible());
		assertEquals(true, resourceList.get(1).isDisponible());
	}
	
	@Test
	public void testCalculateNeeds() {
		weapon.calculateWeaponNeed(5f);
		resourceList.calculateNeeds(population, weapon);
		assertEquals(0.38461f * WEAPONS_NEED_COEF.getValue() - 13.46153f, resourceList.get(0).getNeed(), 0.0001f);
		assertEquals(0.38461f * WEAPONS_NEED_COEF.getValue() - 13.46153f, resourceList.get(1).getNeed(), 0.0001f);
	}
	
	@Test
	public void testConsumeForForge() {
		resourceList.consumeForForge(population, weapon);
		assertEquals(18.4f, resourceList.get(0).getStock(), 0.0001f);
		assertEquals(18.4f, resourceList.get(1).getStock(), 0.0001f);
	}
	
	@Test
	public void testIsAvailableForForge() {
		resourceList.calculateDisponibilities(population, weapon);
		assertEquals(true, resourceList.isAvailableForForge());
		
		resourceList.get(0).removeFromStock(19.5f);
		resourceList.calculateDisponibilities(population, weapon);
		assertEquals(false, resourceList.isAvailableForForge());		
	}
}
