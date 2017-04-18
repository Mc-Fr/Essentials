package net.mcfr.time.weather.humidity;

import static net.mcfr.time.weather.BiomeGenres.*;
import static net.mcfr.time.weather.Seasons.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.mcfr.time.weather.BiomeGenres;
import net.mcfr.time.weather.Seasons;
import net.mcfr.time.weather.messages.DayMessage;
import net.mcfr.time.weather.messages.NightMessage;
import net.mcfr.time.weather.messages.WeatherMessage;
import net.mcfr.time.weather.wind.Wind;

public enum HumidityLevels {
  SUNNY(14),
  CLOUDY(7),
  THIN_RAIN(0),
  HEAVY_RAIN(-7),
  STORM(-14);

  static {
    SUNNY.addMessage(new NightMessage(
        "Doucement la nature se reveille, accompagnée d'un soleil bienveillant. Il se fait sentir un air printannier loin d'être désagréable.",
        SPRING, PLAIN));
    SUNNY.addMessage(new DayMessage(
        "Le soleil vient frapper les feuillages de ses rayons chauds. L’ombre créée par les arbres procure une douce fraîcheur.", SPRING, FOREST));
    SUNNY.addMessage(new DayMessage(
        "L’astre lumineux laisse ses rayons illuminer les endroits non couvert par les épais feuillages des grand arbres.", SPRING, FOREST));
    SUNNY.addMessage(new NightMessage(
        "La lune n’éclaire que peu la forêt dans son ensemble, laissant une atmosphère fraîche et douce envahir cette dernière.", SPRING, FOREST));
    SUNNY.addMessage(new NightMessage(
        "La nuit, tout est sombre dans la forêt. Les quelques rayons lunaires viennent parsemer cette dernière de quelques faibles tâches lumineuses.",
        SPRING, FOREST));
  }

  private int temperatureModificator;
  private List<WeatherMessage> messages;

  private HumidityLevels(int temperatureModificator) {
    this.temperatureModificator = temperatureModificator;
    this.messages = new ArrayList<>();
  }

  private void addMessage(WeatherMessage message) {
    this.messages.add(message);
  }

  public int getTemperatureModificator() {
    return this.temperatureModificator;
  }

  public String getWeatherString(BiomeGenres biomeGenre, Seasons season, int hour, Wind wind, Random rand) {
    WeatherMessage[] availableMessages = this.messages.stream().filter(m -> m.isAcurate(biomeGenre, season, hour, wind))
        .toArray(WeatherMessage[]::new);
    if (availableMessages.length > 0)
      return availableMessages[rand.nextInt(availableMessages.length)].toString();
    return "";
  }
}
