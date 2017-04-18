package net.mcfr.time.weather;

import static org.spongepowered.api.world.biome.BiomeTypes.BEACH;
import static org.spongepowered.api.world.biome.BiomeTypes.BIRCH_FOREST;
import static org.spongepowered.api.world.biome.BiomeTypes.BIRCH_FOREST_HILLS;
import static org.spongepowered.api.world.biome.BiomeTypes.BIRCH_FOREST_HILLS_MOUNTAINS;
import static org.spongepowered.api.world.biome.BiomeTypes.BIRCH_FOREST_MOUNTAINS;
import static org.spongepowered.api.world.biome.BiomeTypes.COLD_BEACH;
import static org.spongepowered.api.world.biome.BiomeTypes.COLD_TAIGA;
import static org.spongepowered.api.world.biome.BiomeTypes.COLD_TAIGA_HILLS;
import static org.spongepowered.api.world.biome.BiomeTypes.COLD_TAIGA_MOUNTAINS;
import static org.spongepowered.api.world.biome.BiomeTypes.DEEP_OCEAN;
import static org.spongepowered.api.world.biome.BiomeTypes.DESERT_HILLS;
import static org.spongepowered.api.world.biome.BiomeTypes.DESERT_MOUNTAINS;
import static org.spongepowered.api.world.biome.BiomeTypes.EXTREME_HILLS;
import static org.spongepowered.api.world.biome.BiomeTypes.EXTREME_HILLS_EDGE;
import static org.spongepowered.api.world.biome.BiomeTypes.EXTREME_HILLS_MOUNTAINS;
import static org.spongepowered.api.world.biome.BiomeTypes.EXTREME_HILLS_PLUS;
import static org.spongepowered.api.world.biome.BiomeTypes.EXTREME_HILLS_PLUS_MOUNTAINS;
import static org.spongepowered.api.world.biome.BiomeTypes.FLOWER_FOREST;
import static org.spongepowered.api.world.biome.BiomeTypes.FOREST_HILLS;
import static org.spongepowered.api.world.biome.BiomeTypes.FROZEN_OCEAN;
import static org.spongepowered.api.world.biome.BiomeTypes.FROZEN_RIVER;
import static org.spongepowered.api.world.biome.BiomeTypes.HELL;
import static org.spongepowered.api.world.biome.BiomeTypes.ICE_MOUNTAINS;
import static org.spongepowered.api.world.biome.BiomeTypes.ICE_PLAINS;
import static org.spongepowered.api.world.biome.BiomeTypes.ICE_PLAINS_SPIKES;
import static org.spongepowered.api.world.biome.BiomeTypes.JUNGLE;
import static org.spongepowered.api.world.biome.BiomeTypes.JUNGLE_EDGE;
import static org.spongepowered.api.world.biome.BiomeTypes.JUNGLE_EDGE_MOUNTAINS;
import static org.spongepowered.api.world.biome.BiomeTypes.JUNGLE_HILLS;
import static org.spongepowered.api.world.biome.BiomeTypes.JUNGLE_MOUNTAINS;
import static org.spongepowered.api.world.biome.BiomeTypes.MEGA_SPRUCE_TAIGA;
import static org.spongepowered.api.world.biome.BiomeTypes.MEGA_SPRUCE_TAIGA_HILLS;
import static org.spongepowered.api.world.biome.BiomeTypes.MEGA_TAIGA;
import static org.spongepowered.api.world.biome.BiomeTypes.MEGA_TAIGA_HILLS;
import static org.spongepowered.api.world.biome.BiomeTypes.MESA;
import static org.spongepowered.api.world.biome.BiomeTypes.MESA_BRYCE;
import static org.spongepowered.api.world.biome.BiomeTypes.MESA_PLATEAU;
import static org.spongepowered.api.world.biome.BiomeTypes.MESA_PLATEAU_FOREST;
import static org.spongepowered.api.world.biome.BiomeTypes.MESA_PLATEAU_FOREST_MOUNTAINS;
import static org.spongepowered.api.world.biome.BiomeTypes.MESA_PLATEAU_MOUNTAINS;
import static org.spongepowered.api.world.biome.BiomeTypes.MUSHROOM_ISLAND;
import static org.spongepowered.api.world.biome.BiomeTypes.MUSHROOM_ISLAND_SHORE;
import static org.spongepowered.api.world.biome.BiomeTypes.PLAINS;
import static org.spongepowered.api.world.biome.BiomeTypes.RIVER;
import static org.spongepowered.api.world.biome.BiomeTypes.ROOFED_FOREST;
import static org.spongepowered.api.world.biome.BiomeTypes.ROOFED_FOREST_MOUNTAINS;
import static org.spongepowered.api.world.biome.BiomeTypes.SAVANNA;
import static org.spongepowered.api.world.biome.BiomeTypes.SAVANNA_MOUNTAINS;
import static org.spongepowered.api.world.biome.BiomeTypes.SAVANNA_PLATEAU;
import static org.spongepowered.api.world.biome.BiomeTypes.SAVANNA_PLATEAU_MOUNTAINS;
import static org.spongepowered.api.world.biome.BiomeTypes.SKY;
import static org.spongepowered.api.world.biome.BiomeTypes.STONE_BEACH;
import static org.spongepowered.api.world.biome.BiomeTypes.SUNFLOWER_PLAINS;
import static org.spongepowered.api.world.biome.BiomeTypes.SWAMPLAND;
import static org.spongepowered.api.world.biome.BiomeTypes.SWAMPLAND_MOUNTAINS;
import static org.spongepowered.api.world.biome.BiomeTypes.TAIGA;
import static org.spongepowered.api.world.biome.BiomeTypes.TAIGA_HILLS;
import static org.spongepowered.api.world.biome.BiomeTypes.TAIGA_MOUNTAINS;
import static org.spongepowered.api.world.biome.BiomeTypes.VOID;

