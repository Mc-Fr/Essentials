package net.mcfr.commands.tp;

import java.util.Optional;

import org.spongepowered.api.Sponge;
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
import org.spongepowered.api.world.storage.WorldProperties;

import net.mcfr.Essentials;
import net.mcfr.commands.AbstractCommand;

public class TpPosCommand extends AbstractCommand {

  public TpPosCommand(Essentials plugin) {
    super(plugin);
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    Location<World> loc = args.<Location<World>>getOne("position").get();
    WorldProperties worldOpt = args.<WorldProperties>getOne("monde").get();
    Optional<Player> optP = args.<Player>getOne("joueur");
    Player p = null;
    if (src instanceof Player) {
      p = (Player) src;
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
    p.setLocation(new Location<>(Sponge.getServer().getWorld(worldOpt.getWorldName()).get(), loc.getPosition()));
    return CommandResult.success();
  }

  @Override
  public CommandSpec getCommandSpec() {
    //#f:0
    return CommandSpec.builder()
            .description(Text.of("Téléporte le joueur ciblé sur une position."))
            .permission("essentials.command.tppos")
            .arguments(GenericArguments.world(Text.of("monde")), GenericArguments.location(Text.of("position")), GenericArguments.optional(GenericArguments.player(Text.of("joueur"))))
            .executor(this)
            .build();
    //#f:1
  }

  @Override
  public String[] getAliases() {
    return new String[] { "tppos" };
  }
}