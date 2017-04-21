package net.mcfr.commands;

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
import net.mcfr.chat.Language;
import net.mcfr.commands.utils.AbstractCommand;
import net.mcfr.utils.McFrPlayer;

public class BabelCommand extends AbstractCommand {

  public BabelCommand(Essentials plugin) {
    super(plugin);
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    if (src instanceof Player) {
      McFrPlayer player = McFrPlayer.getMcFrPlayer(src.getName());

      if (args.hasAny("langue")) {
        Language lang = args.<Language>getOne("langue").get();
        int playerLevel = player.getLanguageLevel(lang);

        if (playerLevel > 0) {
          player.setLanguage(lang);
          src.sendMessage(Text.of(TextColors.YELLOW, "Vous parlez désormais " + lang.getDisplayName() + "."));
        } else {
          src.sendMessage(Text.of(TextColors.YELLOW, "Vous ne parlez pas " + lang.getDisplayName() + " !"));
        }
      }
    } else {
      src.sendMessage(ONLY_PLAYERS_COMMAND);
    }
    return CommandResult.success();
  }

  @Override
  public CommandSpec getCommandSpec() {
    //#f:0
    return CommandSpec.builder()
            .description(Text.of("Change la langue parlée actuellement par le joueur."))
            .permission("essentials.command.babel")
            .arguments(GenericArguments.choices(Text.of("langue"), Language.getLanguages()))
            .executor(this)
            .build();
    //#f:1
  }

  @Override
  public String[] getAliases() {
    return new String[] { "babel", "l", "lang", "langue" };
  }
}
