package com.joinleave;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class VersionChecker {

    private static final String UNSUPPORTED_VERSION = "1.21";

    public static void checkVersion(Plugin plugin) {
        String version = Bukkit.getBukkitVersion();

        if (version.startsWith(UNSUPPORTED_VERSION)) {
            PluginManager pluginManager = Bukkit.getPluginManager();
            plugin.getLogger().severe("Version " + UNSUPPORTED_VERSION + " is not supported. Disabling plugin.");
            pluginManager.disablePlugin(plugin);
        } else {
            plugin.getLogger().info("Running on supported version: " + version);
        }
    }
}
