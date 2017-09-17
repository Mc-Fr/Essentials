package net.mcfr.time;

import net.mcfr.time.weather.Weather;

public interface TimeService {
  
  public void update();
  
  public void freezeTime();
  
  public void freezeTime(TimeValue value);
  
  public void resumeTime();
  
  public McFrDate getDate();
  
  public Weather getWeather();
  
  public boolean isTimeFreezed();
}
