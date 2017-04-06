package net.mcfr.time.weather;

public enum WindLevels {
  NONE("Aucun vent"),
  WEAK("Vent faible"),
  MEDIUM("Vent moyen"),
  STRONG("Vent fort"),
  MAD("Vent très fort");
  
  private String text;
  
  private WindLevels(String text) {
    this.text = text;
  }
  
  public String toString() {
    return this.text;
  }
  
  public static WindLevels getSuperior(WindLevels level) {
    switch (level) {
    case NONE:
      return WEAK;
    case WEAK:
      return MEDIUM;
    case MEDIUM:
      return STRONG;
    case STRONG:
      return MAD;
    case MAD:
      return MAD;
    default:
      return NONE;
    }
  }
}
