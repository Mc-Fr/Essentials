package net.mcfr.time.weather.wind;

import java.util.Random;

public class Wind {
  private WindLevels level;
  private int orientation;
  
  public Wind() {
    this.level = WindLevels.NONE;
    this.orientation = 0;
  }
  
  public void updateIntensity(Random rand) {
    this.level = WindLevels.values()[rand.nextInt(WindLevels.values().length)];
  }
  
  public void updateOrientation(Random rand) {
    this.orientation = (this.orientation + ((int) Math.floor(rand.nextFloat() * 30f)) - 15) % 360;
  }
  
  public String getWindString(int altitude) {
    String result = "";
    
    result += altitude > 100 ? WindLevels.getSuperior(this.level) : this.level;
    result += ", ";
    
    int section = (int) Math.floor(this.orientation / 22.5f);
    switch (section) {
    case 0:
      result += "N";
      break;
    case 1:
      result += "NNE";
      break;
    case 2:
      result += "NE";
      break;
    case 3:
      result += "ENE";
      break;
    case 4:
      result += "E";
      break;
    case 5:
      result += "ESE";
      break;
    case 6:
      result += "SE";
      break;
    case 7:
      result += "SSE";
      break;
    case 8:
      result += "S";
      break;
    case 9:
      result += "SSO";
      break;
    case 10:
      result += "SO";
      break;
    case 11:
      result += "OSO";
      break;
    case 12:
      result += "O";
      break;
    case 13:
      result += "ONO";
      break;
    case 14:
      result += "NO";
      break;
    case 15:
      result += "NNO";
      break;
    }
    
    return result;
  }
  
  public WindLevels getLevel() {
    return this.level;
  }
  
  public int getOrientation() {
    return this.orientation;
  }
}
