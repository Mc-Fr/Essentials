package net.mcfr.time.weather;

public enum HumidityLevels {
  SUNNY(14),
  CLOUDY(7),
  THIN_RAIN(0),
  HEAVY_RAIN(-7),
  STORM(-14);

  private int temperatureModificator;

  private HumidityLevels(int temperatureModificator) {
    this.temperatureModificator = temperatureModificator;
  }
  
  public int getTemperatureModificator() {
    return this.temperatureModificator;
  }
}
