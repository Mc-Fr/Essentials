package net.mcfr.commands.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

import net.mcfr.Essentials;
import net.mcfr.commands.BabelCommand;
import net.mcfr.commands.BackCommand;
import net.mcfr.commands.BurrowCommand;
import net.mcfr.commands.DescCommand;
import net.mcfr.commands.ExpeditionCommand;
import net.mcfr.commands.FatigueCommand;
import net.mcfr.commands.FlyCommand;
import net.mcfr.commands.GmCommand;
import net.mcfr.commands.GodCommand;
import net.mcfr.commands.HealCommand;
import net.mcfr.commands.HealthCommand;
import net.mcfr.commands.HrpCommand;
import net.mcfr.commands.ItemCommand;
import net.mcfr.commands.MoveNpcCommand;
import net.mcfr.commands.MpCommand;
import net.mcfr.commands.MuteCommand;
import net.mcfr.commands.NameCommand;
import net.mcfr.commands.NoCommand;
import net.mcfr.commands.RealnameCommand;
import net.mcfr.commands.RefreshCommand;
import net.mcfr.commands.ReplyCommand;
import net.mcfr.commands.RollCommand;
import net.mcfr.commands.ServerLockCommand;
import net.mcfr.commands.SpawnCommand;
import net.mcfr.commands.SpectateCommand;
import net.mcfr.commands.SpeedCommand;
import net.mcfr.commands.SpyMpCommand;
import net.mcfr.commands.TpCommand;
import net.mcfr.commands.TpHereCommand;
import net.mcfr.commands.TpPosCommand;
import net.mcfr.commands.TpToCommand;
import net.mcfr.commands.TribalLanguageCommand;
import net.mcfr.commands.VanishCommand;
import net.mcfr.commands.WhoIsCommand;

// TODO Trouver un moyen de refactorer ça. C'est dégueu.
public enum Command {
  BABEL(BabelCommand.class),
  BACK(BackCommand.class),
  BURROW(BurrowCommand.class),
  DESC(DescCommand.class),
  EXPEDITION(ExpeditionCommand.class),
  FATIGUE(FatigueCommand.class),
  FLY(FlyCommand.class),
  GM(GmCommand.class),
  GOD(GodCommand.class),
  HEAL(HealCommand.class),
  HEALTH(HealthCommand.class),
  HRP(HrpCommand.class),
  ITEM(ItemCommand.class),
  MOVENPC(MoveNpcCommand.class),
  MP(MpCommand.class),
  MUTE(MuteCommand.class),
  NAME(NameCommand.class),
  NO(NoCommand.class),
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
  WHOIS(WhoIsCommand.class);

  private Class<? extends AbstractCommand> cmdClass;

  private Command(Class<? extends AbstractCommand> cmdClass) {
    this.cmdClass = cmdClass;
  }

  public Class<? extends AbstractCommand> getCommandClass() {
    return this.cmdClass;
  }

  public Optional<AbstractCommand> f() {
    try {
      return Optional.of(getCommandClass().getConstructor(Essentials.class).newInstance(this));
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
        | SecurityException e) {
      e.printStackTrace();
    }
    return Optional.empty();
  }
}