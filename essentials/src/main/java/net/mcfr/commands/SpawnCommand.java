package net.mcfr.commands;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import net.mcfr.Essentials;
import net.mcfr.commands.utils.AbstractCommand;

public class SpawnCommand extends AbstractCommand {

  public SpawnCommand(Essentials plugin) {
    super(plugin);
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    if (src instanceof Player) {
      Location<World> loc = ((Player) src).getLocation();
      Sponge.getCommandManager().process(src, String.format("tp %s %d %d %d", src.getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
      src.sendMessage(Text.of(TextColors.YELLOW, "Vous avez été téléporté au spawn."));
    } else {
      src.sendMessage(ONLY_PLAYERS_COMMAND);
    }

    return CommandResult.success();
  }

  @Override
  public CommandSpec getCommandSpec() {
    //#f:0
    return CommandSpec.builder()
            .description(Text.of("Téléporte le joueur au spawn"))
            .permission("essentials.command.spawn")
            .executor(this)
            .build();
    //#f:1
  }

  @Override
  public String[] getAliases() {
    return new String[] { "spawn" };
  }

}
