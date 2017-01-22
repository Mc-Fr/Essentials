package net.mcfr.commands;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import net.mcfr.Essentials;
import net.mcfr.commands.utils.AbstractCommand;

  //TODO : supprimer la commande après les tests : elle permet juste de donner l'effet coma pour tester

public class ComaCommand extends AbstractCommand {
  
  public ComaCommand(Essentials plugin) {
    super(plugin);
  }
  
  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    if (src instanceof Player) {
      Player p = (Player) src;
      
      PotionEffectData effects = p.getOrCreate(PotionEffectData.class).get();
      
      PotionEffectType potionType = Sponge.getGame().getRegistry().getAllOf(PotionEffectType.class).stream().filter(e -> e.getPotionTranslation().get().equals("effect.coma.postfix")).findAny().get();
      effects.addElement(PotionEffect.builder()
          .potionType(potionType)
          .duration(1000)
          .build());
      p.offer(effects);
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
            .description(Text.of("Met le joueur dans le coma."))
            .permission("essentials.command.coma")
            .executor(this)
            .build();
    //#f:1
  }
  
  @Override
  public String[] getAliases() {
    return new String[] { "coma" };
  }
}