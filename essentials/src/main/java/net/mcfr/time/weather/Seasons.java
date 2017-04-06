package net.mcfr.time.weather;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Seasons {  
  SPRING(-5,
      HumidityLevels.SUNNY, HumidityLevels.CLOUDY, 0.7f,
      HumidityLevels.SUNNY, HumidityLevels.THIN_RAIN, 0.3f,
      
      HumidityLevels.CLOUDY, HumidityLevels.CLOUDY, 0.2f,
      HumidityLevels.CLOUDY, HumidityLevels.THIN_RAIN, 0.3f,
      HumidityLevels.CLOUDY, HumidityLevels.HEAVY_RAIN, 0.4f,
      HumidityLevels.CLOUDY, HumidityLevels.STORM, 0.1f,
      
      HumidityLevels.THIN_RAIN, HumidityLevels.THIN_RAIN, 0.6f,
      HumidityLevels.THIN_RAIN, HumidityLevels.SUNNY, 0.2f,
      HumidityLevels.THIN_RAIN, HumidityLevels.CLOUDY, 0.2f,
      
      HumidityLevels.HEAVY_RAIN, HumidityLevels.HEAVY_RAIN, 0.5f,
      HumidityLevels.HEAVY_RAIN, HumidityLevels.SUNNY, 0.4f,
      HumidityLevels.HEAVY_RAIN, HumidityLevels.CLOUDY, 0.1f,
      
      HumidityLevels.STORM, HumidityLevels.SUNNY, 1f),
  SUMMER(20,
      HumidityLevels.SUNNY, HumidityLevels.SUNNY, 0.9f,
      HumidityLevels.SUNNY, HumidityLevels.CLOUDY, 0.1f,
      
      HumidityLevels.CLOUDY, HumidityLevels.CLOUDY, 0.1f,
      HumidityLevels.CLOUDY, HumidityLevels.HEAVY_RAIN, 0.8f,
      HumidityLevels.CLOUDY, HumidityLevels.STORM, 0.1f,
      
      HumidityLevels.HEAVY_RAIN, HumidityLevels.HEAVY_RAIN, 0.3f,
      HumidityLevels.HEAVY_RAIN, HumidityLevels.SUNNY, 0.7f,
      
      HumidityLevels.STORM, HumidityLevels.SUNNY, 1f),
  FALL(5,
      HumidityLevels.SUNNY, HumidityLevels.SUNNY, 0.3f,
      HumidityLevels.SUNNY, HumidityLevels.THIN_RAIN, 0.7f,
      
      HumidityLevels.CLOUDY, HumidityLevels.CLOUDY, 0.7f,
      HumidityLevels.CLOUDY, HumidityLevels.THIN_RAIN, 0.2f,
      HumidityLevels.CLOUDY, HumidityLevels.HEAVY_RAIN, 0.1f,
      
      HumidityLevels.THIN_RAIN, HumidityLevels.HEAVY_RAIN, 0.2f,
      HumidityLevels.THIN_RAIN, HumidityLevels.SUNNY, 0.3f,
      HumidityLevels.THIN_RAIN, HumidityLevels.CLOUDY, 0.5f,
      
      HumidityLevels.HEAVY_RAIN, HumidityLevels.SUNNY, 0.6f,
      HumidityLevels.HEAVY_RAIN, HumidityLevels.CLOUDY, 0.4f),
  WINTER(-20,
      HumidityLevels.SUNNY, HumidityLevels.CLOUDY, 1f,
      
      HumidityLevels.CLOUDY, HumidityLevels.CLOUDY, 0.6f,
      HumidityLevels.CLOUDY, HumidityLevels.THIN_RAIN, 0.2f,
      HumidityLevels.CLOUDY, HumidityLevels.HEAVY_RAIN, 0.2f,
      
      HumidityLevels.THIN_RAIN, HumidityLevels.THIN_RAIN, 0.3f,
      HumidityLevels.THIN_RAIN, HumidityLevels.HEAVY_RAIN, 0.2f,
      HumidityLevels.THIN_RAIN, HumidityLevels.STORM, 0.1f,
      HumidityLevels.THIN_RAIN, HumidityLevels.CLOUDY, 0.4f,
      
      HumidityLevels.HEAVY_RAIN, HumidityLevels.HEAVY_RAIN, 0.1f,
      HumidityLevels.HEAVY_RAIN, HumidityLevels.SUNNY, 0.2f,
      HumidityLevels.HEAVY_RAIN, HumidityLevels.CLOUDY, 0.7f,
      
      HumidityLevels.STORM, HumidityLevels.SUNNY, 1f);
  
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
