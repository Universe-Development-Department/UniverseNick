package dev.rajaopak.universenick.config;

import dev.rajaopak.universenick.Nicks;

/**
 * Handles all config options in the plugin.
 */
public class NicksConfig {

  public Boolean TAB_NICKS;
  public Integer MAX_LENGTH;
  public Integer MIN_LENGTH;
  public Boolean REQUIRE_ALPHANUMERIC;
  public Boolean CHAT_FORMATTER;
  public String  CHAT_FORMAT;
  public Boolean LEGACY_COLORS;
  public Boolean DEBUG;

  public NicksConfig() {
    reload();
  }

  /**
   * Reload the config values.
   */
  public void reload() {
    TAB_NICKS = Nicks.core().getConfig().getBoolean("tab-nicks", false);
    MAX_LENGTH = Nicks.core().getConfig().getInt("max-length", 20);
    MIN_LENGTH = Nicks.core().getConfig().getInt("min-length", 3);
    REQUIRE_ALPHANUMERIC = Nicks.core().getConfig().getBoolean("require-alphanumeric", false);
    CHAT_FORMATTER = Nicks.core().getConfig().getBoolean("chat-formatter", false);
    CHAT_FORMAT = Nicks.core().getConfig().getString("chat-format", "{displayname}: {message}");
    LEGACY_COLORS = Nicks.core().getConfig().getBoolean("legacy-colors", false);
    DEBUG = Nicks.core().getConfig().getBoolean("debug", false);
  }
}