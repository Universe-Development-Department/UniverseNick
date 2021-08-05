package dev.rajaopak.universenick.command;

import dev.rajaopak.universenick.Nicks;
import dev.rajaopak.universenick.api.NoNickEvent;
import dev.rajaopak.universenick.api.NoNickOtherEvent;
import dev.rajaopak.universenick.config.NicksMessages;
import dev.rajaopak.universenick.util.TabCompleterBase;
import java.util.Collections;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Handles <code>/nonick</code> command execution and tab completion.
 */
public class CommandNoNick implements TabExecutor {

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                           @NotNull String label, @NotNull String[] args) {
    if (args.length == 0) {
      // Console has no nickname
      if (!(sender instanceof Player)) {
        NicksMessages.INVALID_SENDER.send(sender);
        return true;
      }
      Player player = (Player) sender;

      // Call event
      NoNickEvent noNickEvent = new NoNickEvent(player,
          Nicks.core().getDisplayName(player));
      Nicks.api().callEvent(noNickEvent);
      if (noNickEvent.isCancelled()) {
        return true;
      }

      Nicks.core().removeNick(player);
      NicksMessages.NICKNAME_REMOVED.send(player);

    } else {

      // Make sure the sender has permission to remove another player's nickname
      if (!sender.hasPermission("papernicks.nonick.other")) {
        NicksMessages.NO_PERMISSION.send(sender);
        return true;
      }

      // Make sure the target player is online
      Player target = Bukkit.getPlayer(args[0]);
      if (target == null) {
        NicksMessages.UNKNOWN_PLAYER.send(sender, args[0]);
        return true;
      }

      // Call event
      NoNickOtherEvent event = new NoNickOtherEvent(sender, target,
          Nicks.core().getDisplayName(target));
      Nicks.api().callEvent(event);
      if (event.isCancelled()) {
        return true;
      }

      Nicks.core().removeNick(target);
      NicksMessages.NICKNAME_REMOVED_OTHER.send(sender, target);
    }
    return true;
  }

  @Override
  public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                    @NotNull String label, @NotNull String[] args) {
    if (args.length == 1) {
      return TabCompleterBase.getOnlinePlayers(args[0]);
    } else {
      return Collections.emptyList();
    }
  }
}