import java.util.Arrays;
import java.util.List;

import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.BiomeTypes;

public enum BiomeGenres {
  // #f:0
  PLAIN(0, 5, BEACH, FROZEN_RIVER, MUSHROOM_ISLAND, MUSHROOM_ISLAND_SHORE,
      PLAINS, RIVER, SWAMPLAND, SWAMPLAND_MOUNTAINS,
      SAVANNA, SAVANNA_MOUNTAINS, SAVANNA_PLATEAU, SAVANNA_PLATEAU_MOUNTAINS,
      STONE_BEACH, SUNFLOWER_PLAINS),
  FOREST(5, 5, BIRCH_FOREST, BIRCH_FOREST_HILLS, BIRCH_FOREST_HILLS_MOUNTAINS, BIRCH_FOREST_MOUNTAINS,
      FLOWER_FOREST, BiomeTypes.FOREST, FOREST_HILLS, JUNGLE,
      JUNGLE_EDGE, JUNGLE_EDGE_MOUNTAINS, JUNGLE_HILLS, JUNGLE_MOUNTAINS,
      ROOFED_FOREST, ROOFED_FOREST_MOUNTAINS),
  OCEAN(-3, 5, DEEP_OCEAN, FROZEN_OCEAN, SKY, VOID, HELL),
  DESERT(20, 10, BiomeTypes.DESERT, DESERT_HILLS, DESERT_MOUNTAINS, MESA,
      MESA_BRYCE, MESA_PLATEAU, MESA_PLATEAU_FOREST, MESA_PLATEAU_FOREST_MOUNTAINS,
      MESA_PLATEAU_MOUNTAINS),
  SNOWY(-7, 3, COLD_BEACH, COLD_TAIGA, COLD_TAIGA_HILLS, COLD_TAIGA_MOUNTAINS,
      EXTREME_HILLS, EXTREME_HILLS_EDGE, EXTREME_HILLS_MOUNTAINS, EXTREME_HILLS_PLUS,
      EXTREME_HILLS_PLUS_MOUNTAINS, ICE_MOUNTAINS, ICE_PLAINS, ICE_PLAINS_SPIKES,
      MEGA_TAIGA, MEGA_TAIGA_HILLS, MEGA_SPRUCE_TAIGA, MEGA_SPRUCE_TAIGA_HILLS,
      TAIGA, TAIGA_HILLS, TAIGA_MOUNTAINS);
  // #f:1

  private List<BiomeType> biomes;
  private int temperatureBonus;
  private int temperatureVariation;

  private BiomeGenres(int tempBonus, int tempVar, BiomeType... biomes) {
    this.temperatureBonus = tempBonus;
    this.temperatureVariation = tempVar;

    this.biomes = Arrays.asList(biomes);
  }

  public int getTemperatureModificator(int hour) {
    return this.temperatureBonus + (int) Math.floor(1f * this.temperatureVariation * Math.sin(2 * Math.PI * (hour - 9) / 24f));
  }

  public static BiomeGenres getGenreByBiome(BiomeType biome) {
    return Arrays.stream(values()).filter(g -> g.biomes.contains(biome)).findFirst().orElse(SNOWY);
  }
}
