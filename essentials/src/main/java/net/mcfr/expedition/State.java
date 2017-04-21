package net.mcfr.expedition;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public enum State {
//#f:0
  IN_AREA( 
      Text.of(TextColors.YELLOW, "Vous vous sentez enfin débarassé de ce qui vous suivait.")),
  ADVERT( 
      Text.of(TextColors.YELLOW, "Vous n'êtes toujours pas en sécurité..."),
      Text.of(TextColors.YELLOW, "Vous sentez une présence qui vous épie... Mieux vaut faire demi-tour.")),
  HURT1(
      Text.of(TextColors.YELLOW, "La présence est toujours là, elle vous observe partir."),
      Text.of(TextColors.DARK_RED, "Une flèchette vient se planter dans votre bras ! Retournez vite sur vos pas !")),
  HURT2(
      Text.of(TextColors.YELLOW, "Vous entendez des bruits de pas derrière vous pendant que vous faites demi-tour."),
      Text.of(TextColors.DARK_RED, "Une autre fléchette touche votre jambe ! Fuyez !")),
  HURT3(
      Text.of(TextColors.YELLOW, ""),
      Text.of(TextColors.DARK_RED, "Encore une ! Dans le torse cette fois-ci. Vous allez y passer !")),
  KILL,TO_COMPUTE;
  // #f:1

  private final Text safeMessage;
  private final Text dangerMessage;

  private State(Text safeMessage, Text dangerMessage) {
    this.safeMessage = safeMessage;
    this.dangerMessage = dangerMessage;
  }

  private State(Text safeMessage) {
    this(safeMessage, Text.of(""));
  }

  private State() {
    this(Text.of(""));
  }
  
  public Text getSafeMessage() {
    return this.safeMessage;
  }
  
  public Text getDangerMessage() {
    return this.dangerMessage;
  }
}
