package dev.rajaopak.universenick.command;

import dev.rajaopak.universenick.Nicks;
import dev.rajaopak.universenick.api.SetNickEvent;
import dev.rajaopak.universenick.config.NicksMessages;
import java.util.Collections;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Handles <code>/nick</code> command execution and tab completion.
 */
public class CommandNick implements TabExecutor {

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                           @NotNull String label, @NotNull String[] args) {
    // Console cannot have a nickname
    if (!(sender instanceof Player)) {
      NicksMessages.INVALID_SENDER.send(sender);
      return true;
    }
    Player player = (Player) sender;

    if (args.length == 0) {
      return false;
    }

    String nickInput = String.join(" ", args);

    // Check if we're supporting legacy
    if (Nicks.config().LEGACY_COLORS) {
      nickInput = Nicks.utils().legacyToMini(nickInput);
    }

    Component nickname = MiniMessage.get().parse(nickInput);
    String plainTextNick = PlainTextComponentSerializer.plainText().serialize(nickname);
    int maxLength = Nicks.config().MAX_LENGTH;
    int minLength = Nicks.config().MIN_LENGTH;

    // Make sure the nickname is alphanumeric if that's enabled
    if (Nicks.config().REQUIRE_ALPHANUMERIC) {
      if (!plainTextNick.matches("[a-zA-Z0-9]+")) {
        NicksMessages.NON_ALPHANUMERIC.send(player);
        return true;
      }
    }

    // Make sure the nickname isn't too short
    if (plainTextNick.length() < minLength) {
      NicksMessages.TOO_SHORT.send(player, minLength);
      return true;
    }

    // Make sure the nickname isn't too long
    if (plainTextNick.length() > maxLength) {
      NicksMessages.TOO_LONG.send(player, maxLength);
      return true;
    }

    // Call event
    SetNickEvent nickEvent = new SetNickEvent(player, nickname,
        Nicks.core().getDisplayName(player));
    Nicks.api().callEvent(nickEvent);
    if (nickEvent.isCancelled()) {
      return true;
    }

    // Set nick
    Nicks.core().setNick(player, nickEvent.newNick());
    NicksMessages.NICKNAME_SET.send(player, nickEvent.newNick());

    return true;
  }

  @Override
  public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                              @NotNull String label, @NotNull String[] args) {
    return Collections.emptyList();
  }
}