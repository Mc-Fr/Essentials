package net.mcfr.commands;

import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import net.mcfr.Essentials;
import net.mcfr.commands.utils.AbstractCommand;

public class TpPosCommand extends AbstractCommand {

  public TpPosCommand(Essentials plugin) {
    super(plugin);
    // TODO Auto-generated constructor stub
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    Location<World> loc = args.<Location<World>>getOne("position").get();
    Optional<Player> optP = args.<Player>getOne("joueur");
    Player p = (Player) src;
    if (src instanceof Player) {
      if (optP.isPresent()) {
        p = optP.get();
      }
    } else {
      if (!optP.isPresent()) {
        src.sendMessage(ONLY_PLAYERS_COMMAND);
        return CommandResult.empty();
      }
      p = optP.get();
    }
    p.setLocation(loc);
    return CommandResult.success();
  }

  @Override
  public CommandSpec getCommandSpec() {
    //#f:0
    return CommandSpec.builder()
            .description(Text.of("Téléporte le joueur ciblé sur une position."))
            .permission("essentials.command.tppos")
            .arguments(GenericArguments.location(Text.of("position")), GenericArguments.optional(GenericArguments.player(Text.of("joueur"))))
            .executor(this)
            .build();
    //#f:1
  }

  @Override
  public String[] getAliases() {
    return new String[] { "tppos" };
  }

}
