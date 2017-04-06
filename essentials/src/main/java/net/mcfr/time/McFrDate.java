package net.mcfr.time;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class McFrDate {
  private static final long MS_IN_S = 1000;
  private static final long SECONDS_OFFSET = 1484848800;
  private static final McFrDate INITIAL_DATE = new McFrDate(1, 1, 1420, new TimeValue(0));
  private static final float MEAN_DAY_PROPORTION = 0.8333333f;
  private static final List<String> MONTHS_NAMES = new ArrayList<>();
  
  static {
    // Printemps
    MONTHS_NAMES.add("Emeral");
    MONTHS_NAMES.add("Carminal");
    MONTHS_NAMES.add("Roséal");
    MONTHS_NAMES.add("Auréal");
    
    // Été
    MONTHS_NAMES.add("Ambridor");
    MONTHS_NAMES.add("Oridor");
    MONTHS_NAMES.add("Mercuridor");
    MONTHS_NAMES.add("Pyridor");
    
    // Automne
    MONTHS_NAMES.add("Cendraire");
    MONTHS_NAMES.add("Cyanaire");
    MONTHS_NAMES.add("Pourpraire");
    MONTHS_NAMES.add("Anthraire");
    
    // Hiver
    MONTHS_NAMES.add("Mauvose");
    MONTHS_NAMES.add("Sombrose");
    MONTHS_NAMES.add("Lazurose");
    MONTHS_NAMES.add("Fumose");
  }
  
  private static final long SECONDS_IN_DAY = 14400;
  private static final int HOURS_IN_HALF_DAY = 12;
  private static final int TICKS_IN_HOUR = 1000;
  private static final int DAYS_IN_MONTH = 42;
  private static final int MONTHS_IN_YEAR = MONTHS_NAMES.size();
  
  private int year;
  private int month;
  private int day;
  private TimeValue timeValue;
  
  public McFrDate(int day, int month, int year, TimeValue timeValue) {
    this.day = day;
    this.month = month;
    this.year = year;
    this.timeValue = timeValue;
  }
  
  public McFrDate() {
    this.timeValue = new TimeValue(0);
    this.actualize();
  }
  
  public void actualize() {
    long realMilliSeconds = Calendar.getInstance().getTime().getTime() - SECONDS_OFFSET * MS_IN_S;
    
    this.year = (int) Math.floor(realMilliSeconds / (MS_IN_S * SECONDS_IN_DAY * DAYS_IN_MONTH * MONTHS_IN_YEAR));
    realMilliSeconds -= this.year * MS_IN_S * SECONDS_IN_DAY * DAYS_IN_MONTH * MONTHS_IN_YEAR;
    
    this.month = (int) Math.floor(realMilliSeconds / (MS_IN_S * SECONDS_IN_DAY * DAYS_IN_MONTH));
    realMilliSeconds -= this.month * MS_IN_S * SECONDS_IN_DAY * DAYS_IN_MONTH;
    
    this.day = (int) Math.floor(realMilliSeconds / (MS_IN_S * SECONDS_IN_DAY)) - 1;
    realMilliSeconds -= this.day * MS_IN_S * SECONDS_IN_DAY;
    
    double daylightProportion = MEAN_DAY_PROPORTION + 0.05f * (1 - Math.cos(2f * Math.PI * (1f * this.month + 1f * (this.day - 1) / DAYS_IN_MONTH) / MONTHS_IN_YEAR));
    long dayHourValue = (long) Math.floor(1f * MS_IN_S * SECONDS_IN_DAY * daylightProportion / HOURS_IN_HALF_DAY);
    long nightHourValue = (long) Math.floor(1f * MS_IN_S * SECONDS_IN_DAY * (1 - daylightProportion) / HOURS_IN_HALF_DAY);
    
    this.timeValue.set((int) Math.floor(1d * realMilliSeconds / (1d * dayHourValue) * TICKS_IN_HOUR));
    if (this.timeValue.get() >= HOURS_IN_HALF_DAY * TICKS_IN_HOUR) {
      this.timeValue.set(HOURS_IN_HALF_DAY * TICKS_IN_HOUR);
      realMilliSeconds -= HOURS_IN_HALF_DAY * dayHourValue;
      this.timeValue.add((int) Math.floor(1d * realMilliSeconds / (1d * nightHourValue) * TICKS_IN_HOUR));
    }
    
    this.year += INITIAL_DATE.getYear();
    this.month += INITIAL_DATE.getMonth();
    this.day += INITIAL_DATE.getDay();
    this.timeValue.add(INITIAL_DATE.getTimeValue().get());
  }
  
  public int getYear() {
    return this.year;
  }
  
  public int getMonth() {
    return this.month;
  }
  
  public int getDay() {
    return this.day;
  }
  
  public TimeValue getTimeValue() {
    return this.timeValue;
  }
  
  public int getHour() {
    return (this.timeValue.get() / TICKS_IN_HOUR + 6) % (2 * HOURS_IN_HALF_DAY);
  }
  
  @Override
  public String toString() {
    return this.day + " " + MONTHS_NAMES.get(this.month - 1) + " " + this.year + " - " + this.getHour() + "h";
  }
}
