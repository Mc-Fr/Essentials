package net.mcfr.burrows;

import net.mcfr.entities.mobs.entity.EntityBormoth;
import net.mcfr.entities.mobs.entity.EntityGalt;
import net.mcfr.entities.mobs.entity.EntityHoen;
import net.mcfr.entities.mobs.entity.EntityNiale;
import net.mcfr.entities.mobs.entity.EntitySiker;
import net.mcfr.entities.mobs.gender.EntityGendered;

/**
 * Classes d'entités utilisables par le système des terriers
 */
public enum BurrowedEntityClasse {
  SIKER(EntitySiker.class),
  HOEN(EntityHoen.class),
  BORMOTH(EntityBormoth.class),
  GALT(EntityGalt.class),
  NIALE(EntityNiale.class);
  
  private Class<? extends EntityGendered> entityClass;
  
  private BurrowedEntityClasse(Class<? extends EntityGendered> entityClass) {
    this.entityClass = entityClass;
  }
  
  public Class<? extends EntityGendered> getEntityClass() {
    return this.entityClass;
  }
}
