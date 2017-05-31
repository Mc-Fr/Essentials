package net.mcfr.commands.roleplay;

import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.manipulator.mutable.entity.MovementSpeedData;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import net.mcfr.Essentials;
import net.mcfr.commands.AbstractCommand;
import net.mcfr.utils.McFrPlayer;

public class WalkCommand extends AbstractCommand {

  public WalkCommand(Essentials plugin) {
    super(plugin);
  }
  
  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    if (src instanceof Player) {
      Player p = (Player) src;
      Optional<MovementSpeedData> optSpeedData = p.get(MovementSpeedData.class);
      
      if (optSpeedData.isPresent()) {
        McFrPlayer.getMcFrPlayer(p).toggleWalking();
        
        MovementSpeedData speedData = optSpeedData.get();
        Value<Double> walkSpeed = speedData.walkSpeed();
        
        if (McFrPlayer.getMcFrPlayer(p).isWalking()) {
          walkSpeed.set(0.065d);
          p.sendMessage(Text.of(TextColors.YELLOW, "Vitesse réduite."));
        } else {
          walkSpeed.set(0.1d);
          p.sendMessage(Text.of(TextColors.YELLOW, "Vitesse normale."));
        }
        
        speedData.set(walkSpeed);
        p.offer(speedData);
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
