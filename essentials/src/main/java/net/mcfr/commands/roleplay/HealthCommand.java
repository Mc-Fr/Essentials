package net.mcfr.commands.roleplay;

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
import net.mcfr.commands.AbstractCommand;
import net.mcfr.roleplay.Attribute;
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
          player.getHealthState().add(player, addingValue);

          if (addingValue > 0) {
            src.sendMessage(Text.of(TextColors.YELLOW, "Vous avez été soigné de " + addingValue + " point" + (addingValue > 1 ? "s" : "") + "."));
          } else {
            src.sendMessage(Text.of(TextColors.YELLOW, "Vous avez été blessé de " + (-addingValue) + " point" + (addingValue < -1 ? "s" : "") + "."));
          }
        }
        
        int health = player.getHealthState().getValue();
        int max = player.getHealthState().getMax();
        int end = player.getAttributePoints(Attribute.ENDURANCE);
        int malus = player.getHealthState().getMalus(Attribute.ENDURANCE);
        
        if (health <= -2 * end) {
          src.sendMessage(Text.of(TextColors.YELLOW, "Vous êtes mort ! Votre santé a atteint -" + (2*end) + " points."));
        } else if (health <= -end) {
          src.sendMessage(Text.of(TextColors.YELLOW, "Vous êtes inconscient, votre santé est de : " + health + "/" + max + ", malus de " + malus));
        } else if (health <= 0) {
          src.sendMessage(Text.of(TextColors.YELLOW, "Vous êtes gravement blessé, votre santé est de : " + health + "/" + max + ", malus de " + malus));
        } else {
          src.sendMessage(Text.of(TextColors.YELLOW, "Votre santé est de : " + health + "/" + max));
        }
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
          player.getHealthState().add(player, addingValue);

          if (addingValue > 0) {
            src.sendMessage(Text.of(TextColors.YELLOW, "Vous avez soigné " + player.getName() + " de " + addingValue + " points."));
            player.getPlayer().sendMessage(Text.of(TextColors.YELLOW, "Vous avez été soigné de " + addingValue + " points."));
          } else {
            src.sendMessage(Text.of(TextColors.YELLOW, "Vous avez blessé " + player.getName() + " de " + (-addingValue) + " points."));
            player.getPlayer().sendMessage(Text.of(TextColors.YELLOW, "Vous avez été blessé de " + (-addingValue) + " points."));
          }
        }

        int health = player.getHealthState().getValue();
        int max = player.getHealthState().getMax();
        int end = player.getAttributePoints(Attribute.ENDURANCE);
        int malus = player.getHealthState().getMalus(Attribute.ENDURANCE);
        
        if (args.hasAny("ajout")) {
          if (health <= -2 * end) {
            player.getPlayer().sendMessage(Text.of(TextColors.YELLOW, "Vous êtes mort ! Votre santé a atteint -" + (2*end) + " points."));
          } else if (health <= -end) {
            player.getPlayer().sendMessage(Text.of(TextColors.YELLOW, "Vous êtes inconscient, votre santé est de : " + health + "/" + max + ", malus de " + malus));
          } else if (health <= 0) {
            player.getPlayer().sendMessage(Text.of(TextColors.YELLOW, "Vous êtes gravement blessé, votre santé est de : " + health + "/" + max + ", malus de " + malus));
          } else {
            player.getPlayer().sendMessage(Text.of(TextColors.YELLOW, "Votre santé est de : " + health + "/" + max));
          }
        }
        
        if (health <= -2 * end) {
          src.sendMessage(Text.of(TextColors.YELLOW, player.getName() + " est mort ! Sa santé a atteint -" + (2*end) + " points."));
        } else if (health <= -end) {
          src.sendMessage(Text.of(TextColors.YELLOW, player.getName() + " est inconscient, sa santé est de : " + health + "/" + max + ", malus de " + malus));
        } else if (health <= 0) {
          src.sendMessage(Text.of(TextColors.YELLOW, player.getName() + " est gravement blessé, sa santé est de : " + health + "/" + max + ", malus de " + malus));
        } else {
          src.sendMessage(Text.of(TextColors.YELLOW, "La santé de " + player.getName() + " est de : " + health + "/" + max));
        }
        
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
