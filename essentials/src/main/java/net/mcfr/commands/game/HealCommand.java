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

public class HealCommand extends AbstractCommand {
  
  public HealCommand(Essentials plugin) {
    super(plugin);
  }
  
  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    if (args.hasAny("joueur")) {
      Player p = args.<Player> getOne("joueur").get();
      heal(p);
      src.sendMessage(Text.of(TextColors.YELLOW, "Les jauges de " + p.getName() + " ont été restaurées."));
    }
    else if (src instanceof Player) {
      heal((Player) src);
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
            .description(Text.of("Restaure les jauges de besoin du joueur."))
            .permission("essentials.command.heal")
            .executor(this)
            .arguments(GenericArguments.optional(GenericArguments.player(Text.of("joueur"))))
            .build();
    //#f:1
  }
  
  @Override
  public String[] getAliases() {
    return new String[] { "heal", "hl" };
  }
  
  private void heal(Player p) {
    p.offer(Keys.HEALTH, p.getHealthData().health().getMaxValue());
    p.offer(Keys.SATURATION, p.getFoodData().saturation().getMaxValue());
    p.offer(Keys.FOOD_LEVEL, p.getFoodData().foodLevel().getMaxValue());
    p.sendMessage(Text.of(TextColors.YELLOW, "Vos jauges de besoin ont été restaurées."));
  }
  
}
