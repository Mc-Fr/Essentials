package net.mcfr.commands;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import net.mcfr.Essentials;
import net.mcfr.commands.utils.AbstractCommand;
import net.mcfr.utils.McFrPlayer;

public class WalkCommand extends AbstractCommand {

  public WalkCommand(Essentials plugin) {
    super(plugin);
  }
  
  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    if (src instanceof Player) {
      Player p = (Player) src;
      
      McFrPlayer.getMcFrPlayer(p).toggleWalking();
      
      if (McFrPlayer.getMcFrPlayer(p).isWalking()) {
        Sponge.getCommandManager().process(p, String.format("speed walk 0.13"));
      } else {
        Sponge.getCommandManager().process(p, String.format("speed walk 0.2"));
      }
    }
    else {
      src.sendMessage(ONLY_PLAYERS_COMMAND);
    }
    
    return CommandResult.success();
  }
  
  @Override
  public CommandSpec getCommandSpec() {
    //#f:0
    return CommandSpec.builder()
            .description(Text.of("Change votre vitesse de marche."))
            .permission("essentials.command.walk")
            .executor(this)
            .build();
    //#f:1
  }
  
  @Override
  public String[] getAliases() {
    return new String[] { "walk" };
  }
}
