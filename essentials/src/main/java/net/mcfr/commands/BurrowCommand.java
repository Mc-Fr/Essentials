package net.mcfr.commands;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import net.mcfr.Essentials;
import net.mcfr.burrows.Burrow;
import net.mcfr.burrows.BurrowListener;
import net.mcfr.burrows.BurrowedEntityClasses;
import net.mcfr.commands.utils.AbstractCommand;
import net.mcfr.entities.mobs.gender.EntityGendered;
import net.mcfr.utils.McFrPlayer;

public class BurrowCommand extends AbstractCommand {
  public BurrowCommand(Essentials plugin) {
    super(plugin);
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    src.sendMessage(Text.of(TextColors.RED, "Merci de renseigner l'action que vous souhaitez effectuer."));
    return CommandResult.empty();
  }

  private static void sendPopulationMessage(CommandSource src, Burrow burrow) {
    src.sendMessage(Text.of(TextColors.YELLOW, "La population du terrier " + burrow.getName() + " est de " + burrow.getPopulation().getFemales()
        + " femelles et de " + burrow.getPopulation().getMales() + " mâles."));
  }

  private static Optional<Burrow> getSelectedBurrow(Player player) {
    return McFrPlayer.getMcFrPlayer(player).getSelectedBurrow();
  }

  @Override
  public CommandSpec getCommandSpec() {
    // #f:0
    return CommandSpec.builder()
        .description(Text.of("Commande de manipulation des terriers."))
        .permission("essentials.command.burrow")
        .executor(this)
        .children(getChildrenList(new Create(getPlugin()), 
            new Remove(getPlugin()), 
            new Load(getPlugin()), 
            new Select(getPlugin()), 
            new Unselect(getPlugin()),
            new Move(getPlugin()), 
            new Delay(getPlugin()),
            new Max(getPlugin()), 
            new Males(getPlugin()), 
            new Females(getPlugin()),
            new Reset(getPlugin()),
            new Info(getPlugin()),
            new Display(getPlugin()),
            new Tp(getPlugin()),
            new List(getPlugin())))
        .build();
    // #f:1
  }

  @Override
  public String[] getAliases() {
    return new String[] { "burrow" };
  }

  static class Create extends AbstractCommand {

