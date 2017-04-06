package net.mcfr.time.weather;

import java.util.Random;

import org.spongepowered.api.world.biome.BiomeTypes;

public class Weather {
  private static final long seed = 123456789;
  public static final Random rand = new Random(seed);
  
  private Wind wind;
  private Seasons season;
  private HumidityState humidityState;
  private int temperature;
  
  public Weather() {
    this.wind = new Wind();
    this.season = Seasons.SPRING;
    this.humidityState = this.season.getState(HumidityLevels.SUNNY);
    this.temperature = 0;
  }
  
  public void updateWeather() {
    this.wind.updateIntensity();
    this.wind.updateOrientation();
    this.humidityState = this.humidityState.next();
    
    this.temperature = this.season.getTemperatureModificator() + this.humidityState.getLevel().getTemperatureModificator() + rand.nextInt(11) - 5;
  }
  
  public String getWeatherString(BiomeTypes biome, int altitude) {
    String result = "* ";
    result += "Message"; //TODO
    result += " * ";
    result += this.wind.getWindString(altitude);
    result += " * ";
    result += this.temperature + "°C";
    result += " *";
    return result;
  }
}
