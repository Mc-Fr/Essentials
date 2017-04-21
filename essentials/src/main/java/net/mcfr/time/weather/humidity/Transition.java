package net.mcfr.time.weather.humidity;

public class Transition {
  private HumidityLevels start;
  private HumidityLevels end;
  private float value;
  
  public Transition(HumidityLevels start, HumidityLevels end, float value) {
    this.start = start;
    this.end = end;
    this.value = value;
  }
  
  public HumidityLevels getStart() {
    return this.start;
  }
  
  public HumidityLevels getEnd() {
    return this.end;
  }
  
  public float getValue() {
    return this.value;
  }
}
