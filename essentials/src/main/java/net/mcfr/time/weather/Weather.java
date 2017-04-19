package net.mcfr.time.weather;

import java.util.Random;
import java.util.StringJoiner;

import org.spongepowered.api.world.biome.BiomeType;

import net.mcfr.time.McFrDate;
import net.mcfr.time.weather.humidity.HumidityLevels;
import net.mcfr.time.weather.humidity.HumidityState;
import net.mcfr.time.weather.wind.Wind;

public class Weather {
  private Wind wind;
  private Seasons season;
  private HumidityState humidityState;
  private int temperature;
  private McFrDate lastUpdate;
  private Random rand;

  public Weather(McFrDate date) {
    this.lastUpdate = new McFrDate(date);
    this.rand = new Random(date.getSeed());

    this.season = date.getSeason();

    this.wind = new Wind();
    this.humidityState = this.season.getState(HumidityLevels.SUNNY);
    this.temperature = 0;
  }

  public void updateWeather(McFrDate date) {
    this.lastUpdate = new McFrDate(date);
    this.rand = new Random(date.getSeed());

    this.wind.updateIntensity(this.rand);
    this.wind.updateOrientation(this.rand);

    Seasons currentSeason = date.getSeason();
    if (!this.season.equals(currentSeason)) {
      this.season = currentSeason;
      this.humidityState = this.season.getState(HumidityLevels.SUNNY);
    } else {
      this.humidityState = this.humidityState.next(this.rand);
    }

    this.temperature = this.season.getTemperatureModificator() + this.humidityState.getLevel().getTemperatureModificator() + this.rand.nextInt(11)
        - 5;
  }

  public String getWeatherString(BiomeType biome, int altitude, McFrDate date) {
    BiomeGenres biomeGenre = BiomeGenres.getGenreByBiome(biome);
    int hour = date.getHour();

    int localTemperature = this.temperature - (altitude - 80) / 4 + biomeGenre.getTemperatureModificator(hour);

    StringJoiner result = new StringJoiner(" * ");
    result.add(this.humidityState.getLevel().name()); //TODO : remove
    result.add(this.humidityState.getLevel().getWeatherString(biomeGenre, this.season, hour, this.wind, new Random(this.lastUpdate.getSeed())));
    result.add(this.wind.getWindString(altitude));
    result.add(localTemperature + "°C");
    return "* " + result + " *";
  }

  public boolean mustUpdate(McFrDate current) {
    return this.lastUpdate.isWeatherDelayPassed(current);
  }
}
