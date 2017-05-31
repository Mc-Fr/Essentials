package net.mcfr.commands.game;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import net.mcfr.Essentials;
import net.mcfr.commands.AbstractCommand;

//TODO : help

public class SpeedCommand extends AbstractCommand {

  private final static double FACTOR = 1 / 0.45;

  public SpeedCommand(Essentials plugin) {
    super(plugin);
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    if (!(src instanceof Player)) {
      src.sendMessage(ONLY_PLAYERS_COMMAND);
    }

    return CommandResult.empty();
  }

  @Override
  public CommandSpec getCommandSpec() {
    // #f:0
    return CommandSpec.builder()
            .description(Text.of("Permet de changer sa vitesse"))
            .permission("essentials.command.speed")
            .executor(this)
            .children(getChildrenList(new Fly(getPlugin()), new Walk(getPlugin())))
            .build();
    // #f:1
  }

  @Override
  public String[] getAliases() {
    return new String[] { "speed" };
  }

  static class Fly extends AbstractCommand {
    public Fly(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      if (src instanceof Player) {
        Player player = (Player) src;
        double speed = args.<Double>getOne("vitesse").get();
        if (Math.abs(speed) > 1) {
          src.sendMessage(Text.of(TextColors.RED, "La vitesse doit être comprise entre -1 et 1."));
          return CommandResult.empty();
        }
        player.offer(Keys.FLYING_SPEED, speed / FACTOR);
        return CommandResult.success();
      } else {
        src.sendMessage(ONLY_PLAYERS_COMMAND);
        return CommandResult.empty();
      }
    }

    @Override
    public CommandSpec getCommandSpec() {
      //#f:0
      return CommandSpec.builder()
              .description(Text.of("Permet de changer la vitesse de vol."))
              .permission("essentials.command.speed.fly")
              .arguments(GenericArguments.doubleNum(Text.of("vitesse")))
              .executor(this)
              .build();
      //#f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "fly" };
    }
  }

  static class Walk extends AbstractCommand {
    public Walk(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      if (src instanceof Player) {
        Player player = (Player) src;
        double speed = args.<Double>getOne("vitesse").get();
        if (Math.abs(speed) > 1) {
          src.sendMessage(Text.of(TextColors.RED, "La vitesse doit être comprise entre -1 et 1."));
          return CommandResult.empty();
        }
        player.offer(Keys.WALKING_SPEED, speed / FACTOR);
        return CommandResult.success();
      } else {
        src.sendMessage(ONLY_PLAYERS_COMMAND);
      }
      return CommandResult.empty();
    }

    @Override
    public CommandSpec getCommandSpec() {
      //#f:0
      return CommandSpec.builder()
              .description(Text.of("Permet de changer la vitesse de marche."))
              .permission("essentials.command.speed.walk")
              .arguments(GenericArguments.doubleNum(Text.of("vitesse")))
              .executor(this)
              .build();
      //#f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "walk" };
    }
  }
}