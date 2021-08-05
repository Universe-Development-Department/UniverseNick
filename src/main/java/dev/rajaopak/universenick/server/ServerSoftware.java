package dev.rajaopak.universenick.server;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

/**
 * Handles different server software.
 */
public interface ServerSoftware extends Listener {

  /**
   * Fetch the player's nickname, which is their current display name.
   *
   * @param player The player.
   * @return Their nickname as a Component.
   */
  Component getNick(Player player);

  /**
   * Set a player's display name to their nickname.
   *
   * @param player The player who's display name should be modified.
   * @param nickname The nickname to set.
   */
  void setNick(@NotNull Player player, @NotNull Component nickname);

  /**
   * Remove a player's nickname.
   *
   * @param player The player.
   */
  void removeNick(@NotNull Player player);

  /**
   * Send a command sender a message.
   *
   * @param sender The message recipient.
   * @param message The message.
   */
  void sendMessage(CommandSender sender, Component message);

  String softwareName();

}