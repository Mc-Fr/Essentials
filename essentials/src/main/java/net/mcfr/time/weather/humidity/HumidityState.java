package net.mcfr.time.weather.humidity;

import java.util.List;
import java.util.Random;

import net.mcfr.time.weather.Seasons;

public class HumidityState {
  private Seasons season;
  private HumidityLevels level;
  private List<Transition> transitions;
  
  public HumidityState(Seasons season, HumidityLevels humidityLevel, List<Transition> transitions) {
    this.season = season;
    this.level = humidityLevel;
    this.transitions = transitions;
  }
  
  public HumidityLevels getLevel() {
    return this.level;
  }
  
  public HumidityState next(Random rand) {
    float rng = rand.nextFloat();
    float cumulate = 0f;
    
    for (Transition transition : transitions) {
      cumulate += transition.getValue();
      if (rng < cumulate) {
        return this.season.getState(transition.getEnd());
      }
    }
    return this.season.getState(HumidityLevels.SUNNY);
  }
}
