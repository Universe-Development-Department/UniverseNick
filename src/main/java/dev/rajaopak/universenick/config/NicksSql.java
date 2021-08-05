package dev.rajaopak.universenick.config;

import dev.rajaopak.universenick.Nicks;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.bukkit.Bukkit;

/**
 * Handles the plugin's MySQL connection.
 */
public class NicksSql {

  private final String host = Nicks.core().getConfig().getString("host");
  private final String port = Nicks.core().getConfig().getString("port");
  private final String database = Nicks.core().getConfig().getString("database");
  private final String username = Nicks.core().getConfig().getString("username");
  private final String password = Nicks.core().getConfig().getString("password");
  private final boolean useSSL = Nicks.core().getConfig().getBoolean("use-ssl");
  private final int updateInterval = Nicks.core().getConfig().getInt("update-interval", 300);

  private Connection connection;

  /**
   * Check if the plugin is connected to a database.
   *
   * @return True if connected.
   */
  public boolean isConnected() {
    return (connection != null);
  }

  /**
   * Try to connect to the database defined in the config file.
   *
   * @throws SQLException if there is an error connecting.
   */
  public void connect() throws SQLException {
    if (!isConnected()) {
      connection = DriverManager.getConnection("jdbc:mysql://" +
              host + ":" + port + "/" + database + "?useSSL=" + useSSL, username, password);
    }
    Bukkit.getScheduler().scheduleSyncRepeatingTask(Nicks.core(), () -> Nicks.storage().updateNicks(), 200L, updateInterval * 20L);
  }

  /**
   * Disconnect from the database if connected.
   */
  public void disconnect() {
    if (isConnected()) {
      try {
        connection.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Get the connection to the database.
   *
   * @return The connection.
   */
  public Connection getConnection() {
    return connection;
  }

  /**
   * Create the MySQL table if it doesn't already exist.
   */
  public void createTable() {
    PreparedStatement ps;
    try {
      ps = Nicks.sql().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS " +
          "nicknameTable (uniqueId VARCHAR(100),nickname VARCHAR(1000),PRIMARY KEY (uniqueId))");
      ps.executeUpdate();
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }
}