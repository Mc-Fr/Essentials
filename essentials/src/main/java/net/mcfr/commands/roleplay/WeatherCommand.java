package net.mcfr.commands.roleplay;

import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3i;

import net.mcfr.Essentials;
import net.mcfr.commands.AbstractCommand;
import net.mcfr.time.TimeService;
import net.mcfr.time.weather.Weather;

public class WeatherCommand extends AbstractCommand {

  public WeatherCommand(Essentials plugin) {
    super(plugin);
  }

  @Override
  public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
    if (src instanceof Player) {
      Player player = (Player) src;
      Optional<TimeService> optTimeService = Sponge.getServiceManager().provide(TimeService.class);
      if (optTimeService.isPresent()) {
        optTimeService.get().update();
        Weather weather = optTimeService.get().getWeather();
        Location<World> loc = player.getLocation();
        player.sendMessage(Text.of(TextColors.BLUE, weather.getWeatherString(
            loc.getExtent().getBiome(new Vector3i(loc.getBlockX(), 0, loc.getBlockZ())), loc.getBlockY(), optTimeService.get().getDate())));
      } else {
        player.sendMessage(Text.of(TextColors.RED, "Le système de gestion du temps n'a pas été correctement chargé."));
      }
    } else {
      for (Player p : Sponge.getServer().getOnlinePlayers()) {
        Sponge.getCommandManager().process(p, "meteo");
      }
    }
    return CommandResult.success();
  }

  @Override
  public CommandSpec getCommandSpec() {
    //#f:0
    return CommandSpec.builder()
            .description(Text.of(""))
            .permission("essentials.command.meteo")
            .executor(this)
            .build();
    //#f:1
  }

  @Override
  public String[] getAliases() {
    return new String[] { "meteo" };
  }
}
