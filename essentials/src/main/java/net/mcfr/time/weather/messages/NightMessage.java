package net.mcfr.time.weather.messages;

import net.mcfr.time.weather.BiomeGenres;
import net.mcfr.time.weather.Seasons;

public class NightMessage extends HouredMessage {
  
  public NightMessage(String message, Seasons season, BiomeGenres biome) {
    super(message, season, biome, 18, 5);
  }
}
