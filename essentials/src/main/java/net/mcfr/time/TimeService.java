package net.mcfr.time;

public interface TimeService {
  
  public void update();
  
  public McFrDate getDate();
  
  public boolean isTimeStopped();
}