    public Create(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      if (src instanceof Player) {
        Location<World> location = ((Player) src).getLocation();
        long delay = args.<Long>getOne("délai").get();
        int maxPopulation = args.<Integer>getOne("population maximale").get();
        int malePopulation = args.<Integer>getOne("mâles").get();
        int femalePopulation = args.<Integer>getOne("femelles").get();
        Class<? extends EntityGendered> entityClass = ((BurrowedEntityClasses) args.getOne("entité").get()).getEntityClass();
        Optional<String> name = args.<String>getOne("nom");

        Optional<Burrow> optBurrow = Burrow.createBurrow(name, location, delay, maxPopulation, malePopulation, femalePopulation, entityClass);

        if (optBurrow.isPresent()) {
          Burrow burrow = optBurrow.get();
          src.sendMessage(Text.of(TextColors.YELLOW, "Le terrier " + burrow.getName() + " a été créé à votre position et est sélectionné."));
          src.sendMessage(Text.of(TextColors.YELLOW, "Population maximale : " + burrow.getPopulation().getMax() + " - Mâles : "
              + burrow.getPopulation().getMales() + " - Femelles : " + burrow.getPopulation().getFemales()));
          src.sendMessage(Text.of(TextColors.YELLOW, "Délai : " + burrow.getFormatedDelay() + " - Créature : " + burrow.getEntityName()));

          McFrPlayer.getMcFrPlayer((Player) src).selectBurrow(burrow);
        }
      } else {
        src.sendMessage(ONLY_PLAYERS_COMMAND);
      }
      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder()
          .description(Text.of("Crée un terrier à l'emplacement du joueur."))
          .permission("essentials.command.burrow.create")
          .arguments(GenericArguments.integer(Text.of("population maximale")),
              GenericArguments.integer(Text.of("mâles")),
              GenericArguments.integer(Text.of("femelles")),
              GenericArguments.longNum(Text.of("délai")),
              GenericArguments.enumValue(Text.of("entité"), BurrowedEntityClasses.class),
              GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("nom"))))
          .executor(this)
          .build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "create" };
    }

  }

  static class Remove extends AbstractCommand {

    public Remove(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      if (src instanceof Player) {
        Player player = (Player) src;

        Optional<Burrow> burrowOpt = getSelectedBurrow(player);

        if (burrowOpt.isPresent()) {
          McFrPlayer.getMcFrPlayer(player).unselectBurrow();
          Burrow.removeBurrow(burrowOpt.get());
          src.sendMessage(Text.of(TextColors.YELLOW, "Le terrier " + burrowOpt.get().getName() + " a été détruit."));
        } else {
          src.sendMessage(Text.of(TextColors.YELLOW, "Aucun terrier n'est actuellement sélectionné."));
        }
      } else {
        src.sendMessage(ONLY_PLAYERS_COMMAND);
      }
      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder().description(Text.of("Détruit le terrier le plus proche, tuant les créatures au passage."))
          .permission("essentials.command.burrow.remove").arguments().executor(this).build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "remove" };
    }

  }

  static class Load extends AbstractCommand {
    private Essentials plugin;
    private boolean firstTimeCalled;

    public Load(Essentials plugin) {
      super(plugin);
      this.plugin = plugin;
      this.firstTimeCalled = true;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      
      BurrowListener.loadFromDatabase();
      String loadResult = Burrow.loadFromDatabase();

      if (this.firstTimeCalled) {
        Task.Builder taskBuilder = Sponge.getScheduler().createTaskBuilder();
        taskBuilder.execute(() -> Burrow.updateBurrows()).delay(10, TimeUnit.SECONDS).interval(10, TimeUnit.SECONDS).submit(this.plugin);
        this.firstTimeCalled = false;
      }

      src.sendMessage(Text.of(TextColors.YELLOW, loadResult));

      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder().description(Text.of("Charge les terriers."))
          .permission("essentials.command.burrow.load").arguments().executor(this).build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "load" };
    }
  }

  static class Select extends AbstractCommand {

    public Select(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

      if (src instanceof Player) {
        Optional<Burrow> burrowOpt = Optional.empty();
        if (args.hasAny("nom")) {
          burrowOpt = Burrow.getBurrowByName(args.<String>getOne("nom").get());
        } else {
          burrowOpt = Burrow.getNearestBurrow(((Player) src).getLocation());
        }

        if (burrowOpt.isPresent()) {
          Burrow burrow = burrowOpt.get();
          McFrPlayer.getMcFrPlayer((Player) src).selectBurrow(burrow);
          src.sendMessage(Text.of(TextColors.YELLOW, "Le terrier " + burrow.getName() + " a été sélectionné."));
        } else {
          src.sendMessage(Text.of(TextColors.YELLOW, "Aucun terrier n'a été trouvé aux alentours."));
        }
      } else {
        src.sendMessage(ONLY_PLAYERS_COMMAND);
      }

      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder().description(Text.of("Sélectionne le terrier le plus proche."))
          .permission("essentials.command.burrow.select")
          .arguments(GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("nom"))))
          .executor(this)
          .build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "select" };
    }
  }

  static class Unselect extends AbstractCommand {

    public Unselect(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

      if (src instanceof Player) {
        Optional<Burrow> burrowOpt = getSelectedBurrow((Player) src);
        if (burrowOpt.isPresent()) {
          McFrPlayer.getMcFrPlayer((Player) src).unselectBurrow();
          src.sendMessage(Text.of(TextColors.YELLOW, "Le terrier a été déselectionné."));
        } else {
          src.sendMessage(Text.of(TextColors.YELLOW, "Aucun terrier n'est actuellement sélectionné."));
        }
      } else {
        src.sendMessage(ONLY_PLAYERS_COMMAND);
      }

      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder().description(Text.of("Désélectionne le terrier sélectionné."))
          .permission("essentials.command.burrow.unselect").arguments().executor(this).build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "unselect" };
    }
  }

  static class Move extends AbstractCommand {

    public Move(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

      if (src instanceof Player) {
        Burrow burrow = getSelectedBurrow((Player) src).orElse(null);
        if (burrow != null) {
          burrow.setLocation(((Player) src).getLocation());
          burrow.reset();
          src.sendMessage(Text.of(TextColors.YELLOW, "Le terrier " + burrow.getName() + " a été déplacé à votre position."));
        } else {
          src.sendMessage(Text.of(TextColors.YELLOW, "Aucun terrier n'est actuellement sélectionné."));
        }
      } else {
        src.sendMessage(ONLY_PLAYERS_COMMAND);
      }

      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder().description(Text.of("Déplace le terrier sélectionné à l'emplacement du joueur."))
          .permission("essentials.command.burrow.move").arguments().executor(this).build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "move" };
    }
  }

  static class Delay extends AbstractCommand {

    public Delay(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

      if (src instanceof Player) {
        Burrow burrow = getSelectedBurrow((Player) src).orElse(null);
        if (burrow != null) {
          long delay = args.<Long>getOne("délai").get();
          burrow.setDelay(delay);
          src.sendMessage(
              Text.of(TextColors.YELLOW, "Le délai du terrier " + burrow.getName() + " a été réglé à " + burrow.getFormatedDelay() + "."));
        } else {
          src.sendMessage(Text.of(TextColors.YELLOW, "Aucun terrier n'est actuellement sélectionné."));
        }
      } else {
        src.sendMessage(ONLY_PLAYERS_COMMAND);
      }

      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder().description(Text.of("Change le délai du terrier sélectionné."))
          .permission("essentials.command.burrow.delay")
          .arguments(GenericArguments.longNum(Text.of("délai")))
          .executor(this)
          .build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "delay" };
    }
  }

  static class Max extends AbstractCommand {
    public Max(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

      if (src instanceof Player) {
        Burrow burrow = getSelectedBurrow((Player) src).orElse(null);
        if (burrow != null) {
          int max = args.<Integer>getOne("max").get();
          burrow.setMaximumPopulation(max);
          src.sendMessage(Text.of(TextColors.YELLOW, "La population maximale du terrier " + burrow.getName() + " a été réglée à " + max + "."));
        } else {
          src.sendMessage(Text.of(TextColors.YELLOW, "Aucun terrier n'est actuellement sélectionné."));
        }
      } else {
        src.sendMessage(ONLY_PLAYERS_COMMAND);
      }

      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder().description(Text.of("Change la population maximale du terrier sélectionné."))
          .permission("essentials.command.burrow.max")
          .arguments(GenericArguments.integer(Text.of("max")))
          .executor(this)
          .build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "max" };
    }
  }

  static class Males extends AbstractCommand {

    public Males(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

      if (src instanceof Player) {
        Burrow burrow = getSelectedBurrow((Player) src).orElse(null);
        if (burrow != null) {
          burrow.setMalePopulation(args.<Integer>getOne("mâles").get());
          sendPopulationMessage(src, burrow);
        } else {
          src.sendMessage(Text.of(TextColors.YELLOW, "Aucun terrier n'est actuellement sélectionné."));
        }
      } else {
        src.sendMessage(ONLY_PLAYERS_COMMAND);
      }

      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder().description(Text.of("Change la quantité de mâles dans le terrier."))
          .permission("essentials.command.burrow.males")
          .arguments(GenericArguments.integer(Text.of("mâles")))
          .executor(this)
          .build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "males" };
    }
  }

  static class Females extends AbstractCommand {

    public Females(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

      if (src instanceof Player) {
        Burrow burrow = getSelectedBurrow((Player) src).orElse(null);
        if (burrow != null) {
          burrow.setFemalePopulation(args.<Integer>getOne("femelles").get());
          sendPopulationMessage(src, burrow);
        } else {
          src.sendMessage(Text.of(TextColors.YELLOW, "Aucun terrier n'est actuellement sélectionné."));
        }
      } else {
        src.sendMessage(ONLY_PLAYERS_COMMAND);
      }

      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder().description(Text.of("Change la quantité de femelles dans le terrier."))
          .permission("essentials.command.burrow.females")
          .arguments(GenericArguments.integer(Text.of("femelles")))
          .executor(this)
          .build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "females" };
    }
  }

  static class Reset extends AbstractCommand {

    public Reset(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

      if (src instanceof Player) {
        Burrow burrow = getSelectedBurrow((Player) src).orElse(null);
        if (burrow != null) {
          burrow.reset();
          src.sendMessage(Text.of(TextColors.YELLOW, "Les créatures du terrier sont réapparues."));
        } else {
          src.sendMessage(Text.of(TextColors.YELLOW, "Aucun terrier n'est actuellement sélectionné."));
        }
      } else {
        src.sendMessage(ONLY_PLAYERS_COMMAND);
      }

      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder().description(Text.of("Tue puis fait réapparaître toutes les créatures du terrier."))
          .permission("essentials.command.burrow.reset")
          .arguments()
          .executor(this)
          .build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "reset" };
    }
  }

  static class Info extends AbstractCommand {

    public Info(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

      if (src instanceof Player) {
        Burrow burrow = getSelectedBurrow((Player) src).orElse(null);
        if (burrow != null) {
          src.sendMessage(Text.of(TextColors.YELLOW, "Infos sur le terrier : " + burrow.getName()));
          src.sendMessage(Text.of(TextColors.YELLOW, "Population maximale : " + burrow.getPopulation().getMax() + " - Mâles : "
              + burrow.getPopulation().getMales() + " - Femelles : " + burrow.getPopulation().getFemales()));
          src.sendMessage(Text.of(TextColors.YELLOW, "Délai : " + burrow.getFormatedDelay() + " - Créature : " + burrow.getEntityName()));
        } else {
          src.sendMessage(Text.of(TextColors.YELLOW, "Aucun terrier n'est actuellement sélectionné."));
        }
      } else {
        src.sendMessage(ONLY_PLAYERS_COMMAND);
      }

      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder().description(Text.of("Donne les informations du terrier sélectionné."))
          .permission("essentials.command.burrow.info")
          .arguments()
          .executor(this)
          .build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "info" };
    }
  }

  static class Display extends AbstractCommand {

    public Display(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      if (src instanceof Player) {
        Player player = (Player) src;
        McFrPlayer mcFrPlayer = McFrPlayer.getMcFrPlayer(player);

        mcFrPlayer.toggleSeesBurrows();
        if (mcFrPlayer.seesBurrows()) {
          Burrow.setAllVisible(player);
          src.sendMessage(Text.of(TextColors.YELLOW, "Les terriers sont désormais visibles pour vous."));
        } else {
          Burrow.setAllInvisible(player);
          src.sendMessage(Text.of(TextColors.YELLOW, "Les terriers sont désormais cachés pour vous."));
        }
      } else {
        src.sendMessage(ONLY_PLAYERS_COMMAND);
      }

      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder().description(Text.of("Affiche ou cache les terriers pour le joueur."))
          .permission("essentials.command.burrow.display")
          .arguments()
          .executor(this)
          .build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "display" };
    }
  }

  static class Tp extends AbstractCommand {

    public Tp(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

      if (src instanceof Player) {
        Burrow burrow = getSelectedBurrow((Player) src).orElse(null);

        if (burrow != null) {
          Sponge.getCommandManager().process(Sponge.getServer().getConsole(),
              String.format("tp " + ((Player) src).getName() + " " + burrow.getFormatedPosition()));
          src.sendMessage(Text.of(TextColors.YELLOW, "Vous avez été téléporté au terrier " + burrow.getName() + "."));
        } else {
          src.sendMessage(Text.of(TextColors.YELLOW, "Aucun terrier n'est actuellement sélectionné."));
        }
      } else {
        src.sendMessage(ONLY_PLAYERS_COMMAND);
      }

      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder().description(Text.of("Téléporte le joueur sur le terrier sélectionné."))
          .permission("essentials.command.burrow.tp")
          .arguments()
          .executor(this)
          .build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "tp" };
    }
  }

  static class List extends AbstractCommand {
    public List(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      java.util.List<Burrow> burrows = Burrow.getAll();
      java.util.List<Text> texts = new ArrayList<>(burrows.size());
      burrows.forEach(b -> texts.add(Text.of(TextColors.YELLOW,
          "- " + b.getName() + " : " + (b.getPopulation().getFemales() + b.getPopulation().getMales()) + " " + b.getEntityName())));

      PaginationService paginationService = Sponge.getServiceManager().provide(PaginationService.class).get();
      paginationService.builder().title(Text.of(TextColors.YELLOW, "Terriers")).linesPerPage(10).padding(Text.of(TextColors.GOLD, "="))
          .contents(texts).sendTo(src);

      return CommandResult.success();
    }

    @Override
    public CommandSpec getCommandSpec() {
    // #f:0
      return CommandSpec.builder().description(Text.of("Donne la liste des terriers existants."))
          .permission("essentials.command.burrow.list")
          .arguments()
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
