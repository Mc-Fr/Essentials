package net.mcfr.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import net.mcfr.Essentials;
import net.mcfr.commands.utils.AbstractCommand;
import net.mcfr.utils.McFrPlayer;

//TODO : help
//FIXME : première instance de commande bug

public class NoCommand extends AbstractCommand {

  public NoCommand(Essentials plugin) {
    super(plugin);
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    return CommandResult.empty();
  }

  @Override
  public CommandSpec getCommandSpec() {
    //#f:0
    return CommandSpec.builder()
            .description(Text.of("Permet de désactiver des fonctionnalités."))
            .permission("essentials.command.no")
            .executor(this)
            .children(getChildrenList(new Mp(getPlugin()), new Team(getPlugin())))
            .build();
    //#f:1
  }

  @Override
  public String[] getAliases() {
    return new String[] { "no" };
  }

  static class Mp extends AbstractCommand {
    public Mp(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      if (src instanceof Player) {
        Player player = (Player) src;
        McFrPlayer mcfr = McFrPlayer.getMcFrPlayer(player);
        mcfr.toggleWantMp();
        src.sendMessage(Text.of(TextColors.YELLOW, String.format("Vous %s les mps.", mcfr.wantsMP() ? "recevrez" : "ne recevrez plus")));
      } else {
        src.sendMessage(ONLY_PLAYERS_COMMAND);
        return CommandResult.empty();
      }
      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      //#f:0
      return CommandSpec.builder()
              .description(Text.of("Permet de désactiver les mps."))
              .permission("essentials.command.no.mp")
              .executor(this)
              .build();
      //#f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "mp" };
    }

  }

  static class Team extends AbstractCommand {

    public Team(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      if (src instanceof Player) {
        Player player = (Player) src;
        McFrPlayer mcfr = McFrPlayer.getMcFrPlayer(player);
        mcfr.toggleWantTeam();
        src.sendMessage(
            Text.of(TextColors.YELLOW, String.format("Vous %s les messages de l'équipe.", mcfr.wantsTeam() ? "ignorez" : "n'ignorez plus")));
      } else {
        src.sendMessage(ONLY_PLAYERS_COMMAND);
        return CommandResult.empty();
      }
      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      //#f:0
      return CommandSpec.builder()
              .description(Text.of("Permet de désactiver les messages d'un tchat Team."))
              .permission("essentials.command.no.team")
              .executor(this)
              .build();
      //#f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "team", "tm" };
    }
  }
}