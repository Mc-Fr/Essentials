package net.mcfr.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.GameRegistry;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import net.mcfr.harvest.HarvestArea;
import net.mcfr.harvest.RareItemEntry;
import net.mcfr.roleplay.Skill;
import net.mcfr.utils.McFrConnection;

public class HarvestDao implements Dao<HarvestArea> {
  private static GameRegistry registry = Sponge.getGame().getRegistry();

  @Override
  public List<HarvestArea> getAll() {
    List<HarvestArea> areas = new ArrayList<>();
    try (Connection connection = McFrConnection.getConnection()) {
      ResultSet rs = connection.createStatement().executeQuery("select name, displayName, world, x, y, z, skill, tool, toolDamage from srv_harvestareas");
      List<String> unknownWorlds = new ArrayList<>();
      
      while (rs.next()) {
        
        String worldName = rs.getString("world");
        
        if (unknownWorlds.contains(worldName))
          continue;
        
        Optional<World> optWorld = Sponge.getServer().getWorld(worldName);
        
        if (!optWorld.isPresent()) {
          System.out.println("Le monde " + rs.getString("world") + " n'existe pas.");
          unknownWorlds.add(worldName);
          Sponge.getServer().getWorlds().forEach(w -> System.out.println(w.getName()));
          continue;
        }
        
        String name = rs.getString("name");
        String displayName = rs.getString("displayName");
        Location<World> loc = new Location<>(optWorld.get(), rs.getInt("x"), rs.getInt("y"), rs.getInt("z"));
        Skill skill = Skill.getSkillByName(rs.getString("skill"));
        HarvestTools tool = HarvestTools.valueOf(rs.getString("tool"));
        int toolDamage = rs.getInt("toolDamage");
        
        HarvestArea area = new HarvestArea(name, displayName, loc, skill, tool, toolDamage);
        areas.add(area);

        ResultSet itemEntries = connection.createStatement()
            .executeQuery("select itemType, metaData, quantity from srv_harvest_itementry where harvestArea = \"" + area.getName() + "\"");
        while (itemEntries.next()) {
          Optional<ItemType> type = registry.getType(ItemType.class, itemEntries.getString("itemType"));

          if (type.isPresent()) {
            // ItemType & Quantity
            ItemStack item = ItemStack.builder().itemType(type.get()).quantity(itemEntries.getInt("quantity")).build();
            
            // Metadata
            DataView dataView = item.toContainer().set(DataQuery.of("UnsafeDamage"), itemEntries.getInt("metaData"));
            item = ItemStack.builder().fromContainer(dataView).build();

            // HarvestArea
            area.addItem(item);
          } else {
            System.out.println("ItemType non reconnu : " + itemEntries.getString("itemType"));
          }
        }
        itemEntries.close();

        ResultSet rareItemEntries = connection.createStatement()
            .executeQuery("select itemType, metaData, quantity, name, description, probability from srv_harvest_rareitementry where harvestArea = \""
                + area.getName() + "\"");
        
        while (rareItemEntries.next()) {
          Optional<ItemType> type = registry.getType(ItemType.class, rareItemEntries.getString("itemType"));

          if (type.isPresent()) {
            // ItemType & Quantity
            ItemStack item = ItemStack.builder().itemType(type.get()).quantity(rareItemEntries.getInt("quantity")).build();
            
            // Metadata
            DataView dataView = item.toContainer().set(DataQuery.of("UnsafeDamage"), rareItemEntries.getInt("metaData"));
            item = ItemStack.builder().fromContainer(dataView).build();

            // Name
            if (!rareItemEntries.getString("name").equals(""))
              item.offer(Keys.DISPLAY_NAME, Text.of(rareItemEntries.getString("name")));

            // Description
            List<Text> list = new ArrayList<>();
            list.add(Text.of(rareItemEntries.getString("description")));
            item.offer(Keys.ITEM_LORE, list);

            // Probability & HarvestArea
            area.addRareItem(new RareItemEntry(rareItemEntries.getFloat("probability"), item));
          } else {
            System.out.println("ItemType non reconnu : " + rareItemEntries.getString("itemType"));
          }
        }
        rareItemEntries.close();
      }
      rs.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return areas;
  }

  @Override
  public boolean create(HarvestArea o) {
    try (Connection connection = McFrConnection.getConnection()) {
      CallableStatement cs = connection.prepareCall("{ call addHarvestArea(?, ?, ?, ?, ?, ?, ?, ?, ?) }");
      cs.setString(1, o.getName());
      Location<World> loc = o.getLocation();
      cs.setString(2, loc.getExtent().getName());
      cs.setInt(3, loc.getBlockX());
      cs.setInt(4, loc.getBlockY());
      cs.setInt(5, loc.getBlockZ());
      cs.setString(6, o.getSkill().getName());
      cs.setString(7, o.getTool().name());
      cs.setInt(8, o.getToolDamage());
      cs.setString(9, o.getDisplayName());
      cs.execute();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public boolean delete(HarvestArea o) {
    try (Connection connection = McFrConnection.getConnection()) {
      CallableStatement cs = connection.prepareCall("{ call removeHarvestArea(?) }");
      cs.setString(1, o.getName());
      cs.execute();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }
  
  public boolean addItemEntry(ItemStack item, HarvestArea area) {
    try (Connection connection = McFrConnection.getConnection()) {
      CallableStatement cs = connection.prepareCall("{ call addHarvestItemEntry(?, ?, ?, ?) }");
      cs.setString(1, item.getItem().getId());
      cs.setInt(2, (int) item.toContainer().get(DataQuery.of("UnsafeDamage")).orElse(0));
      cs.setInt(3, item.getQuantity());
      cs.setString(4, area.getName());
      cs.execute();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }
  
  public boolean addRareItemEntry(RareItemEntry entry, HarvestArea area) {
    try (Connection connection = McFrConnection.getConnection()) {
      List<Text> emptyDescription = new ArrayList<>();
      emptyDescription.add(Text.of(""));
      ItemStack item = entry.getItemStack();
      
      CallableStatement cs = connection.prepareCall("{ call addHarvestRareItemEntry(?, ?, ?, ?, ?, ?, ?) }");
      cs.setString(1, item.getItem().getId());
      cs.setInt(2, (int) item.toContainer().get(DataQuery.of("UnsafeDamage")).orElse(0));
      cs.setInt(3, item.getQuantity());
      cs.setString(4, item.get(Keys.DISPLAY_NAME).orElse(Text.of("")).toPlain());
      cs.setString(5, item.get(Keys.ITEM_LORE).orElse(emptyDescription).get(0).toPlain());
      cs.setDouble(6, entry.getProbability());
      cs.setString(7, area.getName());
      cs.execute();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }
  
  public boolean removeItemEntry(ItemStack item, HarvestArea area) {
    try (Connection connection = McFrConnection.getConnection()) {
      CallableStatement cs = connection.prepareCall("{ call removeHarvestItemEntry(?, ?, ?) }");
      cs.setString(1, item.getItem().getId());
      cs.setInt(2, (int) item.toContainer().get(DataQuery.of("UnsafeDamage")).orElse(0));
      cs.setString(3, area.getName());
      cs.execute();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }
  
  public boolean removeRareItemEntry(ItemStack item, HarvestArea area) {
    try (Connection connection = McFrConnection.getConnection()) {
      CallableStatement cs = connection.prepareCall("{ call removeHarvestRareItemEntry(?, ?, ?, ?) }");
      cs.setString(1, item.getItem().getId());
      cs.setInt(2, (int) item.toContainer().get(DataQuery.of("UnsafeDamage")).orElse(0));
      cs.setString(3, item.get(Keys.DISPLAY_NAME).orElse(Text.of("")).toPlain());
      cs.setString(4, area.getName());
      cs.execute();
      return true;
    } catch (SQLException e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  @Deprecated
  public boolean update(HarvestArea o) {
    return false;
  }

}
