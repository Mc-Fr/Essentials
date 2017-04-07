package net.mcfr.time.weather.messages;

import net.mcfr.time.weather.BiomeGenres;
import net.mcfr.time.weather.Seasons;
import net.mcfr.time.weather.wind.Wind;
import net.mcfr.time.weather.wind.WindLevels;

public class WindDependantMessage extends WeatherMessage {
  private WindLevels minLevel;
  private WindLevels maxLevel;
  private int minOrientation;
  private int maxOrientation;

  public WindDependantMessage(String message, Seasons season, WindLevels minLevel, WindLevels maxLevel, int minOrient, int maxOrient) {
    super(message, season);
    this.minLevel = minLevel;
    this.maxLevel = maxLevel;
    this.minOrientation = minOrient;
    this.maxOrientation = maxOrient;
  }
  
  public WindDependantMessage(String message, Seasons season, WindLevels minLevel, WindLevels maxLevel) {
    this(message, season, minLevel, maxLevel, 0, 379);
  }
  
  public WindDependantMessage(String message, Seasons season, WindLevels minLevel) {
    this(message, season, minLevel, WindLevels.MAD);
  }
  
  public WindDependantMessage(String message, Seasons season, WindLevels minLevel, int minOrient, int maxOrient) {
    this(message, season, minLevel, WindLevels.MAD, minOrient, maxOrient);
  }
  
  public WindDependantMessage(String message, Seasons season, int minOrient, int maxOrient) {
    this(message, season, WindLevels.NONE, minOrient, maxOrient);
  }
  
  @Override
  public boolean isAcurate(BiomeGenres biome, Seasons season, int hour, Wind wind) {
    if (this.minLevel.ordinal() > wind.getLevel().ordinal() || this.maxLevel.ordinal() < wind.getLevel().ordinal())
      return false;
    
    if (this.minOrientation < this.maxOrientation)
      if (wind.getOrientation() >= this.minOrientation && wind.getOrientation() <= this.maxOrientation)
        return super.isAcurate(biome, season, hour, wind);
    else
      if ((wind.getOrientation() >= this.minOrientation || wind.getOrientation() <= this.maxOrientation))
        return super.isAcurate(biome, season, hour, wind);
    
    return false;
  }
}
