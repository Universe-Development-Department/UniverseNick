package dev.rajaopak.universenick.command;

import dev.rajaopak.universenick.Nicks;
import dev.rajaopak.universenick.config.NicksMessages;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;

/**
 * Handles <code>/nicksreload</code> command execution and tab completion.
 */
public class CommandNicksReload implements TabExecutor {

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                           @NotNull String label, @NotNull String[] args) {
    Nicks.core().reload();
    NicksMessages.PLUGIN_RELOADED.send(sender);
    return true;
  }

  @Override
  public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                              @NotNull String alias, @NotNull String[] args) {
    return Collections.emptyList();
  }
}