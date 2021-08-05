package dev.rajaopak.universenick.event;

import dev.rajaopak.universenick.Nicks;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * <p>Handles the player join event.</p>
 * <p>Sets the player displayname if they have a nickname in the map.</p>
 */
public class PlayerJoin implements Listener {

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerJoin(PlayerJoinEvent event) {
    Nicks.storage().updateNicks();
    if (Nicks.core().hasNick(event.getPlayer().getUniqueId())) {
      Nicks.core().setNick(event.getPlayer(), Nicks.core().getStoredNick(event.getPlayer().getUniqueId()));
    }
  }
}