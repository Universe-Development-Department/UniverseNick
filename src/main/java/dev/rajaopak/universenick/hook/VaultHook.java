package dev.rajaopak.universenick.hook;

import dev.rajaopak.universenick.Nicks;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Handles Vault hook methods.
 */
public class VaultHook {

  private Chat vaultChat;

  public VaultHook() {
    RegisteredServiceProvider<Chat> rsp = Nicks.core().getServer().getServicesManager().getRegistration(Chat.class);
    if (rsp == null) {
      Nicks.error("Error hooking into Vault!");
      return;
    }
    vaultChat = rsp.getProvider();
  }

  /**
   * Get the vault chat class.
   *
   * @return Chat.
   */
  public Chat vaultChat() {
    return vaultChat;
  }
}