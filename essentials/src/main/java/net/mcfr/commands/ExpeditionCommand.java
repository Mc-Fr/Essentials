package net.mcfr.commands;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import net.mcfr.Essentials;
import net.mcfr.commands.utils.AbstractCommand;
import net.mcfr.utils.McFrPlayer;

public class ExpeditionCommand extends AbstractCommand {

  public ExpeditionCommand(Essentials plugin) {
    super(plugin);
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    src.sendMessage(Text.of(TextColors.RED, "Merci de choisir l'une des actions suivantes : add, remove, list"));
    return CommandResult.empty();
  }

  @Override
  public CommandSpec getCommandSpec() {
    //#f:0
    return CommandSpec.builder()
            .description(Text.of("Gestion des joueurs autorisés à partir en expédition."))
            .permission("essentials.command.expedition")
            .executor(this)
            .children(getChildrenList(new Add(getPlugin()), 
                new Remove(getPlugin()),
                new List(getPlugin())))
            .build();
    //#f:1
  }

  @Override
  public String[] getAliases() {
    return new String[] { "expedition" };
  }
  
  static class Add extends AbstractCommand {

    public Add(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      if (args.hasAny("joueur")) {
        McFrPlayer player = McFrPlayer.getMcFrPlayer(args.<Player> getOne("joueur").get());
        if (!player.isAuthorizedToLeaveArea()) {
          player.toggleAuthorizedToLeaveArea();
          src.sendMessage(Text.of(TextColors.YELLOW, "Le joueur " + player.getName() + " peut à présent quitter la zone sécurisée."));
          player.getPlayer().sendMessage(Text.of(TextColors.YELLOW, "Un MJ vous a autorisé à quitter la zone sécurisée. Restez attentifs !"));
        } else {
          src.sendMessage(Text.of(TextColors.YELLOW, "Le joueur " + player.getName() + " est déjà autorisé à quitter la zone sécurisée."));
        }
      } else {
        src.sendMessage(Text.of(TextColors.RED, "Merci de renseigner les arguments : /expedition add <joueur>"));
      }
      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder()
          .description(Text.of("Autorise le joueur à quitter la zone sécurisée."))
          .permission("essentials.command.expedition.add")
          .arguments(GenericArguments.player(Text.of("joueur")))
          .executor(this)
          .build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "add" };
    }
  }
  
  static class Remove extends AbstractCommand {

    public Remove(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      if (args.hasAny("joueur")) {
        McFrPlayer player = McFrPlayer.getMcFrPlayer(args.<Player> getOne("joueur").get());
        if (player.isAuthorizedToLeaveArea()) {
          player.toggleAuthorizedToLeaveArea();
          src.sendMessage(Text.of(TextColors.YELLOW, "Le joueur " + player.getName() + " doit à présent rester dans la zone sécurisée."));
          player.getPlayer().sendMessage(Text.of(TextColors.YELLOW, "Vous devez à présent rester dans la zone sécurisée ! Ne vous éloignez pas trop."));
        } else {
          src.sendMessage(Text.of(TextColors.YELLOW, "Le joueur " + player.getName() + " doit déjà rester dans la zone sécurisée."));
        }
      } else {
        src.sendMessage(Text.of(TextColors.RED, "Merci de renseigner les arguments : /expedition remove <joueur>"));
      }
      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder()
          .description(Text.of("Interdit à un joueur de quitter la zone sécurisée."))
          .permission("essentials.command.expedition.remove")
          .arguments(GenericArguments.player(Text.of("joueur")))
          .executor(this)
          .build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "remove" };
    }
  }

  static class List extends AbstractCommand {
    private String message;

    public List(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      this.message = "Joueurs autorisés à quitter la zone sécurisée :";
      
      Sponge.getServer().getOnlinePlayers().forEach(p -> {
        if (McFrPlayer.getMcFrPlayer(p).isAuthorizedToLeaveArea()) {
          this.message += "\n- " + p.getName();
        }
      });
      
      src.sendMessage(Text.of(TextColors.YELLOW, message));
      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder()
          .description(Text.of("Donne la liste des joueurs autorisés à quitter la zone sécurisée."))
          .permission("essentials.command.expedition.list")
          .executor(this)
          .build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "list" };
    }
  }
}
