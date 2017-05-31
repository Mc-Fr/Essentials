package net.mcfr.commands;

import java.util.ArrayList;
import java.util.List;
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

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    Optional<HarvestService> optHarvestService = Sponge.getServiceManager().provide(HarvestService.class);

    if (!optHarvestService.isPresent()) {
      src.sendMessage(Text.of(TextColors.RED, "Le système de récolte n'a pas été correctement initialisé."));
      return CommandResult.success();
    }

    HarvestService harvest = optHarvestService.get();

    if (args.hasAny("joueur")) {
      McFrPlayer p = McFrPlayer.getMcFrPlayer(args.<Player>getOne("joueur").get());

      java.util.List<Text> texts = new ArrayList<>();

      List<HarvestArea> areas = harvest.getAreasForPlayer(p);

      //#f:0
      texts.add(Text.join(Text.of(TextColors.GREEN, "Vous avez "),
          Text.of(TextColors.WHITE, p.getHarvestTokens()),
          Text.of(TextColors.GREEN, " jetons de récolte. Votre jeton actuel vaut "),
          Text.of(TextColors.WHITE, p.getTokenValue()),
          Text.of(TextColors.GREEN, "% d'une récolte.")));
      
      areas.forEach(a -> texts.add(
              Text.builder()
              .append(Text.join(Text.of(TextColors.GREEN, "Récolte de : "),
                  Text.of(TextColors.WHITE, a.getName()),
                  Text.of(TextColors.GREEN, " : \"")))
              .onClick(TextActions.executeCallback((cmdSrc) -> harvest.askForHarvest(p, a)))
              .build()));
      //#f:1

      PaginationService paginationService = Sponge.getServiceManager().provide(PaginationService.class).get();
      paginationService.builder().title(Text.of(TextColors.GREEN, "Zones de récolte"))
          .footer(Text.of(TextColors.GREEN, "Cliquez sur une ressource pour la récolter")).linesPerPage(15)
          .padding(Text.of(TextColors.DARK_GREEN, "=")).contents(texts).sendTo(src);

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
}
