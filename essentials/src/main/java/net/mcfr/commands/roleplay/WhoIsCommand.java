package net.mcfr.commands.roleplay;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import net.mcfr.Essentials;
import net.mcfr.commands.utils.AbstractCommand;
import net.mcfr.death.CareService;
import net.mcfr.utils.McFrPlayer;

public class WhoIsCommand extends AbstractCommand {

  public WhoIsCommand(Essentials plugin) {
    super(plugin);
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    Player player = null;
    boolean selfTarget = false;
    if (args.hasAny("joueur")) {
      player = args.<Player>getOne("joueur").get();
    } else {
      if (src instanceof Player) {
        player = (Player) src;
        selfTarget = true;
      } else {
        src.sendMessage(ONLY_PLAYERS_COMMAND);
        return CommandResult.success();
      }
    }
    McFrPlayer mcfrPlayer = McFrPlayer.getMcFrPlayer(player);
    String name = mcfrPlayer.getName();
    List<Text> messages = new ArrayList<>();
    messages.add(Text.of(TextColors.DARK_GREEN, player.getName() + " -> " + name));
    if (selfTarget) {
      messages.add(Text.of(TextColors.DARK_GREEN, " - Desc : " + McFrPlayer.getMcFrPlayer(player).getDescription()));
      Optional<CareService> careService = Sponge.getServiceManager().provide(CareService.class);
      if (careService.isPresent()) {
        messages.add(
            Text.of(TextColors.DARK_GREEN, " - Vous " + (careService.get().isInProtectedArea(player) ? "êtes" : "n'êtes pas") + " en zone sûre."));
      }
      messages.add(Text.of(TextColors.DARK_GREEN, mcfrPlayer.getAttributesString()));
      messages.add(Text.of(TextColors.DARK_GREEN, mcfrPlayer.getSkillsString()));
      messages.add(Text.of(TextColors.DARK_GREEN, mcfrPlayer.getTraitsString()));
    }
    src.sendMessages(messages);

    return CommandResult.success();
  }

  @Override
  public CommandSpec getCommandSpec() {
    //#f:0
    return CommandSpec.builder()
            .description(Text.of("Affiche les informations d'un joueur"))
            .permission("essentials.command.whois")
            .executor(this)
            .arguments(GenericArguments.optional(GenericArguments.player(Text.of("joueur"))))
            .build();
    //#f:1
  }

  @Override
  public String[] getAliases() {
    return new String[] { "whois" };
  }

}
