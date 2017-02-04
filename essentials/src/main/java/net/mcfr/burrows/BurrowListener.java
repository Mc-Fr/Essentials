package net.mcfr.burrows;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.world.chunk.LoadChunkEvent;
import org.spongepowered.api.event.world.chunk.UnloadChunkEvent;
import org.spongepowered.api.world.Chunk;

import com.flowpowered.math.vector.Vector3i;

import net.mcfr.entities.mobs.EntityBurrowed;
import net.mcfr.utils.McFrConnection;

public class BurrowListener {
  private final static PreparedStatement saveQuery = McFrConnection.getServerConnection()
      .prepare("INSERT INTO `BurrowChunks`(`burrowId`, `x`, `y`, `z`) VALUES (?, ?, ?, ?)");

  private static Map<Integer, List<Vector3i>> chunks = new HashMap<>();

  @Listener(order = Order.FIRST)
  public void onUnloadChunk(UnloadChunkEvent e) {
    e.getTargetChunk().getEntities().stream().filter(en -> en instanceof EntityBurrowed).forEach(en -> {
      addOnList(((EntityBurrowed) en).getBurrow(), e.getTargetChunk());
    });
  }

  private static void addOnList(int burrowId, Chunk chunk) {
    if (chunk != null) {
      if (!chunks.containsKey(burrowId)) {
        chunks.put(burrowId, new ArrayList<>());
      }
      List<Vector3i> list = chunks.get(burrowId);
      if (!list.contains(chunk.getPosition())) {
        list.add(chunk.getPosition());
      }
    }
  }

  @Listener(order = Order.LAST)
  public void onLoadChunk(LoadChunkEvent e) {
    e.getTargetChunk().getEntities().stream().filter(en -> en instanceof EntityBurrowed).forEach(en -> {
      chunks.values().forEach(l -> l.remove(e.getTargetChunk().getPosition()));
    });
  }

  public static void loadAllOccupiedChunks(BurrowPopulation burrowPopulation) {
    if (chunks.containsKey(burrowPopulation.getId())) {
      List<Vector3i> toLoad = new ArrayList<>();
      chunks.get(burrowPopulation.getId()).forEach(p -> toLoad.add(p));
      toLoad.forEach(p -> burrowPopulation.getWorld().loadChunk(p, true));
    }
  }

  public static void saveInDatabase() {
    try {
      McFrConnection.getServerConnection().prepare("DELETE FROM BurrowChunks WHERE 1").execute();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    chunks.forEach((b, l) -> {
      Optional<Burrow> burrow = Burrow.getBurrowById(b);
      if (burrow.isPresent() && Burrow.isBurrowAlive(burrow.get())) {
        l.forEach(p -> {
          try {
            saveQuery.setInt(1, b);
            saveQuery.setInt(2, p.getX());
            saveQuery.setInt(3, p.getY());
            saveQuery.setInt(4, p.getZ());
            saveQuery.execute();
          } catch (SQLException e) {
            e.printStackTrace();
          }
        });
      }
    });
  }

  public static void loadFromDatabase() {
    try {
      ResultSet chunkData = McFrConnection.getServerConnection().executeQuery("SELECT `burrowId`, `x`, `y`, `z` FROM `BurrowChunks` WHERE 1");

      while (chunkData.next()) {
        Vector3i position = new Vector3i(chunkData.getInt(2), chunkData.getInt(3), chunkData.getInt(4));
        addOnList(chunkData.getInt(1), Sponge.getServer().getWorld("world").get().loadChunk(position, true).orElse(null));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
