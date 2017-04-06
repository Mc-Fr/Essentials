package net.mcfr.time;

import java.util.Optional;

import org.spongepowered.api.Sponge;

public class TimeImp implements TimeService {
  
  private McFrDate currentDate;
  private Optional<TimeValue> freezedTime;
  
  public TimeImp() {
    this.currentDate = new McFrDate();
    this.freezedTime = Optional.empty();
    Sponge.getServer().getWorldProperties("world").get().setGameRule("doDaylightCycle", "false");
  }
  
  @Override
  public void update() {
    TimeValue roleplayTime;
    
    if (isTimeFreezed()) {
      roleplayTime = this.freezedTime.get();
    } else {
      this.currentDate.actualize();
      roleplayTime = getRoleplayTime();
    }
    
    if (!getMinecraftTime().equals(roleplayTime)) {
      Sponge.getServer().getWorldProperties("world").get().setWorldTime(roleplayTime.get());
    }
  }
  
  private TimeValue getMinecraftTime() {
    return new TimeValue((int) (Sponge.getServer().getWorldProperties("world").get().getWorldTime()));
  }
  
  private TimeValue getRoleplayTime() {
    return this.currentDate.getTimeValue();
  }
  
  @Override
  public void freezeTime() {
    freezeTime(this.currentDate.getTimeValue());
  }
  
  @Override
  public void freezeTime(TimeValue value) {
    this.freezedTime = Optional.of(new TimeValue(value.get()));
  }
  
  @Override
  public void resumeTime() {
    this.freezedTime = Optional.empty();
  }
  
  @Override
  public McFrDate getDate() {
    if (isTimeFreezed()) {
      return new McFrDate(this.currentDate.getDay(), this.currentDate.getMonth(), this.currentDate.getYear(), this.freezedTime.get());
    } else {
      return this.currentDate;
    }
  }
  
  @Override
  public boolean isTimeFreezed() {
    return this.freezedTime.isPresent();
  }
}
