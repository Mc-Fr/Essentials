package net.mcfr.chat;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import net.mcfr.babel.Language;
import net.mcfr.utils.McFrPlayer;

public class MessageData {

  /**
   * L'émetteur du message.
   */
  private Player sender;

  /**
   * Le type de tchat du message.
   */
  private ChatType chatType;

  /**
   * Le contenu du message.
   */
  private String message;

  /**
   * Les destinataires du messages.
   */
  private Collection<Player> recipients;

  /**
   * Construit un {@code MessageData} à partir d'un {@code Player} et d'une chaîne de caractère. Extrait du message le
   * type de tchat à utiliser, ainsi que tous les destinataires du message.
   *
   * @param sender
   *          L'émetteur du message
   * @param text
   *          Le contenu du message
   */
  public MessageData(Player sender, String text) {
    Objects.requireNonNull(sender);
    this.sender = sender;
    this.message = text;
    this.chatType = ChatType.getChatType(getMessage(), McFrPlayer.getMcFrPlayer(sender).getDefaultChat());
    this.message = getMessage().substring(getChatType().getCharsRequired().length());
    this.recipients = getListeningPlayers();
  }

  public Player getSender() {
    return this.sender;
  }

  public ChatType getChatType() {
    return this.chatType;
  }

  public String getMessage() {
    return this.message;
  }

  public Collection<Player> getRecipients() {
    return this.recipients;
  }

  /**
   * Transforme le {@code MessageData} en {@code Text} afin qu'il puisse être envoyé.
   *
   * @param recipient
   *          Le destinataire du message
   * @return une instance de {@code Text}
   */
  public Text toText(Player recipient) {
    return toText(recipient, getMessage());
  }

  public Text toText(Player recipient, String message) {
    ChatType type = getChatType();
    TextColor color = McFrPlayer.distance(getSender(), recipient) <= type.getDistance() / 2 ? type.getNearColor()
        : McFrPlayer.distance(getSender(), recipient) <= type.getDistance() ? type.getFarColor() : type.getNearColor();
    String[] names = getFormattedNames(recipient);
    return Text.of(color, type.getStyle(), String.format(type.getMessageFormat(), names[0], names[1], message));
  }

  private Collection<Player> getListeningPlayers() {
    Set<Player> players = new HashSet<>();
    Sponge.getServer().getOnlinePlayers().stream()
        .filter(p -> p.equals(getSender()) || p.getWorld().equals(getSender().getWorld())
            && (McFrPlayer.distance(getSender(), p) <= getChatType().getDistance() || getChatType().getDistance() == -1)
            && p.hasPermission(getChatType().getListenPermission()))
        .forEach(players::add);
    return players;
  }

  private String[] getFormattedNames(Player player) {
    String[] names = new String[2];
    McFrPlayer mcfrSender = McFrPlayer.getMcFrPlayer(getSender());
    McFrPlayer mcfrPlayer = McFrPlayer.getMcFrPlayer(player);
    if (getChatType().isRealname()) {
      names[0] = mcfrSender.getPlayer().getName();
      names[1] = mcfrPlayer.getPlayer().getName();
    } else {
      names[0] = mcfrSender.getName();
      names[1] = mcfrPlayer.getName();
    }
    return names;
  }

  public void send() {
    if (getChatType().isTranslatable()) {
      Language lang = McFrPlayer.getMcFrPlayer(this.sender).getLanguage();
      String[] translatedMessages = new String[4];
      for (int i = 0; i < 4; i++) {
        translatedMessages[i] = lang.transformMessage(getMessage(), i);
      }
      getRecipients().forEach(p -> {
        if (p.equals(getSender())) {
          p.sendMessage(toText(p));
        } else {
          p.sendMessage(toText(p, translatedMessages[McFrPlayer.getMcFrPlayer(p).getLanguageLevel(lang)]));
        }
      });
    } else {
      getRecipients().forEach(p -> p.sendMessage(toText(p)));
    }
  }

  public boolean checkConditions() {
    Player sender = getSender();
    ChatType type = getChatType();
    String message = getMessage();
    if (!(type.isRealname() || McFrPlayer.getMcFrPlayer(sender).hasCharacter())) {
      sender.sendMessage(Text.of(TextColors.RED, "Vous devez avoir un personnage pour utiliser ce canal !"));
      return false;
    }
    if (McFrPlayer.getMcFrPlayer(sender).isMuted() && type != ChatType.HELP) {
      sender.sendMessage(Text.of(TextColors.RED, "Vous êtes muet !"));
      return false;
    }
    if (!sender.hasPermission(type.getSpeakPermission())) {
      sender.sendMessage(Text.of(TextColors.RED, "Vous ne pouvez pas utiliser ce tchat !"));
      return false;
    }
    if (message.length() == 0)
      return false;

    if (type == ChatType.TEAM) {
      getRecipients().removeIf(p -> !McFrPlayer.getMcFrPlayer(p).wantsTeam());
    }

    return true;
  }

}
