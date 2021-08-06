/*
 * This file is part of HexNicks, licensed under the MIT License.
 *
 * Copyright (c) 2020-2021 Majekdor
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.rajaopak.universenick;

import com.google.gson.JsonObject;
import dev.rajaopak.universenick.api.NicksApi;
import dev.rajaopak.universenick.command.*;
import dev.rajaopak.universenick.config.ConfigUpdater;
import dev.rajaopak.universenick.config.JsonConfig;
import dev.rajaopak.universenick.config.NicksConfig;
import dev.rajaopak.universenick.storage.JsonStorage;
import dev.rajaopak.universenick.storage.SqlStorage;
import dev.rajaopak.universenick.storage.StorageMethod;
import dev.rajaopak.universenick.event.PlayerJoin;
import dev.rajaopak.universenick.config.NicksSql;
import dev.rajaopak.universenick.hook.NicksHooks;
import dev.rajaopak.universenick.server.PaperServer;
import dev.rajaopak.universenick.server.SpigotServer;
import dev.rajaopak.universenick.util.NicksUtils;
import dev.rajaopak.universenick.server.ServerSoftware;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * <p>Main plugin class.</p>
 * <p>Use {@link #core()} to access core plugin utilities such as nickname storage.</p>
 * <p>Use {@link #api()} to access api utilities such as event management.</p>
 */
public final class Nicks extends JavaPlugin {

    private static Nicks                core;
    private static NicksApi             api;
    private static NicksUtils           utils;
    private static NicksConfig          config;
    private static NicksSql             sql;
    private static NicksHooks           hooks;
    private static ServerSoftware       software;
    private static StorageMethod        storage;
    private final JsonConfig            jsonConfig;
    private final Map<UUID, Component>  nickMap;
    private final Metrics               metrics;

    /**
     * Initialize plugin.
     */
    public Nicks() {
        core = this;
        api = new NicksApi();
        utils = new NicksUtils();
        config = new NicksConfig();
        sql = new NicksSql();
        hooks = new NicksHooks();
        jsonConfig = new JsonConfig(getDataFolder(), "nicknames.json");
        try {
            jsonConfig.createConfig();
        } catch (FileNotFoundException e) {
            error("Error creating nicknames.json file:");
            e.printStackTrace();
        }
        nickMap = new HashMap<>();
        // Track plugin metrics through bStats
        metrics = new Metrics(this, 8764);
    }

    /**
     * Plugin startup logic.
     */
    @Override
    public void onEnable() {
        // Get server software
        software = (Bukkit.getName().equalsIgnoreCase("Paper") && Integer.parseInt(Bukkit.getMinecraftVersion()
                .split("\\.")[1]) >= 17 )? new PaperServer() : new SpigotServer();
        log("Running on " + software().softwareName() + " server software.");

        // Load nicknames from storage
        if (getConfig().getBoolean("database-enabled")) {
            try {
                sql.connect();
            } catch (SQLException e) {
                error("Failed to connect to MySQL database:");
                e.printStackTrace();
            }
        }
        if (sql.isConnected()) {
            log("Successfully connected to MySQL database.");
            storage = new SqlStorage();
            sql.createTable();
            storage.updateNicks();
        } else {
            try {
                storage = new JsonStorage();
                JsonObject jsonObject = jsonConfig.toJsonObject();
                for (String key : jsonObject.keySet()) {
                    nickMap.put(UUID.fromString(key), GsonComponentSerializer.gson()
                            .deserializeFromTree(jsonObject.get(key)));
                }
            } catch (IOException e) {
                error("Error loading nickname data from nicknames.json file:");
                e.printStackTrace();
            }
            log("Successfully loaded nicknames from Json storage.");
        }

        // Register plugin commands
        registerCommands();

        // Initialize configuration file
        reload();

        // Custom chart to see what percentage of servers are supporting legacy
        metrics.addCustomChart(new SimplePie("supporting_legacy",
                () -> String.valueOf(Nicks.config().LEGACY_COLORS)));
        // Custom chart to see what percentage of servers are using the built in chat formatter
        metrics.addCustomChart(new SimplePie("using-chat-formatter",
                () -> String.valueOf(Nicks.config().CHAT_FORMATTER)));

        // Register events
        registerEvents(new PlayerJoin(), software);

        // Prompt to use Paper if on Spigot
        if (software instanceof SpigotServer) {
            log("This plugin will run better on PaperMC 1.17+!");
        }
    }

    /**
     * Plugin shutdown logic.
     */
    @Override
    public void onDisable() {
        // Disconnect from Sql if necessary
        if (sql.isConnected()) {
            sql.disconnect();
        }
    }

