package net.mcfr.time.weather.messages;

import net.mcfr.time.weather.BiomeGenres;
import net.mcfr.time.weather.Seasons;

public class DayMessage extends HouredMessage {

  public DayMessage(String message, Seasons season, BiomeGenres biome) {
    super(message, season, biome, 6, 17);
  }

}
