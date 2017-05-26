package net.mcfr.commands;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
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

      Sponge.getCommandManager().process(Sponge.getServer().getConsole(), "give " + p.getName() + " mcfr_b_i:orp_sign " + quantity);
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
            .children(getChildrenList(new Food(getPlugin())))
            .build();
    //#f:1
  }

  @Override
  public String[] getAliases() {
    return new String[] { "hrp" };
  }
  
  static class Food extends AbstractCommand {

    public Food(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      if (src instanceof Player) {
        Player p = (Player) src;
        
        ItemStack orpFoodStack = ItemStack.builder()
            .itemType(Sponge.getGame().getRegistry().getType(ItemType.class, "mcfr_b_i:orp_food").get()).quantity(4).build();
        
        List<Text> text = new ArrayList<>();
        text.add(Text.of("Cette nourriture est HRP, pensez à faire manger de la vraie nourriture à votre personnage de temps en temps !"));
        orpFoodStack.offer(Keys.ITEM_LORE, text);
        
        p.getInventory().offer(orpFoodStack);
      } else {
        src.sendMessage(ONLY_PLAYERS_COMMAND);
      }
      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      //#f:0
      return CommandSpec.builder()
              .description(Text.of("Fournit au joueur 16 items de nourriture HRP."))
              .permission("essentials.command.hrp.food")
              .executor(this)
              .build();
      //#f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "food" };
    }

  }
}