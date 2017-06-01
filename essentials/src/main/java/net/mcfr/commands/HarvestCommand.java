package net.mcfr.commands;

import java.util.ArrayList;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import net.mcfr.Essentials;
import net.mcfr.commands.utils.AbstractCommand;
import net.mcfr.harvest.HarvestArea;
import net.mcfr.harvest.HarvestService;
import net.mcfr.utils.McFrPlayer;

public class HarvestCommand extends AbstractCommand {

  public HarvestCommand(Essentials plugin) {
    super(plugin);
  }
  
  public void askForHarvest(McFrPlayer p, HarvestArea area, HarvestService harvest) {
    //#f:0
    Text clickableText = Text.builder()
        .append(Text.of(TextColors.DARK_GREEN, ">> Confirmer <<"))
        .onClick(TextActions.executeCallback((cmdSrc) -> harvest.harvest(p, area)))
        .build();
    
    p.sendMessage(Text.join(Text.of(TextColors.GREEN, "Voulez-vous récolter : "),
        Text.of(TextColors.WHITE, area.getName()),
        Text.of(TextColors.GREEN, " ? "),
        clickableText));
    //f#1
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    Optional<HarvestService> optHarvestService = Sponge.getServiceManager().provide(HarvestService.class);

    if (!optHarvestService.isPresent()) {
      src.sendMessage(Text.of(TextColors.RED, "Le système de récolte n'a pas été correctement initialisé."));
      return CommandResult.success();
    }

    HarvestService harvest = optHarvestService.get();

    if (src instanceof Player) {
      McFrPlayer p = McFrPlayer.getMcFrPlayer((Player) src);

      java.util.List<Text> texts = new ArrayList<>();

      java.util.List<HarvestArea> areas = harvest.getAreasForPlayer(p);

      //#f:0
      texts.add(Text.join(Text.of(TextColors.GREEN, "Vous avez "),
          Text.of(TextColors.WHITE, p.getHarvestTokens()),
          Text.of(TextColors.GREEN, " jetons de récolte. Votre jeton actuel vaut "),
          Text.of(TextColors.WHITE, p.getTokenValue()),
          Text.of(TextColors.GREEN, "% d'une récolte.")));
      //TODO : changer le texte si 0 harvest tokens
      areas.forEach(a -> texts.add(
              Text.builder()
              .append(Text.join(Text.of(TextColors.DARK_GREEN, "Récolte de : "),
                  Text.of(TextColors.WHITE, a.getName())))
              .onClick(TextActions.executeCallback((cmdSrc) -> askForHarvest(p, a, harvest)))
              .build()));
      
      texts.add(Text.of(TextColors.GREEN, "Cliquez sur une ressource pour la récolter."));
      //#f:1

      PaginationService paginationService = Sponge.getServiceManager().provide(PaginationService.class).get();
      paginationService.builder().title(Text.of(TextColors.GREEN, "Zones de récolte")).linesPerPage(15).padding(Text.of(TextColors.DARK_GREEN, "="))
          .contents(texts).sendTo(src);

    } else {
      src.sendMessage(ONLY_PLAYERS_COMMAND);
    }
    return CommandResult.success();
  }

  @Override
  public CommandSpec getCommandSpec() {
    //#f:0
    return CommandSpec.builder()
            .description(Text.of("Propose la liste des zones de récolte avoisinnantes."))
            .permission("essentials.command.harvest")
            .executor(this)
            .build();
    //#f:1
  }

  @Override
  public String[] getAliases() {
    return new String[] { "harvest" };
  }

  /*
   * static class List extends AbstractCommand {
   * 
   * }
   * 
   * static class AddArea extends AbstractCommand {
   * 
   * }
   * 
   * static class RemoveArea extends AbstractCommand {
   * 
   * }
   * 
   * static class AddItemEntry extends AbstractCommand {
   * 
   * }
   * 
   * static class RemoveItemEntry extends AbstractCommand {
   * 
   * }
   * 
   * static class AddRareItemEntry extends AbstractCommand {
   * 
   * }
   * 
   * static class RemoveRareItemEntry extends AbstractCommand {
   * 
   * }
   */
}
