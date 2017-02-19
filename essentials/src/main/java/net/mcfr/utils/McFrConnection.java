package net.mcfr.utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.sql.SqlService;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public final class McFrConnection {
  private static String jdbcUrl;
  private static boolean configRead = false;
  private static McFrConnection jdrConnection;
  private static McFrConnection serverConnection;
  private static SqlService sql = Sponge.getServiceManager().provide(SqlService.class).get();

  private String database;

  private Connection connection;

  private McFrConnection(String database) {
    this.database = database;
    if (!configRead) {
      readConfigFile();
      configRead = true;
    }
    openConnection();
  }

  public void openConnection() {
    try {
      this.connection = sql.getDataSource(jdbcUrl + this.database).getConnection();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static McFrConnection getJdrConnection() {
    if (jdrConnection == null) {
      jdrConnection = new McFrConnection("minecraft");
    }
    return jdrConnection;
  }

  public static McFrConnection getServerConnection() {
    if (serverConnection == null) {
      serverConnection = new McFrConnection("srv_mcfr");
    }
    return serverConnection;
  }

  /**
   * Lit les identifiants de connexion renseignées dans le fichier de config : config/essentials-config/database.json.
   * 
   * Fichier de la forme (valeurs par défaut renseignées) : { "user" : "root", "password" : "" }
   */
  private static void readConfigFile() {
    File commandsFile = new File("config/essentials-config/database.json");
    if (commandsFile.exists()) {
      try {
        JsonObject database = new JsonParser().parse(new JsonReader(new FileReader(commandsFile))).getAsJsonObject();

        String user = database.get("user").getAsString();
        String password = database.get("password").getAsString();

        jdbcUrl = "jdbc:mysql://" + user + ":" + password + "@localhost/";
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public ResultSet executeQuery(String query) {
    try {
      return this.connection.prepareStatement(query).executeQuery();
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  public Connection getConnection() {
    try {
      return sql.getDataSource(jdbcUrl + this.database).getConnection();
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }
}
