package net.mcfr.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import net.mcfr.Essentials;
import net.mcfr.commands.utils.AbstractCommand;

public class HrpCommand extends AbstractCommand {

  public HrpCommand(Essentials plugin) {
    super(plugin);
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    if (src instanceof Player) {
      Player p = (Player) src;

      int quantity = 1;
      if (args.hasAny("quantité")) {
        quantity = Math.min(args.<Integer>getOne("quantité").get(), 16);
      }
      ItemStack nrpSign = ItemStack.builder().itemType(ItemTypes.SIGN).quantity(quantity).build();
      p.getInventory().offer(nrpSign);
    } else {
      src.sendMessage(ONLY_PLAYERS_COMMAND);
    }
    return CommandResult.success();
  }

  @Override
  public CommandSpec getCommandSpec() {
    //#f:0
    return CommandSpec.builder()
            .description(Text.of("Fournit au joueur des panneaux hrp."))
            .permission("essentials.command.hrp")
            .executor(this)
            .arguments(GenericArguments.optional(GenericArguments.integer(Text.of("quantité"))))
            .build();
    //#f:1
  }

  @Override
  public String[] getAliases() {
    return new String[] { "hrp" };
  }

}
