package net.mcfr.time;

import java.util.Optional;

import org.spongepowered.api.Sponge;

import net.mcfr.time.weather.Weather;

public class TimeImp implements TimeService {
  
  private McFrDate date;
  private Weather weather;
  private Optional<TimeValue> freezedTime;
  
  public TimeImp() {
    this.date = new McFrDate();
    this.freezedTime = Optional.empty();
    this.weather = new Weather(this.date);
    Sponge.getServer().getWorldProperties("world").get().setGameRule("doDaylightCycle", "false");
  }
  
  @Override
  public void update() {
    TimeValue roleplayTime;
    
    if (isTimeFreezed()) {
      roleplayTime = this.freezedTime.get();
    } else {
      this.date.actualize();
      roleplayTime = getRoleplayTime();
      
      if (this.weather.mustUpdate(this.date)) {
        this.weather.updateWeather(this.date);
      }
    }
    
    if (!getMinecraftTime().equals(roleplayTime)) {
      Sponge.getServer().getWorldProperties("world").get().setWorldTime(roleplayTime.get());
    }
  }
  
  private TimeValue getMinecraftTime() {
    return new TimeValue((int) (Sponge.getServer().getWorldProperties("world").get().getWorldTime()));
  }
  
  private TimeValue getRoleplayTime() {
    return this.date.getTimeValue();
  }
  
  @Override
  public void freezeTime() {
    freezeTime(this.date.getTimeValue());
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
      return new McFrDate(this.date.getDay(), this.date.getMonth(), this.date.getYear(), this.freezedTime.get());
    } else {
      return this.date;
    }
  }
  
  @Override
  public Weather getWeather() {
    return this.weather;
  }
  
  @Override
  public boolean isTimeFreezed() {
    return this.freezedTime.isPresent();
  }
}
