package dev.rajaopak.universenick.hook;

import dev.rajaopak.universenick.Nicks;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;

/**
 * Handles hooked plugins.
 */
public class NicksHooks {

  private boolean papiHooked;
  private boolean vaultHooked;
  private PapiHook papiHook;
  private VaultHook vaultHook;

  public NicksHooks() {
    papiHooked = false;
    vaultHooked = false;
    papiHook = null;
    vaultHook = null;
  }

  /**
   * Reload the hooks to make sure we're hooked into all available plugins.
   */
  public void reloadHooks() {
    Nicks.debug("Reloaded hooks...");
    if (Nicks.core().getServer().getPluginManager().isPluginEnabled("PlaceholderAPI") &&
        Nicks.core().getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
      Nicks.log("Hooking into PlaceholderAPI...");
      papiHooked = true;
      papiHook = new PapiHook(Nicks.core());
      papiHook.register();
    }
    if (Nicks.core().getServer().getPluginManager().isPluginEnabled("Vault") &&
        Nicks.core().getServer().getPluginManager().getPlugin("Vault") != null) {
      Nicks.log("Hooking into Vault...");
      vaultHooked = true;
      vaultHook = new VaultHook();
    }
  }

  /**
   * Check if the plugin is hooked into PlaceholderAPI.
   *
   * @return True if hooked.
   */
  public boolean isPapiHooked() {
    return papiHooked;
  }

  /**
   * Check if the plugin is hooked into Vault.
   *
   * @return True if hooked.
   */
  public boolean isVaultHooked() {
    return vaultHooked;
  }

  /**
   * Apply placeholders from PlaceholderAPI to a string.
   *
   * @param player The player referenced in the string.
   * @param string The string to search.
   * @return Formatted string.
   */
  public String applyPlaceHolders(Player player, String string) {
    return isPapiHooked() ? PapiHook.applyPlaceholders(player, string) : string;
  }

  /**
   * Get a player's "vault display name" including vault prefixes and suffixes.
   *
   * @param player The player.
   * @return Display name as a Component.
   */
  public Component vaultDisplayName(Player player) {
    return isVaultHooked() ? LegacyComponentSerializer.legacySection().deserialize(vaultHook.vaultChat()
        .getPlayerPrefix(player)).append(Nicks.core().getDisplayName(player)).append(LegacyComponentSerializer
        .legacySection().deserialize(vaultHook.vaultChat().getPlayerSuffix(player)))
        : Nicks.core().getDisplayName(player);
  }

  public String vaultPrefix(Player player) {
    return isVaultHooked() ? vaultHook.vaultChat().getPlayerPrefix(player) : "";
  }

  public String vaultSuffix(Player player) {
    return isVaultHooked() ? vaultHook.vaultChat().getPlayerSuffix(player) : "";
  }
}