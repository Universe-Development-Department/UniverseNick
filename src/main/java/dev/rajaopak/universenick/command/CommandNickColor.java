package dev.rajaopak.universenick.command;

import dev.rajaopak.universenick.Nicks;
import dev.rajaopak.universenick.api.NickColorEvent;
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
 * Handles <code>/nickcolor</code> command execution and tab completion.
 */
public class CommandNickColor implements TabExecutor {

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

    // If there are no colors the length should be 0
    String plainTextInput = PlainTextComponentSerializer.plainText()
        .serialize(MiniMessage.get().parse(nickInput));
    if (plainTextInput.length() > 0) {
      NicksMessages.ONLY_COLOR_CODES.send(player);
      return true;
    }

    // Get the players current nickname to apply color codes to
    String plainTextNick = PlainTextComponentSerializer.plainText()
        .serialize(Nicks.core().getDisplayName(player));
    Component nickname = MiniMessage.get().parse(nickInput + plainTextNick);

    // Call event
    NickColorEvent colorEvent = new NickColorEvent(player, nickname,
        Nicks.core().getDisplayName(player));
    Nicks.api().callEvent(colorEvent);
    if (colorEvent.isCancelled()) {
      return true;
    }

    // Set nick
    Nicks.core().setNick(player, colorEvent.newNick());
    NicksMessages.NICKNAME_SET.send(player, colorEvent.newNick());

    return true;
  }

  @Override
  public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                              @NotNull String label, @NotNull String[] args) {
    return Collections.emptyList();
  }
}