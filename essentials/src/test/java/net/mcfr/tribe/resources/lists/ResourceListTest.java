package net.mcfr.tribe.resources.lists;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import net.mcfr.tribe.PopulationDummy;
import net.mcfr.tribe.resources.ConsumableResourceDummy;

public class ResourceListTest {
  private ResourceListDummy<ConsumableResourceDummy> resourceList;
  private PopulationDummy population;
  private float priceFlexibility;
  private ConsumableResourceDummy consumableResource;

  @Before
  public void setUp() throws Exception {
    this.resourceList = new ResourceListDummy<>();
    this.consumableResource = new ConsumableResourceDummy();
    this.population = new PopulationDummy();
    this.priceFlexibility = 0.2f;
    this.resourceList.add(this.consumableResource);
  }

  @Test
  public void testNewSizeAndAdd() {
    assertEquals(1, this.resourceList.size());
  }

  @Test
  public void testCalculateValues() {
    this.consumableResource.setValue(5f);
    this.resourceList.calculateValues(this.population, this.priceFlexibility);
    assertEquals(0f, this.consumableResource.getValue(), 0.0001f);
  }

  @Test
  public void testConsume() {
    this.resourceList.consume(this.population);
    assertEquals(10f, this.consumableResource.getStock(), 0.0001f);
  }

  @Test
  public void testGet() {
    assertEquals(this.consumableResource, this.resourceList.get(0));
  }

  @Test
  public void testProductResources() {
    this.resourceList.productResources(this.population);
    assertEquals(36f, this.consumableResource.getStock(), 0.0001f);
  }
}
