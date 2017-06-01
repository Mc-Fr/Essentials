package net.mcfr.harvest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import net.mcfr.dao.DaoFactory;
import net.mcfr.roleplay.Skill;
import net.mcfr.utils.McFrPlayer;

public class HarvestImp implements HarvestService {
  private final static int SKILL_HARVESTING_LEVEL = 12;

  private List<HarvestArea> harvestAreas;

  public HarvestImp() {
    this.harvestAreas = new ArrayList<>();
  }

  @Override
  public void loadFromDatabase() {
    this.harvestAreas = DaoFactory.getHarvestDao().getAll();
  }

  @Override
  public List<HarvestArea> getHarvestAreas() {
    return this.harvestAreas;
  }

  @Override
  public void addArea(String name, Location<World> loc, Skill skill) {
    HarvestArea newArea = new HarvestArea(name, loc, skill);
    DaoFactory.getHarvestDao().create(newArea);
    this.harvestAreas.add(newArea);
  }

  @Override
  public void removeArea(HarvestArea area) {
    DaoFactory.getHarvestDao().delete(area);
    this.harvestAreas.remove(area);
  }

  @Override
  public void addItemEntry(ItemStack item, HarvestArea area) {
    DaoFactory.getHarvestDao().addItemEntry(item, area);
    area.addItem(item);
  }

  @Override
  public void addRareItemEntry(ItemStack item, float probability, HarvestArea area) {
    RareItemEntry entry = new RareItemEntry(probability, item);
    DaoFactory.getHarvestDao().addRareItemEntry(entry, area);
    area.addRareItem(entry);
  }

  @Override
  public void removeItemEntry(ItemStack item, HarvestArea area) {
    DaoFactory.getHarvestDao().removeItemEntry(item, area);
    area.removeItem(item);
  }

  @Override
  public void removeRareItemEntry(ItemStack item, HarvestArea area) {
    DaoFactory.getHarvestDao().removeRareItemEntry(item, area);
    area.removeRareItem(item);
  }

  @Override
  public List<HarvestArea> getAreasForPlayer(McFrPlayer p) {
    List<HarvestArea> areas = new ArrayList<>();

    for (HarvestArea a : this.harvestAreas) {
      if (a.isInRange(p.getPlayer()) && p.getSkillLevel(a.getSkill(), Optional.empty()) >= SKILL_HARVESTING_LEVEL)
        areas.add(a);
    }

    return areas;
  }
  
  /**
   * Ne prend pas en compte les compétences de récolte.
   * @param p
   * @return La liste des {@code HarvestArea}s aux alentours.
   */
  @Override
  public List<HarvestArea> getAreasAround(McFrPlayer p) {
    List<HarvestArea> areas = new ArrayList<>();

    for (HarvestArea a : this.harvestAreas) {
      if (a.isInRange(p.getPlayer()))
        areas.add(a);
    }

    return areas;
  }

  @Override
  public void askForHarvest(McFrPlayer p, HarvestArea area) {
    //#f:0
    p.sendMessage(Text.join(Text.of(TextColors.GREEN, "Voulez-vous récolter : "),
        Text.of(TextColors.WHITE, area.getName()),
        Text.of(TextColors.GREEN, " ?")));
    p.sendMessage(Text.builder()
        .append(Text.of(TextColors.DARK_GREEN, ">> Confirmer <<"))
        .onClick(TextActions.executeCallback((cmdSrc) -> this.harvest(p, area)))
        .build());
    //f#1
  }
  
  @Override
  public void harvest(McFrPlayer p, HarvestArea area) {
    Optional<ItemStack> optItem = p.getPlayer().getItemInHand(HandTypes.MAIN_HAND);
    if (optItem.isPresent() && area.isToolCorrect(optItem.get().getItem())) {

      List<ItemStack> items = area.getHarvest();
      float tokenValue = p.getTokenValue();
      Inventory inventory = p.getPlayer().getInventory();
      
      for (ItemStack stack : items) {
        stack.setQuantity((int) Math.ceil(tokenValue * stack.getQuantity()));
        inventory.offer(stack);
      }
      
      //TODO : abîmer l'outil en fonction de sa classe
      
    } else {
      p.sendMessage(Text.of(TextColors.GREEN, "Vous devez avoir un outil correspondant à votre récolte en main."));
    }
    
  }
}
