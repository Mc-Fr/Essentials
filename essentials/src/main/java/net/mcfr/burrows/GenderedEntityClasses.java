package net.mcfr.burrows;

import net.mcfr.entities.mobs.entity.EntityBormoth;
import net.mcfr.entities.mobs.entity.EntityHoen;
import net.mcfr.entities.mobs.entity.EntitySiker;
import net.mcfr.entities.mobs.gender.EntityGendered;

public enum GenderedEntityClasses {
  SIKER(EntitySiker.class),
  HOEN(EntityHoen.class),
  BORMOTH(EntityBormoth.class);
  
  private Class<? extends EntityGendered> entityClass;
  
  private GenderedEntityClasses(Class<? extends EntityGendered> entityClass) {
    this.entityClass = entityClass;
  }
  
  public Class<? extends EntityGendered> getEntityClass() {
    return this.entityClass;
  }
}
