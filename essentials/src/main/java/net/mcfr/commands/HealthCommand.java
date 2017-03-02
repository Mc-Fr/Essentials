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

public class HealthCommand extends AbstractCommand {

  public HealthCommand(Essentials plugin) {
    super(plugin);
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    if (src instanceof Player) {
      McFrPlayer player = McFrPlayer.getMcFrPlayer((Player) src);
      if (player.hasCharacter()) {
        if (args.hasAny("ajout")) {
          int addingValue = args.<Integer>getOne("ajout").get();
          player.getHealth().add(player, addingValue);

          if (addingValue > 0) {
            src.sendMessage(Text.of(TextColors.YELLOW, "Vous avez été soigné de " + addingValue + "points."));
          } else {
            src.sendMessage(Text.of(TextColors.YELLOW, "Vous avez été blessé de " + (-addingValue) + "points."));
          }
        }

        src.sendMessage(Text.of(TextColors.YELLOW, "Votre santé est de : " + player.getHealth().getValue() + "/" + player.getHealth().getMax()
            + ", malus de " + player.getHealth().getMalus()));
      } else {
        src.sendMessage(Text.of(TextColors.YELLOW, "Vous n'avez pas de personnage."));
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
            .description(Text.of("Permet de gérer votre santé."))
            .permission("essentials.command.health")
            .executor(this)
            .arguments(GenericArguments.optional(GenericArguments.integer(Text.of("ajout"))))
            .children(getChildrenList(new Mj(getPlugin())))
            .build();
    //#f:1
  }

  @Override
  public String[] getAliases() {
    return new String[] { "health", "h" };
  }

  static class Mj extends AbstractCommand {

    public Mj(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      McFrPlayer player = McFrPlayer.getMcFrPlayer(args.<Player>getOne("joueur").get());

      if (player.hasCharacter()) {
        if (args.hasAny("ajout")) {
          int addingValue = args.<Integer>getOne("ajout").get();
          player.getHealth().add(player, addingValue);

          if (addingValue > 0) {
            src.sendMessage(Text.of(TextColors.YELLOW, "Vous avez soigné " + player.getName() + " de " + addingValue + "points."));
            player.getPlayer().sendMessage(Text.of(TextColors.YELLOW, "Vous avez été soigné de " + addingValue + "points."));
          } else {
            src.sendMessage(Text.of(TextColors.YELLOW, "Vous avez blessé " + player.getName() + " de " + (-addingValue) + "points."));
            player.getPlayer().sendMessage(Text.of(TextColors.YELLOW, "Vous avez été blessé de " + (-addingValue) + "points."));
          }

          player.getPlayer().sendMessage(Text.of(TextColors.YELLOW, "Votre santé est de : " + player.getHealth().getValue() + "/" + player.getHealth().getMax()
              + ", malus de " + player.getHealth().getMalus()));
        }

        src.sendMessage(Text.of(TextColors.YELLOW, "Le malus de santé de " + player.getName() + " est de : " + player.getHealth().getValue() + "/"
            + player.getHealth().getMax() + ", malus de " + player.getHealth().getMalus()));
      } else {
        src.sendMessage(Text.of(TextColors.YELLOW, "Le joueur ciblé n'a pas de personnage."));
      }

      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder()
          .description(Text.of("Permet de gérer la santé d'un joueur."))
          .permission("essentials.command.health.mj")
          .arguments(GenericArguments.player(Text.of("joueur")), GenericArguments.optional(GenericArguments.integer(Text.of("ajout"))))
          .executor(this)
          .build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "mj" };
    }
  }
}
