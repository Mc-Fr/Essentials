package net.mcfr.harvest;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.GameRegistry;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;

public enum HarvestTools {
  PICKAXE("Pioche"),
  AXE("Hâche"),
  SHOVEL("Pelle"),
  FISHING_ROD("Canne à pêche"),
  FISHING_NET("Filet de pêche"),
  BOW("Arc"),
  HOE("Faux"),
  SHEARS("Cisailles"),
  NO_TOOL("Pas d'outil");
  
  private List<ItemType> toolItems;
  private String display;
  
  static {
    GameRegistry registry = Sponge.getGame().getRegistry();
    
    AXE.toolItems.add(ItemTypes.DIAMOND_AXE);
    AXE.toolItems.add(ItemTypes.IRON_AXE);
    
    SHOVEL.toolItems.add(ItemTypes.DIAMOND_SHOVEL);
    SHOVEL.toolItems.add(ItemTypes.IRON_SHOVEL);

    PICKAXE.toolItems.add(ItemTypes.DIAMOND_PICKAXE);
    PICKAXE.toolItems.add(ItemTypes.IRON_PICKAXE);

    FISHING_ROD.toolItems.add(ItemTypes.FISHING_ROD);
    FISHING_ROD.toolItems.add(registry.getType(ItemType.class, "mcfr_b_i:good_fishing_rod").get());
    
    FISHING_NET.toolItems.add(registry.getType(ItemType.class, "mcfr_b_i:fishing_net").get());

    BOW.toolItems.add(ItemTypes.BOW);
    BOW.toolItems.add(registry.getType(ItemType.class, "mcfr_b_i:iron_bow").get());
    BOW.toolItems.add(registry.getType(ItemType.class, "mcfr_b_i:golden_bow").get());
    BOW.toolItems.add(registry.getType(ItemType.class, "mcfr_b_i:steel_bow").get());
    BOW.toolItems.add(registry.getType(ItemType.class, "mcfr_b_i:barbarian_bow").get());
    BOW.toolItems.add(registry.getType(ItemType.class, "mcfr_b_i:long_bow").get());
    BOW.toolItems.add(registry.getType(ItemType.class, "mcfr_b_i:hunter_bow").get());
    BOW.toolItems.add(registry.getType(ItemType.class, "mcfr_b_i:long_hunter_bow").get());
    BOW.toolItems.add(registry.getType(ItemType.class, "mcfr_b_i:ancient_bow").get());

    HOE.toolItems.add(ItemTypes.DIAMOND_HOE);
    HOE.toolItems.add(ItemTypes.IRON_HOE);

    SHEARS.toolItems.add(ItemTypes.SHEARS);
  }
  
  private HarvestTools(String display) {
    this.toolItems = new ArrayList<>();
    this.display = display;
  }
  
  public String getDisplay() {
    return this.display;
  }
  
  public boolean isToolCorrect(ItemType tool) {
    if (this == NO_TOOL)
      return true;
    for (ItemType i : this.toolItems)
      if (i.equals(tool))
        return true;
    return false;
  }
}
