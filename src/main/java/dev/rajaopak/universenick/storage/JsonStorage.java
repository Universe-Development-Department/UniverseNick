package dev.rajaopak.universenick.storage;

import com.google.gson.JsonParser;
import dev.rajaopak.universenick.Nicks;
import java.io.IOException;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

/**
 * Handles Json storage for nicknames.
 */
public class JsonStorage implements StorageMethod {

  @Override
  public boolean hasNick(@NotNull UUID uuid) {
    return Nicks.core().getNickMap().containsKey(uuid);
  }

  @Override
  @SuppressWarnings("ConstantConditions")
  public Component getNick(@NotNull UUID uuid) {
    return hasNick(uuid) ? Nicks.core().getNickMap().get(uuid)
        : Component.text(Bukkit.getOfflinePlayer(uuid).getName());
  }

  @Override
  public void removeNick(@NotNull UUID uuid) {
    Nicks.core().getNickMap().remove(uuid);
    Bukkit.getScheduler().runTaskAsynchronously(Nicks.core(), () -> {
      try {
        Nicks.core().jsonConfig().removeFromJsonObject(uuid.toString());
        Nicks.debug("Removed nickname from user " + uuid + " from json.");
      } catch (IOException e) {
        Nicks.error("Error removing nickname from file \nUUID: " + uuid);
        e.printStackTrace();
      }
    });
  }

  @Override
  public void saveNick(@NotNull UUID uuid) {
    Bukkit.getScheduler().runTaskAsynchronously(Nicks.core(), () -> {
      try {
        Nicks.core().jsonConfig().putInJsonObject(uuid.toString(),
            JsonParser.parseString(GsonComponentSerializer.gson().serialize(getNick(uuid))));
        Nicks.debug("Saved nickname from user " + uuid + " to json.");
      } catch (IOException e) {
        Nicks.error("Error saving nickname to file \nUUID: " + uuid);
        e.printStackTrace();
      }
    });
  }

  @Override
  public void updateNicks() {
    // Nothing needed for json storage
  }
}