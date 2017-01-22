package net.mcfr.tribe.resources.lists;

import java.util.Iterator;
import java.util.LinkedList;

import net.mcfr.tribe.Population;
import net.mcfr.tribe.resources.ConsumableResource;
import net.mcfr.tribe.resources.Resource;

public abstract class ResourceList<T extends Resource> {
  /** Liste liée de ressources */
  private LinkedList<T> list;
  protected Iterator<T> it;
  
  public ResourceList() {
    this.list = new LinkedList<>();
  }
  
  public void add(T ressource) {
    this.list.add(ressource);
  }
  
  public int size() {
    return this.list.size();
  }
  
  public T get(int index) {
    return this.list.get(index);
  }
  
  public Iterator<T> iterator() {
    return this.list.iterator();
  }
  
  public void consume(Population population) {
    for (Resource r : this.list) {
      ((ConsumableResource) r).consume(population);
    }
  }
  
  public void productResources(Population population) {
    this.it = iterator();
    while (this.it.hasNext()) {
      this.it.next().productResource(population);
    }
  }
  
  public void calculateValues(Population population, float priceFlexibility) {
    this.it = iterator();
    while (this.it.hasNext()) {
      this.it.next().calculateValue(priceFlexibility);
    }
  }
  
  @Override
  public String toString() {
    String result = "";
    for (Resource r : this.list) {
      result += r;
    }
    return result;
  }
}
