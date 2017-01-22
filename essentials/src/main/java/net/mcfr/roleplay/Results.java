package net.mcfr.roleplay;

public enum Results {
  CRITICAL_SUCCESS,
  SUCCESS,
  FAILURE,
  CRITICAL_FAILURE;

  public static Results getResult(int roll, int margin) {
    if (roll == 3 || roll == 4)
      return CRITICAL_SUCCESS;
    if (roll == 17 || roll == 18)
      return CRITICAL_FAILURE;
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
    switch (this) {
    case CRITICAL_FAILURE:
      return "échec critique";
    case CRITICAL_SUCCESS:
      return "succès critique";
    case FAILURE:
      return "échec";
    case SUCCESS:
      return "succès";
    }

    throw new IllegalStateException("Le résultat du jet de dés n'a pas pu être déterminé !");
  }

}
