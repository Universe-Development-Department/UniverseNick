package dev.rajaopak.universenick.server;

import dev.rajaopak.universenick.Nicks;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Handles Spigot server software.
 */
@SuppressWarnings("deprecation")
public final class SpigotServer implements ServerSoftware {

  private final LegacyComponentSerializer legacyComponentSerializer;

  public SpigotServer(){
    legacyComponentSerializer = LegacyComponentSerializer.builder().hexColors()
        .useUnusualXRepeatedCharacterHexFormat().build();
  }

  @Override
  public Component getNick(Player player) {
    return legacyComponentSerializer.deserialize(player.getDisplayName());
  }

  @Override
  public void setNick(@NotNull Player player, @NotNull Component nickname) {
    nickname = Component.empty().color(NamedTextColor.WHITE)
        .decoration(TextDecoration.BOLD, false).append(nickname);
    Nicks.core().getNickMap().put(player.getUniqueId(), nickname);
    player.setDisplayName(legacyComponentSerializer.serialize(nickname));
    if (Nicks.config().TAB_NICKS) {
      player.setPlayerListName(legacyComponentSerializer.serialize(nickname));
    }
    Nicks.storage().saveNick(player.getUniqueId());
  }

  @Override
  public void removeNick(@NotNull Player player) {
    Nicks.core().getNickMap().remove(player.getUniqueId());
    player.setDisplayName(player.getName());
    if (Nicks.config().TAB_NICKS) {
      player.setPlayerListName(player.getName());
    }
    Nicks.storage().removeNick(player.getUniqueId());
  }

  @Override
  public void sendMessage(CommandSender sender, Component message) {
    BukkitAudiences.create(Nicks.core()).sender(sender).sendMessage(message);
  }

  @Override
  public String softwareName() {
    return "SpigotMC";
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onPlayerChatLow(AsyncPlayerChatEvent event) {
    String format = Nicks.config().CHAT_FORMAT;
    format = Nicks.utils().miniToLegacy(format)
        .replace("{displayname}", "%1$s")
        .replace("{message}", "%2$s");
    event.setFormat(format);
    Nicks.debug("low - " + event.getFormat());
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerChatHigh(AsyncPlayerChatEvent event) {
    // Replace our placeholders on highest - just before
    String format = event.getFormat();

    // This is safe. If Vault isn't hooked then NicksHooks#vaultPrefix() will return ""
    format = format.replace("{prefix}", Nicks.utils()
        .applyLegacyColors(Nicks.hooks().vaultPrefix(event.getPlayer())));
    format = format.replace("{suffix}", Nicks.utils()
        .applyLegacyColors(Nicks.hooks().vaultPrefix(event.getPlayer())));
    format = format.replace("{displayname}", Nicks.utils()
        .applyLegacyColors(event.getPlayer().getDisplayName()));

    event.setFormat(format);
    Nicks.debug("high - " + event.getFormat());
  }
}