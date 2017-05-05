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

public class ManaCommand extends AbstractCommand {

  public ManaCommand(Essentials plugin) {
    super(plugin);
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    if (src instanceof Player) {
      McFrPlayer player = McFrPlayer.getMcFrPlayer((Player) src);
      if (player.hasCharacter() && player.getTraitLevel("magie") > 0) {
        if (args.hasAny("ajout")) {
          int addingValue = args.<Integer>getOne("ajout").get();
          int healthToSpend = player.getManaState().add(player, addingValue);

          if (addingValue > 0) {
            src.sendMessage(Text.of(TextColors.YELLOW, "Vous avez récupéré " + addingValue + " point" + (addingValue > 1 ? "s" : "") + "."));
          } else {
            src.sendMessage(Text.of(TextColors.YELLOW, "Vous avez perdu " + (-addingValue) + " point" + (addingValue < -1 ? "s" : "") + "."));
            if (healthToSpend > 0) {
              src.sendMessage(Text.of(TextColors.YELLOW, "Vous avez également perdu " + (-healthToSpend) + "point" + (healthToSpend < -1 ? "s" : "") + " de santé !"));
              src.sendMessage(Text.of(TextColors.YELLOW, "Votre santé est de : " + player.getHealthState().getHealthValue() + "/" + player.getHealthState().getMax()
                  + ", malus de " + player.getHealthState().getHealthMalus()));
            }
          }
        }

        src.sendMessage(Text.of(TextColors.YELLOW, "Votre mana est de : " + player.getManaState().getValue() + "/" + player.getManaState().getMax()));
      } else {
        src.sendMessage(Text.of(TextColors.YELLOW, "Vous n'êtes pas magicien et n'avez donc pas de mana."));
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
            .description(Text.of("Permet de gérer votre mana."))
            .permission("essentials.command.mana")
            .executor(this)
            .arguments(GenericArguments.optional(GenericArguments.integer(Text.of("ajout"))))
            .children(getChildrenList(new Mj(getPlugin())))
            .build();
    //#f:1
  }

  @Override
  public String[] getAliases() {
    return new String[] { "mana" };
  }

  static class Mj extends AbstractCommand {

    public Mj(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      McFrPlayer player = McFrPlayer.getMcFrPlayer(args.<Player>getOne("joueur").get());

      if (player.hasCharacter() && player.getTraitLevel("magie") > 0) {
        if (args.hasAny("ajout")) {
          int addingValue = args.<Integer>getOne("ajout").get();
          int healthToSpend = player.getManaState().add(player, addingValue);

          if (addingValue > 0) {
            src.sendMessage(Text.of(TextColors.YELLOW, player.getName() + " a récupéré " + addingValue + " point" + (addingValue > 1 ? "s" : "") + "."));
            player.getPlayer().sendMessage(Text.of(TextColors.YELLOW, "Vous avez récupéré " + addingValue + " point" + (addingValue > 1 ? "s" : "") + "."));
          } else {
            src.sendMessage(Text.of(TextColors.YELLOW, player.getName() + " a perdu " + (-addingValue) + " point" + (addingValue < -1 ? "s" : "") + "."));
            player.getPlayer().sendMessage(Text.of(TextColors.YELLOW, "Vous avez perdu " + (-addingValue) + " point" + (addingValue < -1 ? "s" : "") + "."));
            if (healthToSpend > 0) {
              src.sendMessage(Text.of(TextColors.YELLOW, player.getName() + " a également perdu " + (-healthToSpend) + "point" + (healthToSpend < -1 ? "s" : "") + " de santé !"));
              src.sendMessage(Text.of(TextColors.YELLOW, "La santé de " + player.getName() + " est de : " + player.getHealthState().getHealthValue() + "/" + player.getHealthState().getMax()
                  + ", malus de " + player.getHealthState().getHealthMalus()));
              player.getPlayer().sendMessage(Text.of(TextColors.YELLOW, "Vous avez également perdu " + (-healthToSpend) + "point" + (healthToSpend < -1 ? "s" : "") + " de santé !"));
              player.getPlayer().sendMessage(Text.of(TextColors.YELLOW, "Votre santé est de : " + player.getHealthState().getHealthValue() + "/" + player.getHealthState().getMax()
                  + ", malus de " + player.getHealthState().getHealthMalus()));
            }
          }
        }
        
        src.sendMessage(Text.of(TextColors.YELLOW, "La mana de " + player.getName() + " est de : " + player.getManaState().getValue() + "/" + player.getManaState().getMax()));
        player.getPlayer().sendMessage(Text.of(TextColors.YELLOW, "Votre mana est de : " + player.getManaState().getValue() + "/" + player.getManaState().getMax()));
      } else {
        src.sendMessage(Text.of(TextColors.YELLOW, "Le personnage n'est pas magicien et n'a donc pas de mana."));
      }

      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder()
          .description(Text.of("Permet de gérer la mana d'un joueur."))
          .permission("essentials.command.mana.mj")
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
