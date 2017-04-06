package net.mcfr.time;

public interface TimeService {
  
  public void update();
  
  public void freezeTime();
  
  public void freezeTime(TimeValue value);
  
  public void resumeTime();
  
  public McFrDate getDate();
  
  public boolean isTimeFreezed();
}
