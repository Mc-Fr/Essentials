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
import net.mcfr.commands.utils.AbstractCommand;
import net.mcfr.utils.McFrPlayer;

public class FatigueCommand extends AbstractCommand {

  public FatigueCommand(Essentials plugin) {
    super(plugin);
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    McFrPlayer player = McFrPlayer.getMcFrPlayer(args.<Player>getOne("joueur").get());

    if (player.hasCharacter()) {
      if (args.hasAny("ajout")) {
        int addingValue = args.<Integer>getOne("ajout").get();
        player.getHealthState().addFatigue(player, addingValue);

        if (addingValue > 0) {
          src.sendMessage(Text.of(TextColors.YELLOW, player.getName() + " a récupéré " + addingValue + "points de fatigue."));
          player.getPlayer().sendMessage(Text.of(TextColors.YELLOW, "Vous avez récupéré " + addingValue + "points de fatigue."));
        } else {
          src.sendMessage(Text.of(TextColors.YELLOW, player.getName() + " a perdu " + (-addingValue) + "points de fatigue."));
          player.getPlayer().sendMessage(Text.of(TextColors.YELLOW, "Vous avez perdu " + (-addingValue) + "points de fatigue."));
        }

        player.getPlayer().sendMessage(Text.of(TextColors.YELLOW, "Votre fatigue est de : " + player.getHealthState().getFatigueValue() + "/" + player.getHealthState().getMax()
            + ", malus de " + player.getHealthState().getFatigueMalus()));
      }

      src.sendMessage(Text.of(TextColors.YELLOW, "Le niveau de fatigue de " + player.getName() + " est de : " + player.getHealthState().getFatigueValue() + "/"
          + player.getHealthState().getMax() + ", malus de " + player.getHealthState().getFatigueMalus()));
    } else {
      src.sendMessage(Text.of(TextColors.YELLOW, "Le joueur ciblé n'a pas de personnage."));
    }

    return CommandResult.success();
  }

  @Override
  public CommandSpec getCommandSpec() {
    //#f:0
    return CommandSpec.builder()
            .description(Text.of("Permet de gérer la fatigue d'un joueur."))
            .permission("essentials.command.fatigue")
            .executor(this)
            .arguments(GenericArguments.player(Text.of("joueur")), GenericArguments.optional(GenericArguments.integer(Text.of("ajout"))))
            .build();
    //#f:1
  }

  @Override
  public String[] getAliases() {
    return new String[] { "fatigue" };
  }
}
