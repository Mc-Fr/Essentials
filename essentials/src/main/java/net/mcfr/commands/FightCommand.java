package net.mcfr.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import net.mcfr.Essentials;
import net.mcfr.commands.utils.AbstractCommand;

public class FightCommand extends AbstractCommand {

  public FightCommand(Essentials plugin) {
    super(plugin);
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    return null;
  }

  @Override
  public CommandSpec getCommandSpec() {
  //#f:0
    return CommandSpec.builder()
            .description(Text.of("Commande du système de combat tour par tour."))
            .permission("essentials.command.fight")
            .executor(this)
            .children(getChildrenList(new Create(getPlugin()),
                new Stop(getPlugin()),
                new Join(getPlugin()),
                new Start(getPlugin()),
                new Leave(getPlugin()),
                new Spectate(getPlugin()),
                new Display(getPlugin()),
                new Next(getPlugin()),
                new Skip(getPlugin()),
                new Interrupt(getPlugin()),
                new Resume(getPlugin()),
                new Lead(getPlugin()),
                new NPF(getPlugin()),
                new Kick(getPlugin()),
                new Ban(getPlugin()),
                new Move(getPlugin()),
                new Turn(getPlugin()),
                new Help(getPlugin())))
            .build();
    //#f:1
  }

  @Override
  public String[] getAliases() {
    return new String[] { "fight", "f" };
  }

  static class Create extends AbstractCommand {

    public Create(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      return null;
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder()
          .description(Text.of("Crée une nouvelle instance de combat en tour par tour."))
          .permission("essentials.command.fight.create")
          .arguments(GenericArguments.string(Text.of("nom")))
          .executor(this)
          .build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "create" };
    }
  }

  static class Stop extends AbstractCommand {

    public Stop(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      return null;
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder()
          .description(Text.of("Ferme le combat (confirmation demandée)."))
          .permission("essentials.command.fight.stop")
          .executor(this)
          .build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "stop" };
    }
  }

  static class Join extends AbstractCommand {

    public Join(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      return null;
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder()
          .description(Text.of("Permet de rejoindre le combat spécifié."))
          .permission("essentials.command.fight.join")
          .arguments(GenericArguments.string(Text.of("nom")))
          .executor(this)
          .build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "join" };
    }
  }

  static class Start extends AbstractCommand {

    public Start(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      return null;
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder()
          .description(Text.of("Démarre le combat."))
          .permission("essentials.command.fight.start")
          .executor(this)
          .build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "start" };
    }
  }

  static class Leave extends AbstractCommand {

    public Leave(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      return null;
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder()
          .description(Text.of("Permet de quitter le combat en cours (confirmation demandée)."))
          .permission("essentials.command.fight.leave")
          .executor(this)
          .build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "leave" };
    }
  }

  static class Spectate extends AbstractCommand {

    public Spectate(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      return null;
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder()
          .description(Text.of("Permet de passer en mode spectateur ou de le quitter."))
          .permission("essentials.command.fight.spectate")
          .executor(this)
          .build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "spectate" };
    }
  }

  static class Display extends AbstractCommand {

    public Display(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      return null;
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder()
          .description(Text.of("Affiche les informations MJ."))
          .permission("essentials.command.fight.display")
          .executor(this)
          .build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "display" };
    }
  }

  static class Next extends AbstractCommand {

    public Next(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      return null;
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder()
          .description(Text.of("Met fin au tour en cours."))
          .permission("essentials.command.fight.next")
          .executor(this)
          .build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "next" };
    }
  }

  static class Skip extends AbstractCommand {

    public Skip(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      return null;
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder()
          .description(Text.of("Met votre tour en attente (reprendre avec /f resume ou /f interrupt)."))
          .permission("essentials.command.fight.wait")
          .executor(this)
          .build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "wait" };
    }
  }

  static class Interrupt extends AbstractCommand {

    public Interrupt(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      return null;
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder()
          .description(Text.of("Interrompt le tour en cours et entame votre tour."))
          .permission("essentials.command.fight.interrupt")
          .executor(this)
          .build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "interrupt" };
    }
  }

  static class Resume extends AbstractCommand {

    public Resume(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      return null;
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder()
          .description(Text.of("Reprend votre tour après le tour en cours."))
          .permission("essentials.command.fight.resume")
          .executor(this)
          .build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "resume" };
    }
  }

  static class Lead extends AbstractCommand {

    public Lead(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      return null;
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder()
          .description(Text.of("Donne la maîtrise du combat à un autre joueur."))
          .permission("essentials.command.fight.lead")
          .arguments(GenericArguments.player(Text.of("joueur")))
          .executor(this)
          .build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "lead" };
    }
  }

  static class NPF extends AbstractCommand {

    public NPF(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      return null;
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder()
          .description(Text.of("Permet de gérer les combattants fictifs."))
          .permission("essentials.command.fight.npf")
          .executor(this)
          .build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "npf" };
    }
  }

  static class Kick extends AbstractCommand {

    public Kick(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      return null;
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder()
          .description(Text.of("Sors un joueur du combat (confirmation demandée)."))
          .permission("essentials.command.fight.kick")
          .arguments(GenericArguments.player(Text.of("joueur")))
          .executor(this)
          .build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "kick" };
    }
  }

  static class Ban extends AbstractCommand {

    public Ban(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      return null;
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder()
          .description(Text.of("Bannis un joueur de ce combat (confirmation demandée)."))
          .permission("essentials.command.fight.ban")
          .arguments(GenericArguments.player(Text.of("joueur")))
          .executor(this)
          .build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "ban" };
    }
  }

  static class Move extends AbstractCommand {

    public Move(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      return null;
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder()
          .description(Text.of("Déplace le tour d'un joueur."))
          .permission("essentials.command.fight.move")
          .arguments()
          .executor(this)
          .build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "move" };
    }
  }

  static class Turn extends AbstractCommand {

    public Turn(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      return null;
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder()
          .description(Text.of("Passe le tour à un autre joueur."))
          .permission("essentials.command.fight.turn")
          .arguments()
          .executor(this)
          .build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "turn" };
    }
  }

  static class Help extends AbstractCommand {

    public Help(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      return null;
    }

    @Override
    public CommandSpec getCommandSpec() {
      // #f:0
      return CommandSpec.builder()
          .description(Text.of("Affiche l'aide du système de combat tour par tour."))
          .permission("essentials.command.fight.help")
          .executor(this)
          .build();
      // #f:1
    }

    @Override
    public String[] getAliases() {
      return new String[] { "help" };
    }
  }
}