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
   * Construit un {@code MessageData} à partir d'un {@code Player} et d'une
   * chaîne de caractère. Extrait du message le type de tchat à utiliser, ainsi
   * que tous les destinataires du message.
   *
   * @param sender
   *          l'émetteur du message
   * @param text
   *          le contenu du message
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
   * Transforme le message passé en {@code Text} afin qu'il puisse être envoyé.
   * 
   * @param recipient
   *          le destinataire du message
   * @param message
   *          le message à transformer
   * @return une instance de {@code Text}
   */
  public Text toText(Player recipient, String message) {
    ChatType type = getChatType();
    int distance = type.getDistance();
    double distanceBetweenPlayers = McFrPlayer.distance(getSender(), recipient);
    TextColor nearColor = type.getNearColor();

    TextColor color = distanceBetweenPlayers <= distance / 2 ? nearColor : distanceBetweenPlayers <= distance ? type.getFarColor() : nearColor;
    String[] names = getFormattedNames(recipient);

    return Text.of(color, type.getStyle(), String.format(type.getMessageFormat(), names[0], names[1], message));
  }

  /**
   * @return la liste des joueurs capables d'entendre le message
   */
  private Collection<Player> getListeningPlayers() {
    Set<Player> players = new HashSet<>();
    Player sender = getSender();
    Sponge.getServer().getOnlinePlayers().stream().filter(p -> {
      boolean samePlayer = p.equals(sender);

      boolean rpChatLow = getChatType().equals(ChatType.WHISPER) || getChatType().equals(ChatType.LOW_VOICE)
          || getChatType().equals(ChatType.SOFT_VOICE) || getChatType().equals(ChatType.MEDIUM);
      boolean rpChatListening = McFrPlayer.distance(sender, p) <= McFrPlayer.getMcFrPlayer(p).getListeningRange();
      boolean rpChatRange = McFrPlayer.distance(sender, p) <= getChatType().getDistance();
      boolean rpChat = p.getWorld().equals(sender.getWorld()) && ((!rpChatLow && rpChatRange) || (rpChatLow && rpChatListening && rpChatRange));

      boolean supportChat = getChatType().getDistance() == -1;
      boolean permission = p.hasPermission(getChatType().getListenPermission());

      return samePlayer || (rpChat || supportChat) && permission;
    }).forEach(players::add);
    return players;
  }

  /**
   * @param player
   *          le destinataire du message
   * @return les noms à utiliser pour traiter le message
   */
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

  /**
   * Envoi le message aux joueurs.
   */
  public void send() {
    if (getChatType().isTranslatable()) {
      Language lang = McFrPlayer.getMcFrPlayer(this.sender).getLanguage();
      String[] translatedMessages = new String[4];
      for (int i = 0; i < 4; i++) {
        translatedMessages[i] = lang.transformMessage(getMessage(), i);

        if (!lang.getAlias().equals("commun")) {
          translatedMessages[i] = "[" + lang.getAlias().substring(0, 4) + "] " + translatedMessages[i];
        }
      }
      getRecipients().forEach(p -> {
        if (p.equals(getSender())) {
          p.sendMessage(toText(p, translatedMessages[3]));
        } else {
          p.sendMessage(toText(p, translatedMessages[McFrPlayer.getMcFrPlayer(p).getLanguageLevel(lang)]));
        }
      });
    } else {
      getRecipients().forEach(p -> p.sendMessage(toText(p, getMessage())));
    }
  }

  /**
   * Vérifie si le message saisi est valide en fonction de diverses conditions.
   * 
   * @return la validité du message
   */
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