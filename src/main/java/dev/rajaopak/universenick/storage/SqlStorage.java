package dev.rajaopak.universenick.storage;

import dev.rajaopak.universenick.Nicks;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Handles Sql storage for nicknames.
 */
public class SqlStorage implements StorageMethod {

  @Override
  public boolean hasNick(@NotNull UUID uuid) {
    try {
      PreparedStatement ps = Nicks.sql().getConnection()
          .prepareStatement("SELECT nickname FROM nicknameTable WHERE uniqueId=?");
      ps.setString(1, uuid.toString());
      ResultSet resultSet = ps.executeQuery();
      String nickname;
      if (resultSet.next()) {
        nickname = resultSet.getString("nickname");
        return nickname == null;
      } else {
        return false;
      }
    } catch (SQLException ex) {
      ex.printStackTrace();
      return false;
    }
  }

  @Override
  @SuppressWarnings("ConstantConditions")
  public Component getNick(@NotNull UUID uuid) {
    try {
      PreparedStatement ps = Nicks.sql().getConnection()
          .prepareStatement("SELECT nickname FROM nicknameTable WHERE uniqueId=?");
      ps.setString(1, uuid.toString());
      ResultSet resultSet = ps.executeQuery();
      String nickname;
      if (resultSet.next()) {
        nickname = resultSet.getString("nickname");
        return GsonComponentSerializer.gson().deserialize(nickname);
      }
      ps.executeUpdate();
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
    return Component.text(Bukkit.getOfflinePlayer(uuid).getName());
  }

  @Override
  public void removeNick(@NotNull UUID uuid) {
    try {
      PreparedStatement ps = Nicks.sql().getConnection()
          .prepareStatement("DELETE FROM nicknameTable WHERE uniqueId=?");
      ps.setString(1, uuid.toString());
      ps.executeUpdate();
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }

  @Override
  public void saveNick(@NotNull UUID uuid) {
    try {
      PreparedStatement ps = Nicks.sql().getConnection()
          .prepareStatement("UPDATE nicknameTable SET nickname=? WHERE uniqueId=?");
      ps.setString(1, GsonComponentSerializer.gson().serialize(getNick(uuid)));
      ps.setString(2, uuid.toString());
      ps.executeUpdate();
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }

  @Override
  public void updateNicks() {
    for (Player player : Bukkit.getOnlinePlayers()) {
      Nicks.software().setNick(player, getNick(player.getUniqueId()));
    }
  }
}