package net.mcfr.time;

public class TimeImp implements TimeService {
  private McFrDate currentDate;
  private boolean isTimeStopped;
  
  public TimeImp() {
    this.currentDate = new McFrDate();
  }
  
  @Override
  public void update() {
    if (!this.isTimeStopped)
      this.currentDate.actualize();
  }
  
  @Override
  public McFrDate getDate() {
    return this.currentDate;
  }
  
  @Override
  public boolean isTimeStopped() {
    return this.isTimeStopped;
  }
}
