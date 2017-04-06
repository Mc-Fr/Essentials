package net.mcfr.time.weather;

import java.util.Map;

public class HumidityState {
  private Seasons season;
  private HumidityLevels level;
  private Map<HumidityLevels, Float> transitions;
  
  public HumidityState(Seasons season, HumidityLevels humidityLevel, Map<HumidityLevels, Float> transitions) {
    this.season = season;
    this.level = humidityLevel;
    this.transitions = transitions;
  }
  
  public HumidityLevels getLevel() {
    return this.level;
  }
  
  public HumidityState next() {
    float rng = Weather.rand.nextFloat();
    float cumulate = 0f;
    for (Map.Entry<HumidityLevels, Float> e : transitions.entrySet()) {
      cumulate += e.getValue();
      if (rng < cumulate) {
        return this.season.getState(e.getKey());
      }
    }
    return this.season.getState(HumidityLevels.SUNNY);
  }
}
