package net.mcfr.tribe;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import net.mcfr.tribe.resources.ConfortResourceN1Test;
import net.mcfr.tribe.resources.ConfortResourceN2Test;
import net.mcfr.tribe.resources.ConstructionResourceTest;
import net.mcfr.tribe.resources.ConsumableResourceTest;
import net.mcfr.tribe.resources.ForgeResourceTest;
import net.mcfr.tribe.resources.ResourceTest;
import net.mcfr.tribe.resources.VitalResourceTest;
import net.mcfr.tribe.resources.WeaponTest;
import net.mcfr.tribe.resources.lists.ConfortResourceN1ListTest;
import net.mcfr.tribe.resources.lists.ConfortResourceN2ListTest;
import net.mcfr.tribe.resources.lists.ConstructionResourceListTest;
import net.mcfr.tribe.resources.lists.ForgeResourceListTest;
import net.mcfr.tribe.resources.lists.ResourceListTest;
import net.mcfr.tribe.resources.lists.VitalResourceListTest;

@RunWith(Suite.class)
@SuiteClasses({ConfortResourceN1ListTest.class,
			   ConfortResourceN2ListTest.class,
			   ConstructionResourceListTest.class,
			   ForgeResourceListTest.class,
			   ResourceListTest.class,
			   VitalResourceListTest.class,
			   ConfortResourceN1Test.class,
			   ConfortResourceN2Test.class,
			   ConstructionResourceTest.class,
			   ForgeResourceTest.class,
			   VitalResourceTest.class,
			   ConsumableResourceTest.class,
			   ResourceTest.class,
			   WeaponTest.class,
			   PopulationTest.class,
			   TribeTest.class})
public class AllTribeTests {
	
}
