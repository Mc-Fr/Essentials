package net.mcfr.chat;

import static org.spongepowered.api.text.format.TextColors.DARK_GREEN;
import static org.spongepowered.api.text.format.TextColors.DARK_PURPLE;
import static org.spongepowered.api.text.format.TextColors.DARK_RED;
import static org.spongepowered.api.text.format.TextColors.GOLD;
import static org.spongepowered.api.text.format.TextColors.GRAY;
import static org.spongepowered.api.text.format.TextColors.GREEN;
import static org.spongepowered.api.text.format.TextColors.LIGHT_PURPLE;
import static org.spongepowered.api.text.format.TextColors.RED;
import static org.spongepowered.api.text.format.TextColors.WHITE;
import static org.spongepowered.api.text.format.TextStyles.ITALIC;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextStyle;
import org.spongepowered.api.text.format.TextStyles;

public enum ChatType {
  WHISPER(2, DARK_PURPLE, DARK_PURPLE, ITALIC, "\"'", "Vous entendez un chuchotement indistinct.", false, "%1$s : %3$s", true),
  LOW_VOICE(5, LIGHT_PURPLE, DARK_PURPLE, ITALIC, "\"", "Vous entendez une voix faible.", false, "%1$s : %3$s", true),
  SOFT_VOICE(10, LIGHT_PURPLE, LIGHT_PURPLE, ITALIC, "'", "Vous entendez une voix douce indistincte.", false, "%1$s : %3$s", true),
  MEDIUM(20, WHITE, GRAY, TextStyles.NONE, "", "Vous entendez une voix au loin.", false, "%1$s : %3$s", true),
  LOUD_SPEAK(45, GOLD, GOLD, TextStyles.NONE, "&", "Quelqu'un parle fort au loin.", false, "%1$s : %3$s", true),
  SHOUT(60, RED, DARK_RED, TextStyles.NONE, "!", "Quelqu'un crie au loin.", false, "%1$s : %3$s", true),

  STEALTHY_ACTION(6, GREEN, DARK_GREEN, ITALIC, "*\"", false, "* %1$s %3$s"),
  DISCREET_ACTION(12, GREEN, DARK_GREEN, ITALIC, "*'", false, "* %1$s %3$s"),
  ACTION(25, GREEN, DARK_GREEN, TextStyles.NONE, "*", false, "* %1$s %3$s"),
  VISIBLE_ACTION(40, GREEN, DARK_GREEN, TextStyles.NONE, "*&", false, "* %1$s %3$s"),

  ORP_WHISPER(4, GRAY, "('", false, "'(%1$s : %3$s)"),
  ORP(25, GRAY, "(", false, "(%1$s : %3$s)"),
  ORP_LOUD(60, GRAY, "(&", false, "&(%1$s : %3$s)"),

  EVENT_ACTION(100, GREEN, DARK_GREEN, ITALIC, "%", false, "* %3$s *"),
  EVENT(350, DARK_PURPLE, "$", false, "[Évent] %3$s"),

  HELP(-1, RED, "?", true, "[Support] %1$s : %3$s"),
  TEAM(-1, RED, "@", true, "[Équipe] %1$s : %3$s"),
  ADMIN(-1, RED, "=", true, "[Admin] %1$s : %3$s");

  private int distance;
  private TextColor nearColor;
  private TextColor farColor;
  private TextStyle style;
  private String charsRequired;
  private Optional<String> tooFarMessage;
  private boolean isRealname;
  private String messageFormat;
  private boolean isTranslatable;

  private ChatType(int distance, TextColor nearColor, TextColor farColor, TextStyle style, String charsRequired, Optional<String> tooFarMessage,
      boolean isRealname, String messageFormat, boolean isTranslatable) {
    this.distance = distance;
    this.nearColor = nearColor;
    this.farColor = farColor;
    this.style = style;
    this.charsRequired = charsRequired;
    this.tooFarMessage = tooFarMessage;
    this.isRealname = isRealname;
    this.messageFormat = messageFormat;
    this.isTranslatable = isTranslatable;
  }

  private ChatType(int distance, TextColor nearColor, TextColor farColor, TextStyle style, String charsRequired, String tooFarMessage,
      boolean isRealname, String messageFormat, boolean isTranslatable) {
    this(distance, nearColor, farColor, style, charsRequired, Optional.of(tooFarMessage), isRealname, messageFormat, isTranslatable);
  }

  private ChatType(int distance, TextColor nearColor, TextColor farColor, TextStyle style, String charsRequired, boolean isRealname,
      String messageFormat) {
    this(distance, nearColor, farColor, style, charsRequired, Optional.<String>empty(), isRealname, messageFormat, false);
  }

  private ChatType(int distance, TextColor color, String charsRequired, boolean isRealname, String messageFormat) {
    this(distance, color, color, TextStyles.NONE, charsRequired, isRealname, messageFormat);
  }

  public int getDistance() {
    return this.distance;
  }

  public TextColor getNearColor() {
    return this.nearColor;
  }

  public TextColor getFarColor() {
    return this.farColor;
  }

  public TextStyle getStyle() {
    return this.style;
  }

  public String getCharsRequired() {
    return this.charsRequired;
  }

  public Optional<String> getTooFarMessage() {
    return this.tooFarMessage;
  }

  public boolean isRealname() {
    return this.isRealname;
  }

  public String getMessageFormat() {
    return this.messageFormat;
  }

  public boolean isTranslatable() {
    return this.isTranslatable;
  }

  /**
   * Retourne le type de tchat en fonction du préfixe de tchat et du tchat par défaut passés en paramètre.
   *
   * @param prefix
   *          Le préfixe utilisé par le joueur lors de la rédaction de son message
   * @param defaultChat
   *          Le tchat par défaut défini par le joueur
   * @return le type de tchat correspondant
   */
  public static ChatType getChatType(String text, ChatType defaultChat) {
    String prefix;
    if (text.length() > 1) {
      prefix = text.substring(0, 2);

      for (ChatType type : values()) {
        if (type.getCharsRequired().equals(prefix))
          return type;
      }
    }

    prefix = text.substring(0, 1);

    for (ChatType type : values()) {
      if (type.getCharsRequired().equals(prefix))
        return type;
    }

    return defaultChat;
  }

  public String getSpeakPermission() {
    return String.format(getPermission(), "speak");
  }

  public String getListenPermission() {
    return String.format(getPermission(), "listen");
  }

  private String getPermission() {
    return "essentials.chat.%s." + name().toLowerCase();
  }

  public static Map<String, ChatType> getRollChatTypes() {
    Map<String, ChatType> result = new HashMap<>();
    result.put("\"", STEALTHY_ACTION);
    result.put("'", DISCREET_ACTION);
    result.put("&", VISIBLE_ACTION);
    return result;
  }
}
