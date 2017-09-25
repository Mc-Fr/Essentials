package net.mcfr.commands.roleplay;

import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockTypes;
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
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.World;

import net.mcfr.Essentials;
import net.mcfr.commands.AbstractCommand;
import net.mcfr.locks.Lock;
import net.mcfr.locks.LocksService;
import net.mcfr.mecanisms.keys.McfrCodedItem;
import net.mcfr.roleplay.Skill;
import net.mcfr.utils.McFrPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class KeyCommand extends AbstractCommand {

  public static final ItemType KEY_ITEM = Sponge.getGame().getRegistry().getType(ItemType.class, "mcfr_b_i:key").get();
  public static final ItemType LOCK_ITEM = Sponge.getGame().getRegistry().getType(ItemType.class, "mcfr_b_i:lock").get();

  public KeyCommand(Essentials plugin) {
    super(plugin);
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    if (src instanceof Player) {
      Player player = (Player) src;
      int forgeLevel = McFrPlayer.getMcFrPlayer(player).getSkillLevel(Skill.getSkillByName("forge"), Optional.empty());
      int mecanicLevel = McFrPlayer.getMcFrPlayer(player).getSkillLevel(Skill.getSkillByName("mecanique"), Optional.empty());
      boolean codingResult;

      Optional<ItemStack> optStack = player.getItemInHand(HandTypes.MAIN_HAND);

      if (optStack.isPresent()) {
        ItemStack stack = optStack.get();

        int code = args.<Integer>getOne("code").get();
        int difficulty = 2;

        while (code / Math.pow(10, difficulty) >= 1) {
          difficulty++;
        }

        if (code <= 0) {
          src.sendMessage(Text.of(TextColors.YELLOW, "Le code doit être strictement supérieur à zéro."));
          return CommandResult.success();
        } else if (difficulty > 4) {
          src.sendMessage(Text.of(TextColors.YELLOW, "Le code doit comporter 4 chiffres au maximum."));
          return CommandResult.success();
        }

        if (stack.getItem() == KEY_ITEM) {
          if (difficulty == 2 && forgeLevel >= 12) {
            codingResult = true;
          } else if (difficulty == 3 && forgeLevel >= 14) {
            codingResult = true;
          } else if (difficulty == 4 && mecanicLevel >= 14) {
            codingResult = true;
          } else {
            codingResult = false;
          }

          if (codingResult)
            if (((McfrCodedItem) stack.getItem()).setCode((EntityPlayerMP) player, code))
              src.sendMessage(Text.of(TextColors.YELLOW, "La clé a été codée : " + code));
            else
              src.sendMessage(Text.of(TextColors.YELLOW, "La clé est cassée ou déjà codée."));
          else
            src.sendMessage(Text.of(TextColors.YELLOW,
                "Vous n'avez pas le niveau nécessaire en forge ou en mécanique pour forger une clé de complexité " + difficulty + "."));

          return CommandResult.success();
        } else if (stack.getItem() == LOCK_ITEM) {
          if (difficulty == 2 && mecanicLevel >= 11) {
            codingResult = true;
          } else if (difficulty == 3 && mecanicLevel >= 13) {
            codingResult = true;
          } else if (difficulty == 4 && mecanicLevel >= 15) {
            codingResult = true;
          } else {
            codingResult = false;
          }

          if (codingResult)
            if (((McfrCodedItem) stack.getItem()).setCode((EntityPlayerMP) player, code))
              src.sendMessage(Text.of(TextColors.YELLOW, "La serrure a été codée : " + code));
            else
              src.sendMessage(Text.of(TextColors.YELLOW, "La serrure est cassée ou déjà codée."));
          else
            src.sendMessage(Text.of(TextColors.YELLOW,
                "Vous n'avez pas le niveau nécessaire en mécanique pour forger une serrure de complexité " + difficulty + "."));

          return CommandResult.success();
        }
      }

      src.sendMessage(Text.of(TextColors.YELLOW, "Vous devez tenir une clé ou une serrure vierge en main."));
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
            .children(getChildrenList(new Read(getPlugin())))
            .executor(this)
            .build();
    //#f:1
  }

  @Override
  public String[] getAliases() {
    return new String[] { "key" };
  }

  static class Read extends AbstractCommand {

    public Read(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      if (src instanceof Player) {
        Player player = (Player) src;
        int forgeLevel = McFrPlayer.getMcFrPlayer(player).getSkillLevel(Skill.getSkillByName("forge"), Optional.empty());
        int mecanicLevel = McFrPlayer.getMcFrPlayer(player).getSkillLevel(Skill.getSkillByName("mecanique"), Optional.empty());

        Optional<ItemStack> optStack = player.getItemInHand(HandTypes.MAIN_HAND);
        BlockRay<World> ray = BlockRay.from(player).distanceLimit(3).build();
        Optional<LocksService> service = Sponge.getServiceManager().provide(LocksService.class);
        Optional<Lock> lookedLock = Optional.empty();
        
        while (ray.hasNext()) {
          BlockRayHit<World> hit = ray.next();
          if (hit.getExtent().getBlock(hit.getBlockPosition()).getType() != BlockTypes.AIR) {
            if (service.isPresent()) {
              lookedLock = service.get().getLock(hit.getBlockPosition(), hit.getExtent());
            }
            break;
          }
        }

        if (optStack.isPresent()) {
          ItemStack stack = optStack.get();
          Optional<Integer> optCode = ((McfrCodedItem) stack.getItem()).getCode((EntityPlayerMP) player);

          if (!optCode.isPresent()) {
            src.sendMessage(Text.of(TextColors.YELLOW, "La clé ou la serrure tenue en main est vierge ou cassée."));
            return CommandResult.success();
          }

          int code = optCode.get();
          int difficulty = 2;

          while (code / Math.pow(10, difficulty) >= 1) {
            difficulty++;
          }

          if (stack.getItem() == KEY_ITEM) {
            if (difficulty == 2 && (forgeLevel >= 12 || mecanicLevel >= 10)) {
              src.sendMessage(Text.of(TextColors.YELLOW, "Code de la clé : " + code));
            } else if (difficulty == 3 && (forgeLevel >= 14 || mecanicLevel >= 12)) {
              src.sendMessage(Text.of(TextColors.YELLOW, "Code de la clé : " + code));
            } else if (difficulty == 4 && mecanicLevel >= 14) {
              src.sendMessage(Text.of(TextColors.YELLOW, "Code de la clé : " + code));
            } else {
              src.sendMessage(
                  Text.of(TextColors.YELLOW, "Vous n'avez pas le niveau nécessaire en forge ou en mécanique pour découvrir le code de cette clé."));
            }
            return CommandResult.success();
          } else if (stack.getItem() == LOCK_ITEM) {
            if (difficulty == 2 && (forgeLevel >= 13 || mecanicLevel >= 11)) {
              src.sendMessage(Text.of(TextColors.YELLOW, "Code de la serrure : " + code));
            } else if (difficulty == 3 && (forgeLevel >= 15 || mecanicLevel >= 13)) {
              src.sendMessage(Text.of(TextColors.YELLOW, "Code de la serrure : " + code));
            } else if (difficulty == 4 && mecanicLevel >= 15) {
              src.sendMessage(Text.of(TextColors.YELLOW, "Code de la serrure : " + code));
            } else {
              src.sendMessage(Text.of(TextColors.YELLOW,
                  "Vous n'avez pas le niveau nécessaire en forge ou en mécanique pour découvrir le code de cette serrure."));
            }
            return CommandResult.success();
          }
        } else if (lookedLock.isPresent()) {
          int code = lookedLock.get().getCode();
          int difficulty = 2;

          while (code / Math.pow(10, difficulty) >= 1) {
            difficulty++;
          }

          if (difficulty == 2 && mecanicLevel >= 12) {
            src.sendMessage(Text.of(TextColors.YELLOW, "Code de la serrure : " + code));
          } else if (difficulty == 3 && mecanicLevel >= 14) {
            src.sendMessage(Text.of(TextColors.YELLOW, "Code de la serrure : " + code));
          } else if (difficulty == 4 && mecanicLevel >= 16) {
            src.sendMessage(Text.of(TextColors.YELLOW, "Code de la serrure : " + code));
          } else {
            src.sendMessage(
                Text.of(TextColors.YELLOW, "Vous n'avez pas le niveau nécessaire en mécanique pour découvrir le code de cette serrure."));
          }

          return CommandResult.success();
        }

        src.sendMessage(Text.of(TextColors.YELLOW, "Vous devez tenir une clé ou une serrure en main ou regarder une serrure déjà installée."));
      } else {
        src.sendMessage(ONLY_PLAYERS_COMMAND);
      }
      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      //#f:0
      return CommandSpec.builder()
              .description(Text.of("Permet de découvrir le code d'une clé ou d'une serrure."))
              .permission("essentials.command.keycode")
              .executor(this)
              .build();
      //#f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "read" };
    }
  }
}
