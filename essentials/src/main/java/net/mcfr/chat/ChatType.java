package net.mcfr.chat;

import static org.spongepowered.api.text.format.TextColors.*;
import static org.spongepowered.api.text.format.TextStyles.*;

import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextStyle;
import org.spongepowered.api.text.format.TextStyles;

public enum ChatType {
  WHISPER(2, DARK_PURPLE, DARK_PURPLE, ITALIC, "\"'", false, "%1$s : %3$s", true),
  LOW_VOICE(5, LIGHT_PURPLE, DARK_PURPLE, ITALIC, "\"", false, "%1$s : %3$s", true),
  SOFT_VOICE(10, LIGHT_PURPLE, LIGHT_PURPLE, ITALIC, "'", false, "%1$s : %3$s", true),
  MEDIUM(20, WHITE, GRAY, TextStyles.NONE, "", false, "%1$s : %3$s", true),
  LOUD_SPEAK(45, GOLD, GOLD, TextStyles.NONE, "&", false, "%1$s : %3$s", true),
  SHOUT(60, RED, DARK_RED, TextStyles.NONE, "!", false, "%1$s : %3$s", true),

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

  /**
   * Distance de compréhension du message. Au delà, le message n'est pas affiché.
   */
  private int distance;

  /**
   * Couleur du message lorsque le joueur qui entend est proche de celui qui parle.
   */
  private TextColor nearColor;

  /**
   * Couleur du message lorsque le joueur qui entend est loin de celui qui parle.
   */
  private TextColor farColor;

  /**
   * Style à appliquer au message.
   */
  private TextStyle style;

  /**
   * Caractères nécessaires pour pouvoir parler sur ce tchat.
   */
  private String charsRequired;

  /**
   * Si {@code true}, le pseudonyme du joueur doit être affiché, sinon on affiche le nom du personnage.
   */
  private boolean isRealname;

  /**
   * Chaîne permettant de formatter le message avant son envoi.
   * 
   * @see String#format
   */
  private String messageFormat;

  /**
   * Indique si le message peut être traduit.
   */
  private boolean isTranslatable;

  private ChatType(int distance, TextColor nearColor, TextColor farColor, TextStyle style, String charsRequired, boolean isRealname,
      String messageFormat, boolean isTranslatable) {
    this.distance = distance;
    this.nearColor = nearColor;
    this.farColor = farColor;
    this.style = style;
    this.charsRequired = charsRequired;
    this.isRealname = isRealname;
    this.messageFormat = messageFormat;
    this.isTranslatable = isTranslatable;
  }

  private ChatType(int distance, TextColor nearColor, TextColor farColor, TextStyle style, String charsRequired, boolean isRealname,
      String messageFormat) {
    this(distance, nearColor, farColor, style, charsRequired, isRealname, messageFormat, false);
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
   *          le préfixe utilisé par le joueur lors de la rédaction de son message
   * @param defaultChat
   *          le tchat par défaut défini par le joueur
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

  /**
   * @return la permission pour pouvoir entendre ce tchat
   */
  public String getSpeakPermission() {
    return String.format(getPermission(), "speak");
  }

  /**
   * @return la permission pour pouvoir parler sur tchat
   */
  public String getListenPermission() {
    return String.format(getPermission(), "listen");
  }

  /**
   * @return la permission générique propre au tchat courant
   */
  private String getPermission() {
    return "essentials.chat.%s." + name().toLowerCase();
  }
}
