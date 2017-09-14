package net.mcfr.commands;

import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import net.mcfr.Essentials;
import net.mcfr.commands.utils.AbstractCommand;
import net.mcfr.mecanisms.keys.McfrCodedItem;
import net.minecraft.entity.player.EntityPlayerMP;

public class KeyCodeCommand extends AbstractCommand {
  
  private final ItemType KEY_ITEM = Sponge.getGame().getRegistry().getType(ItemType.class, "mcfr_b_i:key").get();
  private final ItemType LOCK_ITEM = Sponge.getGame().getRegistry().getType(ItemType.class, "mcfr_b_i:lock").get();
  
  public KeyCodeCommand(Essentials plugin) {
    super(plugin);
  }
  
  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    if (src instanceof Player) {
      Player player = (Player) src;
      Optional<ItemStack> optStack = player.getItemInHand(HandTypes.MAIN_HAND);
      
      if (optStack.isPresent()) {
        ItemStack stack = optStack.get();
        
        if (stack.getItem() == KEY_ITEM || stack.getItem() == LOCK_ITEM) {
          src.sendMessage(Text.of("Clé ou serrure détectée."));

          ((McfrCodedItem)stack.getItem()).setCode((EntityPlayerMP) player, args.<Integer>getOne("code").get());
        }
      } else {
        src.sendMessage(Text.of(TextColors.RED, "Vous devez tenir une clé ou une serrure en main."));
      }
    } else {
      src.sendMessage(ONLY_PLAYERS_COMMAND);
    }
    return CommandResult.success();
  }
  
  @Override
  public CommandSpec getCommandSpec() {
    //#f:0
    return CommandSpec.builder()
            .description(Text.of("Paramètre un code sur une clé ou une serrure vierge."))
            .permission("essentials.command.keycode")
            .arguments(GenericArguments.integer(Text.of("code")))
            .executor(this)
            .build();
    //#f:1
  }
  
  @Override
  public String[] getAliases() {
    return new String[] { "key" };
  }
}
