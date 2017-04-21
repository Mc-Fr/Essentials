package net.mcfr.roleplay;

public enum Result {
  CRITICAL_SUCCESS("succès critique"),
  SUCCESS("succès"),
  FAILURE("échec"),
  CRITICAL_FAILURE("échec critique");

  private String sentence;

  private Result(String sentence) {
    this.sentence = sentence;
  }

  public static Result getResult(int roll, int margin) {
    if (roll == 3 || roll == 4)
      return CRITICAL_SUCCESS;
    if ((roll == 17 || roll == 18))
      if (margin < 0)
        return CRITICAL_FAILURE;
      else
        return FAILURE;
    if (margin >= 10)
      return CRITICAL_SUCCESS;
    if (margin <= -10)
      return CRITICAL_FAILURE;
    if (margin >= 0)
      return SUCCESS;

    return FAILURE;
  }

  @Override
  public String toString() {
    return this.sentence;
  }
}