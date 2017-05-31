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

public class FlyCommand extends AbstractCommand {
  
  public FlyCommand(Essentials plugin) {
    super(plugin);
  }
  
  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    if (args.hasAny("joueur")) {
      toggleFly(args.<Player> getOne("joueur").get());
    }
    else if (src instanceof Player) {
      toggleFly((Player) src);
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
            .description(Text.of("Donne/retire le droit de voler à un ou plusieurs joueur."))
            .permission("essentials.command.fly")
            .executor(this)
            .arguments(GenericArguments.allOf(GenericArguments.player(Text.of("joueur"))))
            .build();
    //#f:1
  }
  
  @Override
  public String[] getAliases() {
    return new String[] { "fly" };
  }
  
  private void toggleFly(Player p) {
    if (p.get(Keys.CAN_FLY).get() && p.get(Keys.IS_FLYING).get()) {
      p.offer(Keys.IS_FLYING, false);
    }
    p.offer(Keys.CAN_FLY, !p.get(Keys.CAN_FLY).get());
    p.sendMessage(Text.of(TextColors.YELLOW, "Vol " + (p.get(Keys.CAN_FLY).get() ? "" : "dés") + "activé."));
  }
}
