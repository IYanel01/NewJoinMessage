package com.joinleave;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.Color;
import java.util.Arrays;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class PlayerWelcome implements Listener {

    private final Plugin plugin;
    private final FileConfiguration playersConfig;
    private final FileConfiguration fireworksConfig;
    private final File playersFile;
    private final File fireworksFile;
    private final Set<UUID> joinedPlayers;

    public PlayerWelcome(Plugin plugin) {
        this.plugin = plugin;
        playersFile = new File(plugin.getDataFolder(), "players.yml");
        playersConfig = YamlConfiguration.loadConfiguration(playersFile);
        fireworksFile = new File(plugin.getDataFolder(), "firework.yml");
        fireworksConfig = YamlConfiguration.loadConfiguration(fireworksFile);
        joinedPlayers = new HashSet<>();
        initializePlayersConfig();
        initializeFireworksConfig();
    }

    public void savePlayersConfig() {
        try {
            playersConfig.save(playersFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save players.yml: " + e.getMessage());
        }
    }

    public void saveFireworksConfig() {
        try {
            fireworksConfig.save(fireworksFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save firework.yml: " + e.getMessage());
        }
    }

    public void initializePlayersConfig() {
        if (!playersFile.exists()) {
            try {
                if (playersFile.createNewFile()) {
                    plugin.getLogger().info("players.yml created successfully.");
                } else {
                    plugin.getLogger().severe("Failed to create players.yml.");
                }
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to create players.yml: " + e.getMessage());
            }
        }

        if (!playersConfig.isConfigurationSection("players")) {
            playersConfig.createSection("players");
            savePlayersConfig();
        } else {
            // Load already joined players' UUIDs
            for (String key : playersConfig.getConfigurationSection("players").getKeys(false)) {
                joinedPlayers.add(UUID.fromString(key));
            }
        }
    }

    public void initializeFireworksConfig() {
        if (!fireworksFile.exists()) {
            try {
                if (fireworksFile.createNewFile()) {
                    plugin.getLogger().info("firework.yml created successfully.");
                    fireworksConfig.createSection("fireworks"); // Create the 'fireworks' section
                    fireworksConfig.set("fireworks.firework-enabled", true); // Add the 'firework-enabled' default value
                    fireworksConfig.set("fireworks.type", "BALL"); // Add the 'type' default value
                    fireworksConfig.set("fireworks.power", 1); // Add the 'power' default value
                    fireworksConfig.set("fireworks.colors", Arrays.asList("FF0000", "0000FF")); // Add the 'colors' default value
                    fireworksConfig.set("fireworks.fade-colors", Arrays.asList("00FF00")); // Add the 'fade-colors' default value
                    saveFireworksConfig(); // Save the updated configuration
                } else {
                    plugin.getLogger().severe("Failed to create firework.yml.");
                }
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to create firework.yml: " + e.getMessage());
            }
        }

        if (!fireworksConfig.isConfigurationSection("fireworks")) {
            fireworksConfig.createSection("fireworks");
            saveFireworksConfig();
        }
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        if (!joinedPlayers.contains(playerId)) {
            joinedPlayers.add(playerId);

            // Check if the welcome message is enabled in the config
            boolean welcomeMessageEnabled = playersConfig.getBoolean("welcome-message-enabled", true);
            if (welcomeMessageEnabled) {
                int playerCount = playersConfig.getConfigurationSection("players").getKeys(false).size();
                String welcomeMessage = ChatColor.translateAlternateColorCodes('&',
                        "&e&l[!] &7Welcome " + ChatColor.YELLOW + player.getName() + ChatColor.GRAY +
                                " to the server! You are the " + ChatColor.LIGHT_PURPLE + "#" + playerCount + ChatColor.GRAY + " player!");

                Bukkit.broadcastMessage(welcomeMessage);
            }

            playersConfig.set("players." + playerId + ".name", player.getName());
            savePlayersConfig();

            // Firework display
            boolean fireworkEnabled = fireworksConfig.getBoolean("fireworks.enabled", true); // Use 'enabled' instead of 'firework-enabled'
            if (fireworkEnabled) {
                String fireworkTypeString = fireworksConfig.getString("fireworks.type", "BALL");
                FireworkEffect.Type fireworkType;
                try {
                    fireworkType = FireworkEffect.Type.valueOf(fireworkTypeString);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid firework type specified in the configuration. Defaulting to BALL.");
                    fireworkType = FireworkEffect.Type.BALL;
                }
                int fireworkPower = fireworksConfig.getInt("fireworks.power", 1);

                Location location = player.getLocation();
                launchFirework(location, fireworkType, fireworkPower);
            }
        }
    }


    private void launchFirework(Location location, FireworkEffect.Type type, int power) {
        Firework firework = location.getWorld().spawn(location, Firework.class);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();

        FireworkEffect.Builder builder = FireworkEffect.builder();
        List<Color> colors = new ArrayList<>();
        List<Color> fadeColors = new ArrayList<>();

        // Retrieve firework colors from the config
        List<String> colorStrings = fireworksConfig.getStringList("fireworks.colors");
        for (String colorString : colorStrings) {
            try {
                Color color = Color.fromRGB(Integer.parseInt(colorString, 16));
                colors.add(color);
            } catch (NumberFormatException e) {
                plugin.getLogger().warning("Invalid color specified in the firework configuration: " + colorString);
            }
        }

        // Retrieve firework fade colors from the config
        List<String> fadeColorStrings = fireworksConfig.getStringList("fireworks.fade-colors");
        for (String fadeColorString : fadeColorStrings) {
            try {
                Color fadeColor = Color.fromRGB(Integer.parseInt(fadeColorString, 16));
                fadeColors.add(fadeColor);
            } catch (NumberFormatException e) {
                plugin.getLogger().warning("Invalid fade color specified in the firework configuration: " + fadeColorString);
            }
        }

        // Set firework effect colors
        if (!colors.isEmpty()) {
            builder.withColor(colors.toArray(new Color[0]));
        }
        if (!fadeColors.isEmpty()) {
            builder.withFade(fadeColors.toArray(new Color[0]));
        }

        // Set firework effect type
        builder.with(type);

        // Build the firework effect
        FireworkEffect fireworkEffect = builder.build();

        fireworkMeta.addEffect(fireworkEffect);
        fireworkMeta.setPower(power);
        firework.setFireworkMeta(fireworkMeta);
    }
}
