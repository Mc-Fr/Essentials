package net.mcfr.commands;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import com.flowpowered.math.vector.Vector3d;

import net.mcfr.Essentials;
import net.mcfr.commands.utils.AbstractCommand;

public class MoveNpcCommand extends AbstractCommand {

  public MoveNpcCommand(Essentials plugin) {
    super(plugin);
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    if (src instanceof Player) {
      Player player = (Player) src;
      String[] npcs = args.<String>getOne("pnjs").get().split(" ");
      Vector3d position = player.getLocation().getPosition();
      double x, y, z;
      
      for(int i = 0; i < npcs.length; i++) {
        x = position.getX() + 0.2D * (double)npcs.length * Math.cos(2.0D * i * Math.PI / (double)npcs.length);
        y = position.getY();
        z = position.getZ() + 0.2D * (double)npcs.length * Math.sin(2.0D * i * Math.PI / (double)npcs.length);
        Sponge.getCommandManager().process(player, "noppes npc " + npcs[i] + " home " + x + " " + y + " " + z);
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
            .description(Text.of("Déplace les NPC de CustomNPC ciblés."))
            .permission("essentials.command.movenpc")
            .executor(this)
            .arguments(GenericArguments.remainingJoinedStrings(Text.of("pnjs")))
            .build();
    //#f:1
  }

  @Override
  public String[] getAliases() {
    return new String[] { "movenpc" };
  }
}
