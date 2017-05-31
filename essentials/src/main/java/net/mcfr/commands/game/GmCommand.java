package net.mcfr.commands.game;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.text.Text;

import net.mcfr.Essentials;
import net.mcfr.commands.utils.AbstractCommand;

public class GmCommand extends AbstractCommand {
  
  public GmCommand(Essentials plugin) {
    super(plugin);
  }
  
  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    if (src instanceof Player) {
      Player p = (Player) src;
      Sponge.getCommandManager().process(p, String.format("gamemode %d %s", p.gameMode().get().equals(GameModes.SURVIVAL) ? 1 : 0, p.getName()));
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
            .description(Text.of("Change le mode de jeu du joueur."))
            .permission("essentials.command.gm")
            .executor(this)
            .build();
    //#f:1
  }
  
  @Override
  public String[] getAliases() {
    return new String[] { "gm" };
  }
}
