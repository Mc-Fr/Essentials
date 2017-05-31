package net.mcfr.commands.game;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import net.mcfr.Essentials;
import net.mcfr.commands.AbstractCommand;

public class VanishCommand extends AbstractCommand {

  public VanishCommand(Essentials plugin) {
    super(plugin);
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    if (!(src instanceof Player)) {
      src.sendMessage(ONLY_PLAYERS_COMMAND);
      return CommandResult.empty();
    }
    Player player = (Player) src;
    boolean wasVisible = player.get(Keys.INVISIBLE).orElse(false);
    player.offer(Keys.INVISIBLE, !wasVisible);
    player.offer(Keys.VANISH_IGNORES_COLLISION, !wasVisible);
    player.offer(Keys.VANISH_PREVENTS_TARGETING, !wasVisible);

    player.sendMessage(Text.of(TextColors.YELLOW, "Vous êtes maintenant " + (wasVisible ? "" : "in") + "visible"));
    return CommandResult.success();
  }

  @Override
  public CommandSpec getCommandSpec() {
    //#f:0
    return CommandSpec.builder()
            .description(Text.of("Permet de se vanish."))
            .permission("essentials.command.vanish")
            .executor(this)
            .build();
    //#f:1
  }

  @Override
  public String[] getAliases() {
    return new String[] { "vanish", "v" };
  }

}
