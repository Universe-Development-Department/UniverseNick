package dev.rajaopak.universenick.hook;

import dev.rajaopak.universenick.Nicks;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Handles PlaceholderAPI hook methods.
 */
public class PapiHook extends PlaceholderExpansion {

  private final JavaPlugin plugin;

  public PapiHook(JavaPlugin plugin){
    this.plugin = plugin;
  }

  @Override
  public boolean canRegister(){
    return true;
  }

  @Override
  public boolean persist(){
    return true;
  }

  @Override
  public @NotNull String getAuthor(){
    return plugin.getDescription().getAuthors().get(0);
  }

  @Override
  public @NotNull String getIdentifier(){
    return plugin.getDescription().getName().toLowerCase();
  }

  @Override
  public @NotNull String getVersion(){
    return plugin.getDescription().getVersion();
  }

  @Override
  public String onRequest(OfflinePlayer player, @NotNull String identifier) {
    if (identifier.equalsIgnoreCase("nick")) {
      Component nick = Nicks.core().getStoredNick(player.getUniqueId());
      if (nick == null) {
        return player.getName();
      } else {
        return LegacyComponentSerializer.legacySection().serialize(nick);
      }
    }
    return null;
  }

  /**
   * Apply PAPI placeholders.
   *
   * @param player The player to set placeholders for.
   * @param message The message to set placeholders in.
   * @return Formatted message.
   */
  public static String applyPlaceholders(Player player, String message) {
    return PlaceholderAPI.setPlaceholders(player, message);
  }
}