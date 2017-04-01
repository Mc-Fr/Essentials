package net.mcfr.time;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.spongepowered.api.Sponge;

public class McFrDate {
  private static final long SECONDS_OFFSET = 1484848800;
  private static final McFrDate INITIAL_DATE = new McFrDate(1, 1, 1420);
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
  private static final long DAYS_IN_MONTH = 42;
  private static final long MONTHS_IN_YEAR = MONTHS_NAMES.size();
  
  private int year;
  private int month;
  private int day;
  
  public McFrDate(int day, int month, int year) {
    this.day = day;
    this.month = month;
    this.year = year;
  }
  
  public McFrDate() {
    this.actualize();
  }
  
  public void actualize() {
    long realSeconds = Calendar.getInstance().getTime().getTime() / 1000 - SECONDS_OFFSET;
    
    this.year = (int) Math.floor(realSeconds / (SECONDS_IN_DAY * DAYS_IN_MONTH * MONTHS_IN_YEAR));
    realSeconds -= this.year * SECONDS_IN_DAY * DAYS_IN_MONTH * MONTHS_IN_YEAR;
    
    this.month = (int) Math.floor(realSeconds / (SECONDS_IN_DAY * DAYS_IN_MONTH));
    realSeconds -= this.month * SECONDS_IN_DAY * DAYS_IN_MONTH;
    
    this.day = (int) Math.floor(realSeconds / SECONDS_IN_DAY) - 1;
    
    this.year += INITIAL_DATE.getYear();
    this.month += INITIAL_DATE.getMonth();
    this.day += INITIAL_DATE.getDay();
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
  
  @Override
  public String toString() {
    long worldTime = Sponge.getServer().getWorldProperties("world").get().getWorldTime() % 24000;
    int hour = (int) Math.floor(1f * worldTime / 1000f);
    return this.day + " " + MONTHS_NAMES.get(this.month - 1) + " " + this.year + " - " + hour + "h";
  }
}
