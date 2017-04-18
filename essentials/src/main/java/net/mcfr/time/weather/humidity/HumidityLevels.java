package net.mcfr.time.weather.humidity;

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
    BiomeGenres PLAIN = BiomeGenres.PLAIN;
    BiomeGenres FOREST = BiomeGenres.FOREST;
    BiomeGenres DESERT = BiomeGenres.DESERT;
    BiomeGenres OCEAN = BiomeGenres.OCEAN;
    BiomeGenres SNOWY = BiomeGenres.SNOWY;
    
    Seasons SPRING = Seasons.SPRING;
    Seasons SUMMER = Seasons.SUMMER;
    Seasons FALL = Seasons.FALL;
    Seasons WINTER = Seasons.WINTER;
    
    // #f:0
    
    // Forêt, Printemps
    SUNNY.addMessage(new DayMessage("Le soleil vient frapper les feuillages de ses rayons chauds. L’ombre créée par les arbres procure une douce fraîcheur.", SPRING, FOREST));
    SUNNY.addMessage(new DayMessage("L’astre lumineux laisse ses rayons illuminer les endroits non couvert par les épais feuillages des grand arbres.", SPRING, FOREST));
    SUNNY.addMessage(new NightMessage("La lune n’éclaire que peu la forêt dans son ensemble, laissant une atmosphère fraîche et douce envahir cette dernière.", SPRING, FOREST));
    SUNNY.addMessage(new NightMessage("La nuit, tout est sombre dans la forêt. Les quelques rayons lunaires viennent parsemer cette dernière de quelques faibles tâches lumineuses.", SPRING, FOREST));
    CLOUDY.addMessage(new DayMessage("Les rares rayons lumineux ne traversent que peu les différents nuages. En forêt la rare luminosité est bloquée par les arbres qui s’en abreuvent.", SPRING, FOREST));
    CLOUDY.addMessage(new DayMessage("Les gros nuages qui parcourent le ciel empêche au soleil de faire rayonner la forêt, ce qui lui donne un air triste et inquiétant, sans rayons de soleil.", SPRING, FOREST));
    CLOUDY.addMessage(new NightMessage("Les nuages couvrent le ciel, obstruant les rayons lunaires. La forêt est très sombre.", SPRING, FOREST));
    THIN_RAIN.addMessage(new DayMessage("Une pluie fine ruisselle sur les feuilles des arbres, gouttant sur ceux qui se trouvent en-dessous.", SPRING, FOREST));
    THIN_RAIN.addMessage(new NightMessage("De fines gouttelettes percent la canopée pour tremper les explorateurs assez inconscients pour se promener dans la forêt de nuit.", SPRING, FOREST));
    HEAVY_RAIN.addMessage(new DayMessage("La luminosité diminue fortement alors que le fracas de la pluie qui s’abat avec force sur les feuilles fait écho à vos oreilles.", SPRING, FOREST));
    HEAVY_RAIN.addMessage(new NightMessage("Des giboulées froides s’abattent sur la forêt, et les branchages des arbres s’agitent sous le vent.", SPRING, FOREST));
    STORM.addMessage(new DayMessage("La forêt s’assombrit dangereusement alors que le grondement du tonnerre se fait entendre au-dessus des arbres. La pluie frappe les feuilles avec violence.", SPRING, FOREST));
    STORM.addMessage(new NightMessage("Une obscurité quasi-totale s’empare de la forêt, alors que le tonnerre de la pluie qui frappe la canopée fait écho aux éclairs qui se déchaînent au-dessus de vos têtes.", SPRING, FOREST));
    
    // Forêt, Été
    SUNNY.addMessage(new DayMessage("La forêt se pare de jeux de lumière chatoyants. Dans les arbres, les oiseaux s’agitent et se mettent à chanter.", SUMMER, FOREST));
    SUNNY.addMessage(new DayMessage("L’ombre fournie par les feuillages fournit une protection bienvenue contre la chaleur de l’été.", SUMMER, FOREST));
    SUNNY.addMessage(new NightMessage("La température chute légèrement alors que la nuit s’abat sur la forêt.", SUMMER, FOREST));
    SUNNY.addMessage(new NightMessage("Une chaude nuit d’été plonge la forêt dans une douce torpeur.", SUMMER, FOREST));
    CLOUDY.addMessage(new DayMessage("Une pénombre triste s’abat sur la forêt alors que le ciel se couvre.", SUMMER, FOREST));
    CLOUDY.addMessage(new DayMessage("Une morne obscurité envahit lentement la forêt au fur et à mesure que les nuages avalent le soleil.", SUMMER, FOREST));
    CLOUDY.addMessage(new NightMessage("La clarté lunaire disparaît graduellement sous les nuages, alors que la forêt plonge doucement dans le noir.", SUMMER, FOREST));
    CLOUDY.addMessage(new NightMessage("L’obscurité s’empare de la forêt au fur et à mesure que le chant des oiseaux se tait. La lune est progressivement masquée par d’épais nuages cotonneux.", SUMMER, FOREST));
    THIN_RAIN.addMessage(new DayMessage("Une légère pluie chaude ruisselle sur le feuillage des arbres.", SUMMER, FOREST));
    THIN_RAIN.addMessage(new NightMessage("Une bruine tiède trempe les voyageurs qui errent sous la futaie.", SUMMER, FOREST));
    HEAVY_RAIN.addMessage(new DayMessage("De grosses gouttes frappent la canopée avec force, alors que la luminosité diminue fortement. L’humidité est pesante.", SUMMER, FOREST));
    HEAVY_RAIN.addMessage(new NightMessage("Le temps est lourd, la nuit opaque et l'eau martelle sur les feuilles.", SUMMER, FOREST));
    STORM.addMessage(new DayMessage("Une pluie drue et chaude vous trempe jusqu’aux os alors que le grondement de l’orage se fait entendre au-dessus de la canopée.", SUMMER, FOREST));
    STORM.addMessage(new NightMessage("L’obscurité est quasi-totale alors qu’une pluie violente s’abat sur vous, perçant la canopée. Le tonnerre résonne au-dessus de vos têtes, se répercutant entre les troncs. ", SUMMER, FOREST));
    
    // Forêt, Automne
    SUNNY.addMessage(new DayMessage("Le soleil morne d’automne se répercute sur le camaïeu des arbres, peinant à réchauffer la forêt.", FALL, FOREST));
    SUNNY.addMessage(new DayMessage("Le feuillage cramoisi des arbres tombe lentement autour de vous alors que la forêt et sa vie entrent lentement en hibernation.", FALL, FOREST));
    SUNNY.addMessage(new NightMessage("Une nuit calme s’abat sur la forêt. Seul le bruissement des feuilles qui tombent vient troubler le silence.", FALL, FOREST));
    SUNNY.addMessage(new NightMessage("Une pénombre fraîche s’empare de la forêt. La lune éclaire doucement le feuillage carmin.", FALL, FOREST));
    CLOUDY.addMessage(new DayMessage("Le vent souffle entre les arbres, apportant avec lui une couche nuageuse qui vient masquer le soleil.", FALL, FOREST));
    CLOUDY.addMessage(new DayMessage("Le gris terne du ciel empêche les maigres rayons solaires de venir réchauffer la forêt.", FALL, FOREST));
    CLOUDY.addMessage(new NightMessage("Une nuit sombre envahit la forêt alors que les nuages avalent la lune.", FALL, FOREST));
    CLOUDY.addMessage(new NightMessage("Une obscurité calme s’abat sur la forêt. On n’entend que le souffle du vent qui balaye les feuilles tombées au sol.", FALL, FOREST));
    THIN_RAIN.addMessage(new DayMessage("Une pluie fine et fraîche ruisselle le long des branches, partiellement arrêtée par le peu de feuilles qui ne sont pas tombées.", FALL, FOREST));
    THIN_RAIN.addMessage(new NightMessage("Une bruine humide vient glacer ceux qui ne sont pas habillés pour ce temps d’automne.", FALL, FOREST));
    HEAVY_RAIN.addMessage(new DayMessage("Une pluie violente et froide s’abat sur la forêt. Une odeur d’humus se dégage du sol alors que les feuilles pourrissent. Mieux vaut s’abriter rapidement.", FALL, FOREST));
    HEAVY_RAIN.addMessage(new NightMessage("Un déluge d’eau s’abat sur la forêt, trempant jusqu’aux os ceux qui se trouvent en-dessous. La visibilité est quasi-nulle.", FALL, FOREST));
    
    // Forêt, Hiver
    SUNNY.addMessage(new DayMessage("Le soleil d’hiver réchauffe difficilement ceux qui s’aventurent sous la frondaison des arbres. A l’ombre, le sol verglacé est recouvert de neige qui peine à fondre.", WINTER, FOREST));
    SUNNY.addMessage(new DayMessage("Un froid mordant circule entre les troncs dénudés, porté par le vent. La neige fond lentement, rendant le sol boueux.", WINTER, FOREST));
    SUNNY.addMessage(new NightMessage("Une nuit glaciale et claire s’abat sur la forêt, mordant la chair de ceux qui ne sont pas habillés pour ces températures.", WINTER, FOREST));
    SUNNY.addMessage(new NightMessage("Les rayons lunaires éclairent les arbres dénudés, perçant ça et là l’obscurité froide de la nuit.", WINTER, FOREST));
    CLOUDY.addMessage(new DayMessage("Le ciel se couvre de nuages grisâtres tandis que le vent se lève, soufflant entre les troncs.", WINTER, FOREST));
    CLOUDY.addMessage(new DayMessage("Le soleil disparaît derrière un épais voile cotonneux alors qu’un froid mordant s’installe sur la forêt. ", WINTER, FOREST));
    CLOUDY.addMessage(new NightMessage("La lune est masquée par des nuages imposants, plongeant la forêt dans une froide obscurité.", WINTER, FOREST));
    CLOUDY.addMessage(new NightMessage("Un épais voile cotonneux avale la clarté des astres. La pénombre s’abat autour de vous.", WINTER, FOREST));
    THIN_RAIN.addMessage(new DayMessage("Une pluis fine et froide s’abat sur la forêt, ruisselant le long des troncs dénudés.", WINTER, FOREST));
    THIN_RAIN.addMessage(new NightMessage("Quelques gouttes froides tombent, troublant le calme de la nuit en s’abattant sur le sol givré.", WINTER, FOREST));
    HEAVY_RAIN.addMessage(new DayMessage("Des giboulées violentes, faites de grêlons et de neige mêlés, déferlent sur la forêt, obscurcissant le ciel. Mieux vaut s’abriter.", WINTER, FOREST));
    HEAVY_RAIN.addMessage(new NightMessage("Un violent déluge de neige fondue tombe sur les voyageurs assez inconscients pour se promener de nuit dans la forêt.", WINTER, FOREST));
    STORM.addMessage(new DayMessage("Le ciel vire au noir alors qu’une grêle d’une violence rare se met à tomber. Au-dessus de vos têtes, le tonnerre gronde dangereusement.", WINTER, FOREST));
    STORM.addMessage(new NightMessage("La forêt est avalée par l’obscurité alors que la lune disparaît derrière d’épais nuages noirs. Un déluge de grêlons et de neige se met à chuter du ciel. Mieux vaut s’abriter rapidement.", WINTER, FOREST));
    
    // Plaine, Printemps
    // Plaine, Été
    // Plaine, Automne
    // Plaine, Hiver
    
    // Océan, Printemps
    // Océan, Été
    // Océan, Automne
    // Océan, Hiver
    
    // Désert, Printemps
    SUNNY.addMessage(new DayMessage("Le soleil brille d'un éclat quasi insoutenable, se reflétant sur le sable. Mieux vaut éviter ses rayons pour le moment ...", SPRING, DESERT));
    SUNNY.addMessage(new NightMessage("Les astres semblent plus nombreux qu'ailleurs, les nuits restent chaudes, malgré la disparition du jour.", SPRING, DESERT));
    CLOUDY.addMessage(new DayMessage("L'astre solaire s'élève dans le ciel, ce dernier est décoré de quelques rares et éparses nuages cantonnés à l'horizon.", SPRING, DESERT));
    CLOUDY.addMessage(new NightMessage("Le silence s'installe avec l’arrivée de la nuit, il devient viable d'être à l'éxtérieur.", SPRING, DESERT));
    THIN_RAIN.addMessage(new DayMessage("Le désert reste immuable, les seules variations venant de ses visiteurs et des ombres que l'éclatant soleil projette.", SPRING, DESERT));
    THIN_RAIN.addMessage(new NightMessage("Une chape d'ombre s'abat sur les dunes, accentuant l'impression de solitude.", SPRING, DESERT));
    HEAVY_RAIN.addMessage(new DayMessage("Presque pâle, le soleil continue sa route, son rayonnement se faisant supportable.", SPRING, DESERT));
    HEAVY_RAIN.addMessage(new NightMessage("L'éclat des étoiles se trouve atténué par de légers tourbillons de sables, qui ainsi soulevés forment une sorte de nuage.", SPRING, DESERT));  
    STORM.addMessage(new DayMessage("Le vent se lève, mugissant de toute part, le sable s'élève en de grandes volutes, venant cacher le ciel et fouetter la peau.", SPRING, DESERT));
    STORM.addMessage(new NightMessage("Une tempête de sable se lève, venant cacher les étoiles, privant le désert de toute lumière.", SPRING, DESERT));
    
    // Désert, Été
    SUNNY.addMessage(new DayMessage("Le soleil brille d'un éclat quasi insoutenable, se reflétant sur le sable. Mieux vaut éviter ses rayons pour le moment ...", SUMMER, DESERT));
    SUNNY.addMessage(new NightMessage("Les astres semblent plus nombreux qu'ailleurs, les nuits restent chaudes, malgré la disparition du jour.", SUMMER, DESERT));
    CLOUDY.addMessage(new DayMessage("L'astre solaire s'élève dans le ciel, ce dernier est décoré de quelques rares et éparses nuages cantonnés à l'horizon.", SUMMER, DESERT));
    CLOUDY.addMessage(new NightMessage("Le silence s'installe avec l’arrivée de la nuit, il devient viable d'être à l'extérieur.", SUMMER, DESERT));   
    HEAVY_RAIN.addMessage(new DayMessage("Le désert reste immuable, les seules variations venant de ses visiteurs et des ombres que l'éclatant soleil projette.", SUMMER, DESERT));
    HEAVY_RAIN.addMessage(new NightMessage("Une chape d'ombre s'abat sur les dunes, accentuant l'impression de solitude.", SUMMER, DESERT));   
    STORM.addMessage(new DayMessage("Le vent se lève, mugissant de toute part, le sable s'élève en de grandes volutes, venant cacher le ciel et fouetter la peau.", SUMMER, DESERT));
    STORM.addMessage(new NightMessage("Une tempête de sable se lève, venant cacher les étoiles, privant le désert de toute lumière.", SUMMER, DESERT));
    
    // Désert, Automne
    SUNNY.addMessage(new DayMessage("L'astre solaire baigne le désert de ses rayons, ceux-ci sont assez agressifs.", FALL, DESERT));
    SUNNY.addMessage(new NightMessage("Le sable, malgré son temps d'exposition au soleil, ne restitue pas assez de chaleur pour dissiper le froid de la nuit.", FALL, DESERT));
    CLOUDY.addMessage(new DayMessage("Un soleil de plomb règne sans partage sur le ciel, imposant sa domination au désert.", FALL, DESERT));
    CLOUDY.addMessage(new NightMessage("Un ciel constellé d'étoiles recouvre les froides dunes, dont le pesant silence n'est brisé que par un peu de vent.", FALL, DESERT));
    THIN_RAIN.addMessage(new DayMessage("Le sable s'étend de toute part, présentant parfois quelques arbustes et rochers qu'un soleil baigne d'ardents rayons.", FALL, DESERT));
    THIN_RAIN.addMessage(new NightMessage("L'impression de solitude se fait pesante dans la nuit glaciale que vous offre ses grandes étendues de sable.", FALL, DESERT));
    HEAVY_RAIN.addMessage(new DayMessage("Une brise vient soulever quelques grains de sable, rien de suffisant pour éviter au soleil de frapper sur le sable.", FALL, DESERT));
    HEAVY_RAIN.addMessage(new NightMessage("Mût par un vent persistant, le sable volette en tout sens, rendant changeant et vivant le paysage seulement éclairé par les astres.", FALL, DESERT));
    
    // Désert, Hiver
    SUNNY.addMessage(new DayMessage("L'astre solaire baigne le désert de ses rayons, ceux-ci sont assez agressifs.", WINTER, DESERT)); 
    SUNNY.addMessage(new NightMessage("Le sable, malgré son temps d'exposition au soleil, ne restitue pas assez de chaleur pour dissiper le froid de la nuit.", WINTER, DESERT));  
    CLOUDY.addMessage(new DayMessage("Un soleil de plomb règne sans partage sur le ciel, imposant sa domination au désert.", WINTER, DESERT)); 
    CLOUDY.addMessage(new NightMessage("Un ciel constellé d'étoiles recouvre les froides dunes, dont le pesant silence n'est brisé que par un peu de vent.", WINTER, DESERT)); 
    THIN_RAIN.addMessage(new DayMessage("Le sable s'étend de toute part, présentant parfois quelques arbustes et rochers qu'un soleil baigne d'ardents rayons.", WINTER, DESERT)); 
    THIN_RAIN.addMessage(new NightMessage("L'impression de solitude se fait pesante dans la nuit glaciale que vous offre ses grandes étendues de sable.", WINTER, DESERT));
    HEAVY_RAIN.addMessage(new DayMessage("Une brise vient soulever quelques grains de sable, rien de suffisant pour éviter au soleil de frapper sur le sable.", WINTER, DESERT));  
    HEAVY_RAIN.addMessage(new NightMessage("Mût par un vent persistant, le sable volette en tout sens, rendant changeant et vivant le paysage seulement éclairé par les astres.", WINTER, DESERT));  
    STORM.addMessage(new DayMessage("Un tempête se lève, le sable s'incrustant en tout endroits exposés, masquant le ciel azur.", WINTER, DESERT));  
    STORM.addMessage(new NightMessage("Le froid s'empare du désert, le sable, battu par les vents, vient rendre l'endroit plus inhabitable encore. ", WINTER, DESERT)); 
    
    // Enneigé, Printemps
    
    // Enneigé, Été
    
    // Enneigé, Automne
    
    // Enneigé, Hiver
    
    
    // #f:1
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
    WeatherMessage[] availableMessages = this.messages.stream().filter(m -> m.isAcurate(biomeGenre, season, hour, wind)).toArray(WeatherMessage[]::new);
    if (availableMessages.length > 0) {
      return availableMessages[rand.nextInt(availableMessages.length)].toString();
    }
    return "";
  }
}
