package dev.rajaopak.universenick.storage;

import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

/**
 * Handles different storage methods.
 */
public interface StorageMethod {

  /**
   * Check if there is a nickname stored for the provided unique id.
   *
   * @param uuid The unique id to check.
   * @return True if nickname stored.
   */
  boolean hasNick(@NotNull UUID uuid);

  /**
   * Get the nickname stored for a specific unique id. {@link #hasNick(UUID)} should be checked first.
   *
   * @param uuid The unique id to fetch.
   * @return Nickname.
   */
  Component getNick(@NotNull UUID uuid);

  /**
   * Remove a nickname from storage.
   *
   * @param uuid The unique id the nickname is tied to.
   */
  void removeNick(@NotNull UUID uuid);

  /**
   * Save a nickname in storage. This can be used to put it there initially or to update it.
   *
   * @param uuid The unique id of the player who's nickname to save.
   */
  void saveNick(@NotNull UUID uuid);

  /**
   * Update the nickname of all online players from storage.
   */
  void updateNicks();

}