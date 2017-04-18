package net.mcfr.time.weather;

import static net.mcfr.time.weather.humidity.HumidityLevels.CLOUDY;
import static net.mcfr.time.weather.humidity.HumidityLevels.HEAVY_RAIN;
import static net.mcfr.time.weather.humidity.HumidityLevels.STORM;
import static net.mcfr.time.weather.humidity.HumidityLevels.SUNNY;
import static net.mcfr.time.weather.humidity.HumidityLevels.THIN_RAIN;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.mcfr.time.weather.humidity.HumidityLevels;
import net.mcfr.time.weather.humidity.HumidityState;

public enum Seasons {  
  SPRING(-5,
      SUNNY, CLOUDY, 0.7f,
      SUNNY, THIN_RAIN, 0.3f,
      
      CLOUDY, CLOUDY, 0.2f,
      CLOUDY, THIN_RAIN, 0.3f,
      CLOUDY, HEAVY_RAIN, 0.4f,
      CLOUDY, STORM, 0.1f,
      
      THIN_RAIN, THIN_RAIN, 0.6f,
      THIN_RAIN, SUNNY, 0.2f,
      THIN_RAIN, CLOUDY, 0.2f,
      
      HEAVY_RAIN, HEAVY_RAIN, 0.5f,
      HEAVY_RAIN, SUNNY, 0.4f,
      HEAVY_RAIN, CLOUDY, 0.1f,
      
      STORM, SUNNY, 1f),
  SUMMER(10,
      SUNNY, SUNNY, 0.9f,
      SUNNY, CLOUDY, 0.1f,
      
      CLOUDY, CLOUDY, 0.1f,
      CLOUDY, HEAVY_RAIN, 0.8f,
      CLOUDY, STORM, 0.1f,
      
      HEAVY_RAIN, HEAVY_RAIN, 0.3f,
      HEAVY_RAIN, SUNNY, 0.7f,
      
      STORM, SUNNY, 1f),
  FALL(5,
      SUNNY, SUNNY, 0.3f,
      SUNNY, THIN_RAIN, 0.7f,
      
      CLOUDY, CLOUDY, 0.7f,
      CLOUDY, THIN_RAIN, 0.2f,
      CLOUDY, HEAVY_RAIN, 0.1f,
      
      THIN_RAIN, HEAVY_RAIN, 0.2f,
      THIN_RAIN, SUNNY, 0.3f,
      THIN_RAIN, CLOUDY, 0.5f,
      
      HEAVY_RAIN, SUNNY, 0.6f,
      HEAVY_RAIN, CLOUDY, 0.4f),
  WINTER(-10,
      SUNNY, CLOUDY, 1f,
      
      CLOUDY, CLOUDY, 0.6f,
      CLOUDY, THIN_RAIN, 0.2f,
      CLOUDY, HEAVY_RAIN, 0.2f,
      
      THIN_RAIN, THIN_RAIN, 0.3f,
      THIN_RAIN, HEAVY_RAIN, 0.2f,
      THIN_RAIN, STORM, 0.1f,
      THIN_RAIN, CLOUDY, 0.4f,
      
      HEAVY_RAIN, HEAVY_RAIN, 0.1f,
      HEAVY_RAIN, SUNNY, 0.2f,
      HEAVY_RAIN, CLOUDY, 0.7f,
      
      STORM, SUNNY, 1f);
  
  private HumidityState[] humidityStates;
  private int temperatureModificator;

  private Seasons(int temperatureModificator, Object... params) {
    this.humidityStates = generateHumidityTable(params);
    this.temperatureModificator = temperatureModificator;
  }
  
  public int getTemperatureModificator() {
    return this.temperatureModificator;
  }
  
  public HumidityState getState(HumidityLevels level) {
    for (HumidityState state : this.humidityStates) {
      if (state.getLevel().equals(level)) {
        return state;
      }
    }
    return this.humidityStates[0];
  }
  
  private HumidityState[] generateHumidityTable(Object[] params) {
    HumidityState[] states = new HumidityState[HumidityLevels.values().length];
    
    List<Object[]> triplets = new ArrayList<>();
    int i = 0;
    boolean wrongArguments = false;
    Object[] triplet = new Object[3];
    for (Object o : params) {
      switch (i % 3) {
      case 0:
        if (o instanceof HumidityLevels) {
          triplet[0] = o;
        } else {
          wrongArguments = true;
        }
        break;
      case 1:
        if (o instanceof HumidityLevels) {
          triplet[1] = o;
        } else {
          wrongArguments = true;
        }
        break;
      case 2:
        if (o instanceof Float) {
          triplet[2] = o;
          triplets.add(triplet);
          triplet = new Object[3];
        } else {
          wrongArguments = true;
        }
        break;
      }
      if (wrongArguments)
        break;
    }
    
    for (HumidityLevels humidityLevel : HumidityLevels.values()) {
      Map<HumidityLevels, Float> transitions = new HashMap<>();
      
      for (Object[] t : triplets) {
        if (t[0].equals(humidityLevel)) {
          transitions.put((HumidityLevels) t[1], (Float) t[2]);
        }
      }
      
      states[humidityLevel.ordinal()] = new HumidityState(this, humidityLevel, transitions);
    }
    
    return states;
  }
}
