package net.mcfr.time.weather;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.BiomeTypes;

public enum BiomeGenres {
  // #f:0
  PLAIN(0, 5, BiomeTypes.BEACH, BiomeTypes.FROZEN_RIVER, BiomeTypes.MUSHROOM_ISLAND, BiomeTypes.MUSHROOM_ISLAND_SHORE,
      BiomeTypes.PLAINS, BiomeTypes.RIVER, BiomeTypes.SWAMPLAND, BiomeTypes.SWAMPLAND_MOUNTAINS,
      BiomeTypes.SAVANNA, BiomeTypes.SAVANNA_MOUNTAINS, BiomeTypes.SAVANNA_PLATEAU, BiomeTypes.SAVANNA_PLATEAU_MOUNTAINS,
      BiomeTypes.STONE_BEACH, BiomeTypes.SUNFLOWER_PLAINS),
  FOREST(5, 5, BiomeTypes.BIRCH_FOREST, BiomeTypes.BIRCH_FOREST_HILLS, BiomeTypes.BIRCH_FOREST_HILLS_MOUNTAINS, BiomeTypes.BIRCH_FOREST_MOUNTAINS,
      BiomeTypes.FLOWER_FOREST, BiomeTypes.FOREST, BiomeTypes.FOREST_HILLS, BiomeTypes.JUNGLE,
      BiomeTypes.JUNGLE_EDGE, BiomeTypes.JUNGLE_EDGE_MOUNTAINS, BiomeTypes.JUNGLE_HILLS, BiomeTypes.JUNGLE_MOUNTAINS,
      BiomeTypes.ROOFED_FOREST, BiomeTypes.ROOFED_FOREST_MOUNTAINS),
  OCEAN(-3, 5, BiomeTypes.DEEP_OCEAN, BiomeTypes.FROZEN_OCEAN, BiomeTypes.SKY, BiomeTypes.VOID, BiomeTypes.HELL),
  DESERT(20, 10, BiomeTypes.DESERT, BiomeTypes.DESERT_HILLS, BiomeTypes.DESERT_MOUNTAINS, BiomeTypes.MESA,
      BiomeTypes.MESA_BRYCE, BiomeTypes.MESA_PLATEAU, BiomeTypes.MESA_PLATEAU_FOREST, BiomeTypes.MESA_PLATEAU_FOREST_MOUNTAINS,
      BiomeTypes.MESA_PLATEAU_MOUNTAINS),
  SNOWY(-7, 3, BiomeTypes.COLD_BEACH, BiomeTypes.COLD_TAIGA, BiomeTypes.COLD_TAIGA_HILLS, BiomeTypes.COLD_TAIGA_MOUNTAINS,
      BiomeTypes.EXTREME_HILLS, BiomeTypes.EXTREME_HILLS_EDGE, BiomeTypes.EXTREME_HILLS_MOUNTAINS, BiomeTypes.EXTREME_HILLS_PLUS,
      BiomeTypes.EXTREME_HILLS_PLUS_MOUNTAINS, BiomeTypes.ICE_MOUNTAINS, BiomeTypes.ICE_PLAINS, BiomeTypes.ICE_PLAINS_SPIKES,
      BiomeTypes.MEGA_TAIGA, BiomeTypes.MEGA_TAIGA_HILLS, BiomeTypes.MEGA_SPRUCE_TAIGA, BiomeTypes.MEGA_SPRUCE_TAIGA_HILLS,
      BiomeTypes.TAIGA, BiomeTypes.TAIGA_HILLS, BiomeTypes.TAIGA_MOUNTAINS);
  // #f:1

  private List<BiomeType> biomes;
  private int temperatureBonus;
  private int temperatureVariation;

  private BiomeGenres(int tempBonus, int tempVar, BiomeType... biomes) {
    this.temperatureBonus = tempBonus;
    this.temperatureVariation = tempVar;
    
    this.biomes = new ArrayList<>();

    for (BiomeType biome : biomes) {
      this.biomes.add(biome);
    }
  }
  
  public int getTemperatureModificator(int hour) {
    return this.temperatureBonus + (int) Math.floor(1f * this.temperatureVariation * Math.sin(2*Math.PI*(hour - 9)/24f));
  }
  
  public static BiomeGenres getGenreByBiome(BiomeType biome) {
    for (BiomeGenres genre : BiomeGenres.values()) {
      if (genre.biomes.contains(biome)) {
        return genre;
      }
    }
    return SNOWY;
  }
}
