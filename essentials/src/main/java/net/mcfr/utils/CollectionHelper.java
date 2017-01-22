package net.mcfr.utils;

import java.util.Collection;
import java.util.Iterator;

public class CollectionHelper {

  public static Object getLastElement(final Collection<?> c) {
    final Iterator<?> it = c.iterator();
    Object lastElement = it.next();
    while (it.hasNext()) {
      lastElement = it.next();
    }
    return lastElement;
  }
}
