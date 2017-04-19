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
    this.season = season;
    this.biome = biome;
  }
  
  public boolean isAccurate(BiomeGenres biomeIn, Seasons seasonIn, int hour, Wind wind) {
    return this.season.equals(seasonIn) && this.biome.equals(biomeIn);
  }
  
  @Override
  public String toString() {
    return this.message;
  }
}