    /**
     * Register plugin commands.
     */
    @SuppressWarnings("ConstantConditions")
    private void registerCommands() {
        getCommand("nick").setExecutor(new CommandNick());
        getCommand("nick").setTabCompleter(new CommandNick());
        getCommand("nickreset").setExecutor(new CommandNickReset());
        getCommand("nickreset").setTabCompleter(new CommandNickReset());
        getCommand("nickother").setExecutor(new CommandNickOther());
        getCommand("nickother").setTabCompleter(new CommandNickOther());
        getCommand("nickcolor").setExecutor(new CommandNickColor());
        getCommand("nickcolor").setTabCompleter(new CommandNickColor());
        getCommand("nicksreload").setExecutor(new CommandNicksReload());
        getCommand("nicksreload").setTabCompleter(new CommandNicksReload());
    }

    /**
     * Register plugin events.
     */
    private void registerEvents(Listener... listeners) {
        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, this);
        }
    }

    /**
     * Get an instance of the main class. Use this for things like managing nicknames.
     *
     * @return Core.
     */
    public static Nicks core() {
        return core;
    }

    /**
     * Get the Nicks Api for accessing things such as event management and nickname lookup.
     *
     * @return Api.
     */
    public static NicksApi api() {
        return api;
    }

    /**
     * Access various utility methods used in the plugin.
     *
     * @return NicksUtils.
     */
    public static NicksUtils utils() {
        return utils;
    }

    /**
     * Easier access for plugin config options with defaults for redundancy.
     *
     * @return NicksConfig.
     */
    public static NicksConfig config() {
        return config;
    }

    /**
     * Access the plugin's SQL connection if connected.
     *
     * @return NicksSql.
     */
    public static NicksSql sql() {
        return sql;
    }

    /**
     * Check what plugins this plugin has hooked into.
     *
     * @return NicksHooks.
     */
    public static NicksHooks hooks() {
        return hooks;
    }

    /**
     * Get the server software the plugin is running on.
     * This will return either {@link PaperServer} or {@link SpigotServer}.
     *
     * @return ServerSoftware.
     */
    public static ServerSoftware software() {
        return software;
    }

    /**
     * Get the storage method the plugin is using for nickname storage.
     * This will return either {@link JsonStorage} or {@link SqlStorage}.
     *
     * @return StorageMethod.
     */
    public static StorageMethod storage() {
        return storage;
    }

    /**
     * Log an object to console. This should be a non-critical message.
     *
     * @param x Object to log.
     */
    public static void log(@NotNull Object x) {
        core().getLogger().info(x.toString());
    }

    /**
     * Log an object to console. This will only be logged if debugging is enabled.
     *
     * @param x Object to log.
     */
    public static void debug(@NotNull Object x) {
        if (config().DEBUG) {
            core().getLogger().warning(x.toString());
        }
    }

    /**
     * Log an object to console. This should only be used for plugin errors.
     *
     * @param x Object to log.
     */
    public static void error(@NotNull Object x) {
        core().getLogger().severe(x.toString());
    }

    /**
     * Reload the plugin's configuration file.
     */
    public void reload() {
        debug("Reloading plugin...");
        saveDefaultConfig();
        File configFile = new File(core().getDataFolder(), "config.yml");
        try {
            ConfigUpdater.update(core(), "config.yml", configFile, Collections.emptyList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        reloadConfig();
        config().reload();
        storage().updateNicks();
        hooks().reloadHooks();

    }

    /**
     * Access the Json config manager. This class manages reads and writes from the nicknames.json file.
     *
     * @return JsonConfig.
     */
    public JsonConfig jsonConfig() {
        return jsonConfig;
    }

    /**
     * Get the map that stores unique ids keyed to nicknames.
     *
     * @return NickMap.
     */
    public Map<UUID, Component> getNickMap() {
        return nickMap;
    }

    /**
     * Check whether or not there is a nickname stored for a unique id.
     *
     * @param uuid The unique id.
     * @return True if there is a nickname stored.
     */
    public boolean hasNick(@NotNull UUID uuid) {
        return storage.hasNick(uuid);
    }

    /**
     * Get a nickname from a player's display name.
     *
     * @param player The player.
     * @return Nickname/Display name.
     */
    public Component getDisplayName(@NotNull Player player) {
        return software.getNick(player);
    }

    /**
     * Get a nickname from storage from a unique id.
     *
     * @param uuid Unique id.
     * @return Nickname if it exists.
     */
    public Component getStoredNick(@NotNull UUID uuid) {
        return storage().getNick(uuid);
    }

    /**
     * Set a user's nickname using an online {@link Player}.
     * This will immediately be saved to Json.
     *
     * @param player Online player.
     * @param nick Player's new nickname.
     */
    public void setNick(@NotNull Player player, @NotNull Component nick) {
        software().setNick(player, nick);
    }

    /**
     * Remove a nickname from the map and from Json storage.
     * It will be asynchronously removed from the file.
     *
     * @param player The player who's nickname to remove.
     */
    public void removeNick(@NotNull Player player) {
        software().removeNick(player);
    }
}