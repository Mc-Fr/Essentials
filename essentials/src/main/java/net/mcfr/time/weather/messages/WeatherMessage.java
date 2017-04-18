package net.mcfr.time.weather.messages;

import net.mcfr.time.weather.BiomeGenres;
import net.mcfr.time.weather.Seasons;
import net.mcfr.time.weather.wind.Wind;

public class WeatherMessage {
  private String message;
  private Seasons season;
  private BiomeGenres biome;
  
  public WeatherMessage(String message, Seasons season, BiomeGenres biome) {
    this.message = message;
    this.biome = biome;
  }
  
  public boolean isAcurate(BiomeGenres biome, Seasons season, int hour, Wind wind) {
    return this.season.equals(season) && this.biome.equals(biome);
  }
  
  @Override
  public String toString() {
    return this.message;
  }
}
