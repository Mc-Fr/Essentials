package net.mcfr.time;

public class TimeValue {
  private static final int MAX = 24000;
  
  private int value;
  
  public TimeValue (int value) {
    this.value = value % MAX;
  }
  
  public int get() {
    return this.value;
  }
  
  public void set(int v) {
    this.value = v % MAX;
  }
  
  public void add(int v) {
    this.value = (this.value + v) % MAX;
  }
  
  public boolean equals(TimeValue other) {
    return this.value == other.value;
  }
}
