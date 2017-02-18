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
        src.sendMessage(Text.of(TextColors.YELLOW, "Votre malus de santé est de : " + player.getHealthMalus()));
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
            .description(Text.of("Affiche votre malus de santé."))
            .permission("essentials.command.health")
            .executor(this)
            .children(getChildrenList(new Hurt(getPlugin()), 
                new Heal(getPlugin()),
                new Check(getPlugin())))
            .build();
    //#f:1
  }
  
  @Override
  public String[] getAliases() {
    return new String[] { "health", "h" };
  }
  
  static class Hurt extends AbstractCommand {

    public Hurt(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      if (src instanceof Player) {
        McFrPlayer player = McFrPlayer.getMcFrPlayer((Player) src);
        if (player.hasCharacter()) {
          int injury = args.<Integer>getOne("blessure").get();
          injury = injury > 0 ? -injury : injury;
          player.addHealth(injury);
          src.sendMessage(Text.of(TextColors.YELLOW, "Votre malus de santé est maintenant de : " + player.getHealthMalus()));
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
      // #f:0
      return CommandSpec.builder()
          .description(Text.of("Ajoute un malus de santé."))
          .permission("essentials.command.health.hurt")
          .arguments(GenericArguments.integer(Text.of("blessure")))
          .executor(this)
          .build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "hurt" };
    }
  }
  
  static class Heal extends AbstractCommand {

    public Heal(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      if (src instanceof Player) {
        McFrPlayer player = McFrPlayer.getMcFrPlayer((Player) src);
        if (player.hasCharacter()) {
          int healing = args.<Integer>getOne("soin").get();
          healing = healing < 0 ? -healing : healing;
          player.addHealth(healing);
          src.sendMessage(Text.of(TextColors.YELLOW, "Votre malus de santé est maintenant de : " + player.getHealthMalus()));
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
      // #f:0
      return CommandSpec.builder()
          .description(Text.of("Retire une partie du malus de santé."))
          .permission("essentials.command.health.heal")
          .arguments(GenericArguments.integer(Text.of("soin")))
          .executor(this)
          .build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "heal" };
    }
  }
  
  static class Check extends AbstractCommand {

    public Check(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      McFrPlayer player = McFrPlayer.getMcFrPlayer(args.<Player>getOne("joueur").get());
      src.sendMessage(Text.of(TextColors.YELLOW, "Le malus de santé de " + player.getName() + " est de : " + player.getHealthMalus()));
      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder()
          .description(Text.of("Vérifie la santé d'un joueur."))
          .permission("essentials.command.health.check")
          .arguments(GenericArguments.player(Text.of("joueur")))
          .executor(this)
          .build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "check" };
    }
  }
}
