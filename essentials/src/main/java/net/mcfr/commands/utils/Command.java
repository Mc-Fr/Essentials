package net.mcfr.commands.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

import net.mcfr.Essentials;
import net.mcfr.commands.chat.MpCommand;
import net.mcfr.commands.chat.MuteCommand;
import net.mcfr.commands.chat.NoCommand;
import net.mcfr.commands.chat.RangeCommand;
import net.mcfr.commands.chat.RealnameCommand;
import net.mcfr.commands.chat.ReplyCommand;
import net.mcfr.commands.chat.SpyMpCommand;
import net.mcfr.commands.game.FlyCommand;
import net.mcfr.commands.game.GmCommand;
import net.mcfr.commands.game.GodCommand;
import net.mcfr.commands.game.HealCommand;
import net.mcfr.commands.game.ServerLockCommand;
import net.mcfr.commands.game.SpectateCommand;
import net.mcfr.commands.game.SpeedCommand;
import net.mcfr.commands.game.VanishCommand;
import net.mcfr.commands.roleplay.BabelCommand;
import net.mcfr.commands.roleplay.DescCommand;
import net.mcfr.commands.roleplay.HealthCommand;
import net.mcfr.commands.roleplay.HrpCommand;
import net.mcfr.commands.roleplay.ItemCommand;
import net.mcfr.commands.roleplay.ManaCommand;
import net.mcfr.commands.roleplay.NameCommand;
import net.mcfr.commands.roleplay.RefreshCommand;
import net.mcfr.commands.roleplay.RollCommand;
import net.mcfr.commands.roleplay.WalkCommand;
import net.mcfr.commands.roleplay.WhoIsCommand;
import net.mcfr.commands.systems.BurrowCommand;
import net.mcfr.commands.systems.CareCenterCommand;
import net.mcfr.commands.systems.ExpeditionCommand;
import net.mcfr.commands.systems.MoveNpcCommand;
import net.mcfr.commands.systems.TribalLanguageCommand;
import net.mcfr.commands.tp.BackCommand;
import net.mcfr.commands.tp.SpawnCommand;
import net.mcfr.commands.tp.TpCommand;
import net.mcfr.commands.tp.TpHereCommand;
import net.mcfr.commands.tp.TpPosCommand;
import net.mcfr.commands.tp.TpToCommand;
import net.mcfr.commands.tp.WarpCommand;

// TODO Trouver un moyen de refactorer ça. C'est dégueu.
public enum Command {
  BABEL(BabelCommand.class),
  BACK(BackCommand.class),
  BURROW(BurrowCommand.class),
  CARE_CENTER(CareCenterCommand.class),
  DESC(DescCommand.class),
  EXPEDITION(ExpeditionCommand.class),
  FLY(FlyCommand.class),
  GM(GmCommand.class),
  GOD(GodCommand.class),
  HEAL(HealCommand.class),
  HEALTH(HealthCommand.class),
  HRP(HrpCommand.class),
  ITEM(ItemCommand.class),
  MANA(ManaCommand.class),
  MOVENPC(MoveNpcCommand.class),
  MP(MpCommand.class),
  MUTE(MuteCommand.class),
  NAME(NameCommand.class),
  NO(NoCommand.class),
  RANGE(RangeCommand.class),
  REALNAME(RealnameCommand.class),
  REFRESH(RefreshCommand.class),
  REPLY(ReplyCommand.class),
  ROLL(RollCommand.class),
  SERVERLOCK(ServerLockCommand.class),
  SPAWN(SpawnCommand.class),
  SPECTATE(SpectateCommand.class),
  SPEED(SpeedCommand.class),
  SPYMP(SpyMpCommand.class),
  TP(TpCommand.class),
  TPH(TpHereCommand.class),
  TPPOS(TpPosCommand.class),
  TPTO(TpToCommand.class),
  TRIBELANG(TribalLanguageCommand.class),
  VANISH(VanishCommand.class),
  WALK(WalkCommand.class),
  WARP(WarpCommand.class),
  WHOIS(WhoIsCommand.class);

  private Class<? extends AbstractCommand> cmdClass;

  private Command(Class<? extends AbstractCommand> cmdClass) {
    this.cmdClass = cmdClass;
  }

  public Class<? extends AbstractCommand> getCommandClass() {
    return this.cmdClass;
  }

  public Optional<AbstractCommand> createCommand(Essentials plugin) {
    try {
      return Optional.of(getCommandClass().getConstructor(Essentials.class).newInstance(plugin));
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
        | SecurityException e) {
      e.printStackTrace();
    }
    return Optional.empty();
  }

}