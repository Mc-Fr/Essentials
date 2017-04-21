package net.mcfr.time.weather;

import static net.mcfr.time.weather.humidity.HumidityLevels.CLOUDY;
import static net.mcfr.time.weather.humidity.HumidityLevels.HEAVY_RAIN;
import static net.mcfr.time.weather.humidity.HumidityLevels.STORM;
import static net.mcfr.time.weather.humidity.HumidityLevels.SUNNY;
import static net.mcfr.time.weather.humidity.HumidityLevels.THIN_RAIN;

import java.util.ArrayList;
import java.util.List;

import net.mcfr.time.weather.humidity.HumidityLevels;
import net.mcfr.time.weather.humidity.HumidityState;
import net.mcfr.time.weather.humidity.Transition;

public enum Seasons {  
  SPRING(-5),
  SUMMER(10),
  FALL(5),
  WINTER(-10);
  
  private static final List<Transition> SPRING_TRANSITIONS = new ArrayList<>();
  private static final List<Transition> SUMMER_TRANSITIONS = new ArrayList<>();
  private static final List<Transition> FALL_TRANSITIONS = new ArrayList<>();
  private static final List<Transition> WINTER_TRANSITIONS = new ArrayList<>();
  
  static {
    SPRING_TRANSITIONS.add(new Transition(SUNNY, CLOUDY, 0.7f));
    SPRING_TRANSITIONS.add(new Transition(SUNNY, THIN_RAIN, 0.3f));
    SPRING_TRANSITIONS.add(new Transition(CLOUDY, CLOUDY, 0.2f));
    SPRING_TRANSITIONS.add(new Transition(CLOUDY, THIN_RAIN, 0.3f));
    SPRING_TRANSITIONS.add(new Transition(CLOUDY, HEAVY_RAIN, 0.4f));
    SPRING_TRANSITIONS.add(new Transition(CLOUDY, STORM, 0.1f));
    SPRING_TRANSITIONS.add(new Transition(THIN_RAIN, THIN_RAIN, 0.6f));
    SPRING_TRANSITIONS.add(new Transition(THIN_RAIN, SUNNY, 0.2f));
    SPRING_TRANSITIONS.add(new Transition(THIN_RAIN, CLOUDY, 0.2f));
    SPRING_TRANSITIONS.add(new Transition(HEAVY_RAIN, HEAVY_RAIN, 0.5f));
    SPRING_TRANSITIONS.add(new Transition(HEAVY_RAIN, SUNNY, 0.4f));
    SPRING_TRANSITIONS.add(new Transition(HEAVY_RAIN, CLOUDY, 0.1f));
    SPRING_TRANSITIONS.add(new Transition(STORM, SUNNY, 1f));
    
    SUMMER_TRANSITIONS.add(new Transition(SUNNY, SUNNY, 0.9f));
    SUMMER_TRANSITIONS.add(new Transition(SUNNY, CLOUDY, 0.1f));
    SUMMER_TRANSITIONS.add(new Transition(CLOUDY, CLOUDY, 0.1f));
    SUMMER_TRANSITIONS.add(new Transition(CLOUDY, HEAVY_RAIN, 0.8f));
    SUMMER_TRANSITIONS.add(new Transition(CLOUDY, STORM, 0.1f));
    SUMMER_TRANSITIONS.add(new Transition(HEAVY_RAIN, HEAVY_RAIN, 0.3f));
    SUMMER_TRANSITIONS.add(new Transition(HEAVY_RAIN, SUNNY, 0.7f));
    SUMMER_TRANSITIONS.add(new Transition(STORM, SUNNY, 1f));
    
    FALL_TRANSITIONS.add(new Transition(SUNNY, SUNNY, 0.3f));
    FALL_TRANSITIONS.add(new Transition(SUNNY, THIN_RAIN, 0.7f));
    FALL_TRANSITIONS.add(new Transition(CLOUDY, CLOUDY, 0.7f));
    FALL_TRANSITIONS.add(new Transition(CLOUDY, THIN_RAIN, 0.2f));
    FALL_TRANSITIONS.add(new Transition(CLOUDY, HEAVY_RAIN, 0.1f));
    FALL_TRANSITIONS.add(new Transition(THIN_RAIN, HEAVY_RAIN, 0.2f));
    FALL_TRANSITIONS.add(new Transition(THIN_RAIN, SUNNY, 0.3f));
    FALL_TRANSITIONS.add(new Transition(THIN_RAIN, CLOUDY, 0.5f));
    FALL_TRANSITIONS.add(new Transition(HEAVY_RAIN, SUNNY, 0.6f));
    FALL_TRANSITIONS.add(new Transition(HEAVY_RAIN, CLOUDY, 0.4f));
    
    WINTER_TRANSITIONS.add(new Transition(SUNNY, CLOUDY, 1f));
    WINTER_TRANSITIONS.add(new Transition(CLOUDY, CLOUDY, 0.6f));
    WINTER_TRANSITIONS.add(new Transition(CLOUDY, THIN_RAIN, 0.2f));
    WINTER_TRANSITIONS.add(new Transition(CLOUDY, HEAVY_RAIN, 0.2f));
    WINTER_TRANSITIONS.add(new Transition(THIN_RAIN, THIN_RAIN, 0.3f));
    WINTER_TRANSITIONS.add(new Transition(THIN_RAIN, HEAVY_RAIN, 0.2f));
    WINTER_TRANSITIONS.add(new Transition(THIN_RAIN, STORM, 0.1f));
    WINTER_TRANSITIONS.add(new Transition(THIN_RAIN, CLOUDY, 0.4f));
    WINTER_TRANSITIONS.add(new Transition(HEAVY_RAIN, HEAVY_RAIN, 0.1f));
    WINTER_TRANSITIONS.add(new Transition(HEAVY_RAIN, SUNNY, 0.2f));
    WINTER_TRANSITIONS.add(new Transition(HEAVY_RAIN, CLOUDY, 0.7f));
    WINTER_TRANSITIONS.add(new Transition(STORM, SUNNY, 1f));
    
    SPRING.generateHumidityStates(SPRING_TRANSITIONS);
    SUMMER.generateHumidityStates(SUMMER_TRANSITIONS);
    FALL.generateHumidityStates(FALL_TRANSITIONS);
    WINTER.generateHumidityStates(WINTER_TRANSITIONS);
  }
  
  private HumidityState[] humidityStates;
  private int temperatureModificator;

  private Seasons(int temperatureModificator) {
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
  
  private void generateHumidityStates(List<Transition> transitions) {
    HumidityState[] states = new HumidityState[HumidityLevels.values().length];
    
    for (HumidityLevels humidityLevel : HumidityLevels.values()) {
      List<Transition> transitionsList = new ArrayList<>();
      for (Transition transition : transitions) {
        if (transition.getStart().equals(humidityLevel)) {
          transitionsList.add(transition);
        }
      }
      
      states[humidityLevel.ordinal()] = new HumidityState(this, humidityLevel, transitionsList);
    }
    
    this.humidityStates = states;
  }
}
