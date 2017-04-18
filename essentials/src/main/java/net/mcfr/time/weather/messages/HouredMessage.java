package net.mcfr.time.weather.messages;

import net.mcfr.time.weather.BiomeGenres;
import net.mcfr.time.weather.Seasons;
import net.mcfr.time.weather.wind.Wind;

public class HouredMessage extends WeatherMessage {
  private int minHour;
  private int maxHour;

  public HouredMessage(String message, Seasons season, BiomeGenres biome, int minHour, int maxHour) {
    super(message, season, biome);
    this.minHour = minHour;
    this.maxHour = maxHour;
  }
  
  @Override
  public boolean isAccurate(BiomeGenres biome, Seasons season, int hour, Wind wind) {
    if (hour >= this.minHour && hour <= this.maxHour) {
      return super.isAccurate(biome, season, hour, wind);
    }
    return false;
  }
}
