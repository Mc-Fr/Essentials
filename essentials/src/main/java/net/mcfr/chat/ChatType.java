package net.mcfr.chat;

import static org.spongepowered.api.text.TextTemplate.*;
import static org.spongepowered.api.text.TextTemplate.of;
import static org.spongepowered.api.text.format.TextColors.*;
import static org.spongepowered.api.text.format.TextStyles.*;

import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.format.TextColor;

public enum ChatType {
  WHISPER(2, DARK_PURPLE, DARK_PURPLE, "\"'", false, of(ITALIC, arg("player"), " : ", arg("message")), true),
  LOW_VOICE(5, LIGHT_PURPLE, DARK_PURPLE, "\"", false, of(ITALIC, arg("player"), " : ", arg("message")), true),
  SOFT_VOICE(10, LIGHT_PURPLE, LIGHT_PURPLE, "'", false, of(ITALIC, arg("player"), " : ", arg("message")), true),
  MEDIUM(20, WHITE, GRAY, "", false, of(arg("player"), " : ", arg("message")), true),
  LOUD_SPEAK(45, GOLD, GOLD, "&", false, of(arg("player"), " : ", arg("message")), true),
  SHOUT(60, RED, DARK_RED, "!", false, of(ITALIC, arg("player"), " : ", arg("message")), true),

  STEALTHY_ACTION(6, GREEN, DARK_GREEN, "*\"", false, of(ITALIC, "* ", arg("player"), " ", arg("message"))),
  DISCREET_ACTION(12, GREEN, DARK_GREEN, "*'", false, of(ITALIC, "* ", arg("player"), " ", arg("message"))),
  ACTION(25, GREEN, DARK_GREEN, "*", false, of("* ", arg("player"), " ", arg("message"))),
  VISIBLE_ACTION(40, GREEN, DARK_GREEN, "*&", false, of("* ", arg("player"), " ", arg("message"))),

  ORP_LOW(5, GRAY, "(\"", false, of("\"(", arg("player"), " : ", arg("message"), ")")),
  ORP_SOFT(10, GRAY, "('", false, of("'(", arg("player"), " : ", arg("message"), ")")),
  ORP(25, GRAY, "(", false, of("(", arg("player"), " : ", arg("message"), ")")),
  ORP_LOUD(60, GRAY, "(&", false, of("&(", arg("player"), " : ", arg("message"), ")")),

  EVENT_ACTION(100, GREEN, DARK_GREEN, "%", false, of(ITALIC, "* ", arg("message"), " *")),
  EVENT(350, DARK_PURPLE, "$", false, of("[Évent] ", arg("message"))),

  HELP(-1, RED, "?", true, of("[Support] ", arg("player"), " : ", arg("message"))),
  TEAM(-1, RED, "@", true, of("[Équipe] ", arg("player"), " : ", arg("message"))),
  ADMIN(-1, RED, "=", true, of("[Admin] ", arg("player"), " : ", arg("message")));

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
   * Caractères nécessaires pour pouvoir parler sur ce tchat.
   */
  private String charsRequired;

  /**
   * Si {@code true}, le pseudonyme du joueur doit être affiché, sinon on affiche le nom du personnage.
   */
  private boolean isRealname;

  /**
   * {@code TextTemplate} de formattage du message.
   */
  private TextTemplate template;

  /**
   * Indique si le message peut être traduit.
   */
  private boolean isTranslatable;

  private ChatType(int distance, TextColor nearColor, TextColor farColor, String charsRequired, boolean isRealname, TextTemplate template,
      boolean isTranslatable) {
    this.distance = distance;
    this.nearColor = nearColor;
    this.farColor = farColor;
    this.charsRequired = charsRequired;
    this.isRealname = isRealname;
    this.template = template;
    this.isTranslatable = isTranslatable;
  }

  private ChatType(int distance, TextColor nearColor, TextColor farColor, String charsRequired, boolean isRealname, TextTemplate template) {
    this(distance, nearColor, farColor, charsRequired, isRealname, template, false);
  }

  private ChatType(int distance, TextColor color, String charsRequired, boolean isRealname, TextTemplate template) {
    this(distance, color, color, charsRequired, isRealname, template);
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

  public String getCharsRequired() {
    return this.charsRequired;
  }

  public boolean isRealname() {
    return this.isRealname;
  }

  public TextTemplate getTemplate() {
    return this.template;
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
