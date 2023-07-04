package com.joinleave;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerWelcome implements Listener {

    private final Plugin plugin;
    private final FileConfiguration playersConfig;
    private final FileConfiguration mainConfig;
    private final File playersFile;
    private final File configFile;
    private final Set<UUID> joinedPlayers;

    public PlayerWelcome(Plugin plugin) {
        this.plugin = plugin;
        playersFile = new File(plugin.getDataFolder(), "players.yml");
        configFile = new File(plugin.getDataFolder(), "config.yml");
        playersConfig = YamlConfiguration.loadConfiguration(playersFile);
        mainConfig = YamlConfiguration.loadConfiguration(configFile);
        joinedPlayers = new HashSet<>();
    }

    public void savePlayersConfig() {
        try {
            playersConfig.save(playersFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save players.yml: " + e.getMessage());
        }
    }

    public void reloadConfigs() {
        try {
            playersConfig.load(playersFile);
            mainConfig.load(configFile);
        } catch (IOException | org.bukkit.configuration.InvalidConfigurationException e) {
            plugin.getLogger().severe("Failed to reload configuration files: " + e.getMessage());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        if (!joinedPlayers.contains(playerId)) {
            joinedPlayers.add(playerId);

            if (!playersConfig.contains("playerCount")) {
                playersConfig.set("playerCount", 0);
            }

            int playerCount = playersConfig.getInt("playerCount") + 1;
            playersConfig.set("playerCount", playerCount);
            savePlayersConfig();

            boolean counterEnabled = mainConfig.getBoolean("New-player-counter", true);

            if (counterEnabled) {
                String welcomeMessage = ChatColor.translateAlternateColorCodes('&',
                        "&e&l[!] &7Welcome " + ChatColor.YELLOW + player.getName() + ChatColor.GRAY +
                                " to the server! &5[&5#" + playerCount + "&5]");

                Bukkit.broadcastMessage(welcomeMessage);
            }
        }
    }
}
