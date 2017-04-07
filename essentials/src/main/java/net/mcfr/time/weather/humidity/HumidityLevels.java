package net.mcfr.time.weather.humidity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.mcfr.time.weather.BiomeGenres;
import net.mcfr.time.weather.Seasons;
import net.mcfr.time.weather.messages.HouredMessage;
import net.mcfr.time.weather.messages.WeatherMessage;
import net.mcfr.time.weather.wind.Wind;

public enum HumidityLevels {
  SUNNY(14),
  CLOUDY(7),
  THIN_RAIN(0),
  HEAVY_RAIN(-7),
  STORM(-14);

  static {
    SUNNY.addMessage(new WeatherMessage("Test", Seasons.FALL));
    SUNNY.addMessage(new HouredMessage("Test la nuit", Seasons.WINTER, 19, 5));
  }
  
  private int temperatureModificator;
  private List<WeatherMessage> messages;

  private HumidityLevels(int temperatureModificator) {
    this.temperatureModificator = temperatureModificator;
    this.messages = new ArrayList<>();
  }
  
  private void addMessage(WeatherMessage message) {
    this.messages.add(message);
  }
  
  public int getTemperatureModificator() {
    return this.temperatureModificator;
  }
  
  public String getWeatherString(BiomeGenres biomeGenre, Seasons season, int hour, Wind wind, Random rand) {
    WeatherMessage[] availableMessages = this.messages.stream().filter(m -> m.isAcurate(biomeGenre, season, hour, wind)).toArray(WeatherMessage[]::new);
    if (availableMessages.length > 0) {
      return availableMessages[rand.nextInt(availableMessages.length)].toString();
    }
    return "";
  }
}
