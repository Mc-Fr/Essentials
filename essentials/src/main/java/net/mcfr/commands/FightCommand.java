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
            .arguments(GenericArguments.allOf(GenericArguments.player(Text.of("joueur"))))
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
      return CommandSpec.builder().arguments(GenericArguments.string(Text.of("nom"))).build();
    }

    @Override
    public String[] getAliases() {
      return null;
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
      return null;
    }

    @Override
    public String[] getAliases() {
      return null;
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
      return null;
    }

    @Override
    public String[] getAliases() {
      return null;
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
      return null;
    }

    @Override
    public String[] getAliases() {
      return null;
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
      return null;
    }

    @Override
    public String[] getAliases() {
      return null;
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
      return null;
    }

    @Override
    public String[] getAliases() {
      return null;
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
      return null;
    }

    @Override
    public String[] getAliases() {
      return null;
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
      return null;
    }

    @Override
    public String[] getAliases() {
      return null;
    }
  }

  static class Wait extends AbstractCommand {

    public Wait(Essentials plugin) {
      super(plugin);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
      return null;
    }

    @Override
    public CommandSpec getCommandSpec() {
      return null;
    }

    @Override
    public String[] getAliases() {
      return null;
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
      return null;
    }

    @Override
    public String[] getAliases() {
      return null;
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
      return null;
    }

    @Override
    public String[] getAliases() {
      return null;
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
      return null;
    }

    @Override
    public String[] getAliases() {
      return null;
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
      return null;
    }

    @Override
    public String[] getAliases() {
      return null;
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
      return null;
    }

    @Override
    public String[] getAliases() {
      return null;
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
      return null;
    }

    @Override
    public String[] getAliases() {
      return null;
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
      return null;
    }

    @Override
    public String[] getAliases() {
      return null;
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
      return null;
    }

    @Override
    public String[] getAliases() {
      return null;
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
      return null;
    }

    @Override
    public String[] getAliases() {
      return null;
    }
  }
}