package dev.rajaopak.universenick.config;

import dev.rajaopak.universenick.Nicks;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static dev.rajaopak.universenick.Nicks.utils;

/**
 * Handles all plugin messages.
 */
public interface NicksMessages {

  Args0 PREFIX = () -> utils().configString("message.prefix", null);

  Args0 INVALID_SENDER = () -> utils().configString(utils().getPrefix() + "messages.invalidSender",
      "<red>You must be in-game to use this command.");

  Args1<String> UNKNOWN_PLAYER = playerName -> utils().configString(utils().getPrefix() + "messages.unknownPlayer",
      "<red>Unknown player %player%.").replaceText(TextReplacementConfig.builder()
      .matchLiteral("%player%").replacement(playerName).build());

  Args0 NO_PERMISSION = () -> utils().configString(utils().getPrefix() + "messages.noPermission",
      "<red>You do not have permission to execute this command.");

  Args1<Integer> TOO_SHORT = minLength -> MiniMessage.get().parse(Nicks.core().getConfig()
      .getString(utils().getPrefix() + "messages.tooShort", "<red>That nickname is too short. Minimum length is "
          + "%length% characters.")).replaceText(TextReplacementConfig.builder().matchLiteral("%length%")
      .replacement(String.valueOf(minLength)).build());

  Args1<Integer> TOO_LONG = maxLength -> utils().configString(utils().getPrefix() + "messages.tooLong",
      "<red>That nickname is too long. Maximum length is %length% characters.")
      .replaceText(TextReplacementConfig.builder().matchLiteral("%length%")
      .replacement(String.valueOf(maxLength)).build());

  Args0 NON_ALPHANUMERIC = () -> utils().configString(utils().getPrefix() + "messages.nonAlphanumeric",
      "<red>Your nickname must be alphanumeric.");

  Args1<Component> NICKNAME_SET = nickname -> utils().configString(utils().getPrefix() + "messages.nicknameSet",
      "<gray>Your nickname has been set to: <white>%nick%<gray>.")
      .replaceText(TextReplacementConfig.builder().matchLiteral("%nick%").replacement(nickname).build());

  Args2<Player, Component> NICKNAME_SET_OTHER = (player, nickname) -> utils()
      .configStringPlaceholders(utils().getPrefix() + "messages.nicknameSetOther", "<aqua>%player%<gray>'s nickname has been " +
          "set to: <white>%nick%<gray>.", player)
      .replaceText(TextReplacementConfig.builder().matchLiteral("%player%").replacement(player.getName()).build())
      .replaceText(TextReplacementConfig.builder().matchLiteral("%nick%").replacement(nickname).build());

  Args0 NICKNAME_REMOVED = () -> utils().configString(utils().getPrefix() + "messages.nicknameRemoved", "<gray>Nickname removed.");

  Args1<Player> NICKNAME_REMOVED_OTHER = target -> utils().configStringPlaceholders(
                  utils().getPrefix() + "messages.nicknameRemovedOther", "<aqua>%player%<gray>'s nickname removed.", target)
      .replaceText(TextReplacementConfig.builder().matchLiteral("%player%").replacement(target.getName()).build());

  Args0 ONLY_COLOR_CODES = () -> utils().configString(utils().getPrefix() + "messages.onlyColorCodes",
      "<red>You may only include color codes.");

  Args0 PLUGIN_RELOADED = () -> utils().configString(utils().getPrefix() + "messages.pluginReloaded", "<green>Plugin reloaded.");

  /**
   * A message that has no arguments that need to be replaced.
   */
  interface Args0 {
    Component build();

    default void send(CommandSender sender) {
      Nicks.software().sendMessage(sender, build());
    }
  }

  /**
   * A message that has one argument that needs to be replaced.
   */
  interface Args1<A0> {
    Component build(A0 arg0);

    default void send(CommandSender sender, A0 arg0) {
      Nicks.software().sendMessage(sender, build(arg0));
    }
  }

  /**
   * A message that has two arguments that need to be replaced.
   */
  interface Args2<A0, A1> {
    Component build(A0 arg0, A1 arg1);

    default void send(CommandSender sender, A0 arg0, A1 arg1) {
      Nicks.software().sendMessage(sender, build(arg0, arg1));
    }
  }
}