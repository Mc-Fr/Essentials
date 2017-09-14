package net.mcfr.time.weather;

import static org.spongepowered.api.world.biome.BiomeTypes.*;

import java.util.Arrays;
import java.util.List;

import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.BiomeTypes;

public enum BiomeGenres {
  // #f:0
  PLAIN(0, 10, BEACH, FROZEN_RIVER, MUSHROOM_ISLAND, MUSHROOM_ISLAND_SHORE,
      PLAINS, RIVER, SWAMPLAND, SWAMPLAND_MOUNTAINS,
      SAVANNA, SAVANNA_MOUNTAINS, SAVANNA_PLATEAU, SAVANNA_PLATEAU_MOUNTAINS,
      STONE_BEACH, SUNFLOWER_PLAINS),
  FOREST(5, 8, BIRCH_FOREST, BIRCH_FOREST_HILLS, BIRCH_FOREST_HILLS_MOUNTAINS, BIRCH_FOREST_MOUNTAINS,
      FLOWER_FOREST, BiomeTypes.FOREST, FOREST_HILLS, JUNGLE,
      JUNGLE_EDGE, JUNGLE_EDGE_MOUNTAINS, JUNGLE_HILLS, JUNGLE_MOUNTAINS,
      ROOFED_FOREST, ROOFED_FOREST_MOUNTAINS),
  OCEAN(-3, 9, DEEP_OCEAN, FROZEN_OCEAN, SKY, VOID, HELL),
  DESERT(20, 15, BiomeTypes.DESERT, DESERT_HILLS, DESERT_MOUNTAINS, MESA,
      MESA_BRYCE, MESA_PLATEAU, MESA_PLATEAU_FOREST, MESA_PLATEAU_FOREST_MOUNTAINS,
      MESA_PLATEAU_MOUNTAINS),
  SNOWY(-7, 6, COLD_BEACH, COLD_TAIGA, COLD_TAIGA_HILLS, COLD_TAIGA_MOUNTAINS,
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
