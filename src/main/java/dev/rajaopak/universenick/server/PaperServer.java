package dev.rajaopak.universenick.server;

import dev.rajaopak.universenick.Nicks;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

/**
 * Handles Paper server software.
 */
public final class PaperServer implements ServerSoftware {

  @Override
  public Component getNick(Player player) {
    return player.displayName();
  }

  @Override
  public void setNick(@NotNull Player player, @NotNull Component nickname) {
    nickname = Component.empty().color(NamedTextColor.WHITE)
        .decoration(TextDecoration.BOLD, false).append(nickname);
    Nicks.core().getNickMap().put(player.getUniqueId(), nickname);
    player.displayName(nickname);
    if (Nicks.config().TAB_NICKS) {
      player.playerListName(nickname);
    }
    Nicks.storage().saveNick(player.getUniqueId());
  }

  @Override
  public void removeNick(@NotNull Player player) {
    Nicks.core().getNickMap().remove(player.getUniqueId());
    player.displayName(Component.text(player.getName()));
    if (Nicks.config().TAB_NICKS) {
      player.playerListName(Component.text(player.getName()));
    }
    Nicks.storage().removeNick(player.getUniqueId());
  }

  @Override
  public void sendMessage(CommandSender sender, Component message) {
    sender.sendMessage(message);
  }

  @Override
  public String softwareName() {
    return "PaperMC";
  }

  /**
   * Fires on chat to format. Lowest priority to allow other plugins to modify over us.
   *
   * @param event AsyncChatEvent.
   */
  @EventHandler(priority = EventPriority.LOWEST)
  public void onChat(AsyncChatEvent event) {
    if (Nicks.config().CHAT_FORMATTER) {
      event.renderer((source, sourceDisplayName, message, viewer) -> MiniMessage.get().parse(Nicks.config().CHAT_FORMAT)
          .replaceText(TextReplacementConfig.builder().matchLiteral("{displayname}").replacement(getNick(source)).build())
          .replaceText(TextReplacementConfig.builder().matchLiteral("{prefix}").replacement(Nicks.hooks().vaultPrefix(source)).build())
          .replaceText(TextReplacementConfig.builder().matchLiteral("{suffix}").replacement(Nicks.hooks().vaultSuffix(source)).build())
          .replaceText(TextReplacementConfig.builder().matchLiteral("{message}").replacement(message).build()));
    }
  }
}