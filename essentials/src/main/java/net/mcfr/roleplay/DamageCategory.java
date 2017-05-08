package net.mcfr.roleplay;

import java.util.HashMap;
import java.util.Map;

public enum DamageCategory {
  A,
  B,
  C;
  
  private Map<Integer, Integer> dies = new HashMap<>();
  private Map<Integer, Integer> bonus = new HashMap<>();
  
  static {
    A.dies.put(1, 0);
    A.dies.put(2, 0);
    A.dies.put(3, 0);
    A.dies.put(4, 0);
    A.dies.put(5, 1);
    A.dies.put(6, 1);
    A.dies.put(7, 1);
    A.dies.put(8, 1);
    A.dies.put(9, 1);
    A.dies.put(10, 1);
    A.dies.put(11, 1);
    A.dies.put(12, 1);
    A.dies.put(13, 1);
    A.dies.put(14, 1);
    A.dies.put(15, 1);
    A.dies.put(16, 1);
    A.dies.put(17, 1);
    A.dies.put(18, 1);
    A.dies.put(19, 2);
    A.dies.put(20, 2);
    
    A.bonus.put(1, 0);
    A.bonus.put(2, 0);
    A.bonus.put(3, 0);
    A.bonus.put(4, 0);
    A.bonus.put(5, -5);
    A.bonus.put(6, -4);
    A.bonus.put(7, -3);
    A.bonus.put(8, -3);
    A.bonus.put(9, -2);
    A.bonus.put(10, -2);
    A.bonus.put(11, -1);
    A.bonus.put(12, -1);
    A.bonus.put(13, 0);
    A.bonus.put(14, 0);
    A.bonus.put(15, 1);
    A.bonus.put(16, 1);
    A.bonus.put(17, 2);
    A.bonus.put(18, 2);
    A.bonus.put(19, -1);
    A.bonus.put(20, -1);
    
    B.dies.put(1, 0);
    B.dies.put(2, 0);
    B.dies.put(3, 0);
    B.dies.put(4, 0);
    B.dies.put(5, 1);
    B.dies.put(6, 1);
    B.dies.put(7, 1);
    B.dies.put(8, 1);
    B.dies.put(9, 1);
    B.dies.put(10, 1);
    B.dies.put(11, 1);
    B.dies.put(12, 1);
    B.dies.put(13, 2);
    B.dies.put(14, 2);
    B.dies.put(15, 2);
    B.dies.put(16, 2);
    B.dies.put(17, 3);
    B.dies.put(18, 3);
    B.dies.put(19, 3);
    B.dies.put(20, 3);
    
    B.bonus.put(1, 0);
    B.bonus.put(2, 0);
    B.bonus.put(3, 0);
    B.bonus.put(4, 0);
    B.bonus.put(5, -5);
    B.bonus.put(6, -4);
    B.bonus.put(7, -3);
    B.bonus.put(8, -2);
    B.bonus.put(9, -1);
    B.bonus.put(10, 0);
    B.bonus.put(11, 1);
    B.bonus.put(12, 2);
    B.bonus.put(13, -1);
    B.bonus.put(14, 0);
    B.bonus.put(15, 1);
    B.bonus.put(16, 2);
    B.bonus.put(17, -1);
    B.bonus.put(18, 0);
    B.bonus.put(19, 1);
    B.bonus.put(20, 2);
    
    C.dies.put(1, 0);
    C.dies.put(2, 0);
    C.dies.put(3, 0);
    C.dies.put(4, 0);
    C.dies.put(5, 1);
    C.dies.put(6, 1);
    C.dies.put(7, 1);
    C.dies.put(8, 1);
    C.dies.put(9, 1);
    C.dies.put(10, 1);
    C.dies.put(11, 1);
    C.dies.put(12, 1);
    C.dies.put(13, 2);
    C.dies.put(14, 2);
    C.dies.put(15, 2);
    C.dies.put(16, 2);
    C.dies.put(17, 3);
    C.dies.put(18, 3);
    C.dies.put(19, 3);
    C.dies.put(20, 3);
    
    C.bonus.put(1, 0);
    C.bonus.put(2, 0);
    C.bonus.put(3, 0);
    C.bonus.put(4, 0);
    C.bonus.put(5, -3);
    C.bonus.put(6, -2);
    C.bonus.put(7, -1);
    C.bonus.put(8, 0);
    C.bonus.put(9, 1);
    C.bonus.put(10, 2);
    C.bonus.put(11, 3);
    C.bonus.put(12, 4);
    C.bonus.put(13, 1);
    C.bonus.put(14, 2);
    C.bonus.put(15, 3);
    C.bonus.put(16, 4);
    C.bonus.put(17, 1);
    C.bonus.put(18, 2);
    C.bonus.put(19, 3);
    C.bonus.put(20, 4);
  }
  
  public int getDies(int strenght) {
    return this.dies.getOrDefault(strenght, 1);
  }
  
  public int getBonus(int strenght) {
    return this.bonus.getOrDefault(strenght, 0);
  }
}
