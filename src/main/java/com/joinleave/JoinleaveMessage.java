package com.joinleave;


import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.command.ConsoleCommandSender;

import java.util.*;

import org.bukkit.util.StringUtil;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class JoinleaveMessage extends JavaPlugin implements Listener {

    private FileConfiguration playersConfig;
    private File playersFile;
    private Connection connection;
    private boolean mysqlEnabled;
    private static JoinleaveMessage instance;
    private LanguageConfigs languageConfigs;
    private LanguageManager languageManager;

    public void onEnable() {

        // Check the Minecraft version
        String version = Bukkit.getBukkitVersion();
        if (version.startsWith("1.21")) {
            getLogger().severe("Version 1.21 is not supported. Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }


        instance = this;
        saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(this, this);

        // Initialize LanguageConfigs
        languageConfigs = new LanguageConfigs(this);
        languageConfigs.loadConfigs();

        // Initialize LanguageManager
        File dataLangFile = new File(getDataFolder(), "Lang/DataLang.yml");
        languageManager = new LanguageManager(dataLangFile);

        // Register events
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(languageManager), this);

        Bukkit.getScheduler().runTask(this, () -> {
            ConsoleCommandSender console = Bukkit.getConsoleSender();

            // Build the message including ASCII art and update status
            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append(ChatColor.LIGHT_PURPLE + "                           \n");
            messageBuilder.append(ChatColor.LIGHT_PURPLE + "  _   _                   _       _       __  __                                      \n");
            messageBuilder.append(ChatColor.LIGHT_PURPLE + " | \\ | |                 | |     (_)     |  \\/  |                                     \n");
            messageBuilder.append(ChatColor.LIGHT_PURPLE + " |  \\| | _____      __   | | ___  _ _ __ | \\  / | ___  ___ ___  __ _  __ _  ___  ___ \n");
            messageBuilder.append(ChatColor.LIGHT_PURPLE + " | . ` |/ _ \\ \\ /\\ / /   | |/ _ \\| | '_ \\| |\\/| |/ _ \\/ __/ __|/ _` |/ _` |/ _ \\/ __|\n");
            messageBuilder.append(ChatColor.LIGHT_PURPLE + " | |\\  |  __/\\ V  V / |__| | (_) | | | | | |  | |  __/\\__ \\__ \\ (_| | (_| |  __/\\__ \\\n");
            messageBuilder.append(ChatColor.LIGHT_PURPLE + " |_| \\_|\\___| \\_/\\_/ \\____/ \\___/|_|_| |_|_|  |_|\\___||___/___/\\__,_|\\__, |\\___||___/\n");
            messageBuilder.append(ChatColor.LIGHT_PURPLE + "                                                                      __/ |          \n");
            messageBuilder.append(ChatColor.LIGHT_PURPLE + "                                                                     |___/           \n");
            messageBuilder.append("\n");

            // Check for updates
            UpdateChecker.init(this, 110979).requestUpdateCheck().whenComplete((result, e) -> {
                if (result.requiresUpdate()) {
                    String pluginName = "                       [" + getDescription().getName() + "]";
                    String updateMessage = pluginName + " " + ChatColor.RED + "An update is available! New version: " + result.getNewestVersion();
                    messageBuilder.append(updateMessage);
                    console.sendMessage(messageBuilder.toString());
                    // Perform update handling logic here
                } else {
                    String pluginName = "                        " + getDescription().getName() + " ";
                    String upToDateMessage = pluginName + " " + ChatColor.GREEN + "Plugin is up to date!";
                    messageBuilder.append(upToDateMessage);
                    console.sendMessage(messageBuilder.toString());
                }
            });
        });

        int pluginId = 18952;
        Metrics Metrics = new Metrics(this, pluginId);
        PlayerWelcome playerWelcome = new PlayerWelcome(this);
        Bukkit.getPluginManager().registerEvents(playerWelcome, this);
        String defaultEncoding = System.getProperty("file.encoding");
        getLogger().info("Default system encoding: " + defaultEncoding);
        JoinleaveCommand joinLeaveCommand = new JoinleaveCommand(this);
        getCommand("njm").setExecutor(joinLeaveCommand);
        // Register JoinLeaveGUI listener
        JoinLeaveGUI guiListener = new JoinLeaveGUI(this);
        getServer().getPluginManager().registerEvents(guiListener, this);
        // Check for updates when the plugin is enabled

        playersFile = new File(getDataFolder(), "data.yml");
        if (!playersFile.exists()) {
            saveResource("data.yml", false);
        }
        playersConfig = YamlConfiguration.loadConfiguration(playersFile);

        mysqlEnabled = getConfig().getBoolean("mysql.enabled");
        if (mysqlEnabled) {
            if (setupMySQL()) {
                createTableIfNotExists();
            } else {
                getLogger().severe("Failed to connect to MySQL. Please check your configuration.");
            }
        }

        // Register the command executor for the '/njm' command
        getCommand("njm").setExecutor(new JoinleaveCommand(this));
    }

    @Override
    public void onDisable() {
        if (playersConfig != null) {
            savePlayersConfig();
        }
        closeMySQLConnection();
    }


    // Example method to handle player join event
    public void onPlayerJoin(Player player) {
        // Example: Set default language to English for new players
        String defaultLanguage = "English";
        languageManager.setPlayerLanguage(player, defaultLanguage);
    }

    public static JoinleaveMessage getInstance() {
        return instance;
    }

    private FileConfiguration getPlayersConfig() {
        return playersConfig;
    }
    private void savePlayersConfig() {
        try {
            playersConfig.save(playersFile);
        } catch (IOException e) {
            getLogger().severe("Failed to save data.yml: " + e.getMessage());
        }
    }

    public boolean hasCustomMessage(Player player) {
        return getMessage(player, "join", "default-join-message") != null || getMessage(player, "leave", "default-leave-message") != null;
    }

    public String getLastChange(Player player, String messageType) {
        FileConfiguration playersConfig = getPlayersConfig();

        String lastChangePath = "players." + player.getUniqueId() + ".last_change." + messageType;
        if (playersConfig.contains(lastChangePath)) {
            long lastChangeTimestamp = playersConfig.getLong(lastChangePath);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return dateFormat.format(new Date(lastChangeTimestamp));
        }

        return "N/A";
    }

    private boolean setupMySQL() {
        String host = getConfig().getString("mysql.host");
        int port = getConfig().getInt("mysql.port");
        String database = getConfig().getString("mysql.database");
        String username = getConfig().getString("mysql.username");
        String password = getConfig().getString("mysql.password");

        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false", username, password);
            return true;
        } catch (SQLException e) {
            getLogger().severe("Failed to connect to MySQL: " + e.getMessage());
            return false;
        }
    }

    private void closeMySQLConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                getLogger().severe("Failed to close MySQL connection: " + e.getMessage());
            }
        }
    }

    private void createTableIfNotExists() {
        try {
            PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS player_messages (uuid VARCHAR(36) PRIMARY KEY, join_message TEXT, leave_message TEXT)");
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            getLogger().severe("Failed to create player_messages table: " + e.getMessage());
        }
    }


    public void reloadPlugin(CommandSender sender) {
        // List of config files
        List<String> configFiles = Arrays.asList("config.yml", "firework.yml", "players.yml", "data.yml");

        for (String configFile : configFiles) {
            File file = new File(getDataFolder(), configFile);

            if (!file.exists()) {
                // Config file is missing
                saveResource(configFile, false);

                if (sender instanceof Player) {
                    // Sender is a player
                    Player player = (Player) sender;
                    player.sendMessage(ChatColor.LIGHT_PURPLE + "Checking " + configFile + "...");
                    player.sendMessage(ChatColor.DARK_PURPLE + configFile + " not found, created default configuration." + ChatColor.GREEN + " ✔");
                } else {
                    // Sender is the console
                    getLogger().info("Checking " + configFile + "...");
                    getLogger().info(configFile + " not found, created default configuration.");
                }
            }
        }

        // Reload the main configuration
        reloadConfig();

        // Reload firework.yml specifically
        File fireworkFile = new File(getDataFolder(), "firework.yml");
        if (fireworkFile.exists()) {
            YamlConfiguration fireworkConfig = new YamlConfiguration();
            try {
                fireworkConfig.load(fireworkFile);
            } catch (IOException | InvalidConfigurationException e) {
                getLogger().severe("Failed to reload firework.yml: " + e.getMessage());
            }
        } else {
            getLogger().warning("firework.yml not found to reload.");
        }

        // Check if MySQL configuration is changed
        boolean newMySQLStatus = getConfig().getBoolean("mysql.enabled");

        if (newMySQLStatus != mysqlEnabled) {
            if (newMySQLStatus) {
                // MySQL is now enabled
                closeMySQLConnection(); // Close the existing connection if any
                if (setupMySQL()) {
                    createTableIfNotExists();
                    getLogger().info("MySQL has been enabled and connected successfully.");
                } else {
                    getLogger().severe("Failed to connect to MySQL. Please check your configuration.");
                }
            } else {
                // MySQL is now disabled
                closeMySQLConnection();
                getLogger().info("MySQL has been disabled.");
            }
            mysqlEnabled = newMySQLStatus;
        }

        if (mysqlEnabled && connection == null) {
            // MySQL was enabled in the config, but connection is null (not established)
            if (setupMySQL()) {
                createTableIfNotExists();
                getLogger().info("MySQL has been enabled and connected successfully.");
            } else {
                getLogger().severe("Failed to connect to MySQL. Please check your configuration.");
            }
        }

        if (!mysqlEnabled && connection != null) {
            // MySQL was disabled in the config, but connection is not null (still established)
            closeMySQLConnection();
            getLogger().info("System is now on local files");
        }

        if (sender instanceof Player) {
            Player player = (Player) sender;
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Plugin reloaded" + ChatColor.GREEN + " ✔");
        } else {
            // Handle console reload message
            getLogger().info("Plugin reloaded successfully.");
        }
    }

    public void clearMessage(Player player, String messageType) {
        if (messageType.equals("all")) {
            // Clear all join/leave messages for the player
            setMessage(player, "join", "");
            setMessage(player, "leave", "");
        } else {
            // Clear specific join/leave message for the player
            setMessage(player, messageType, "");
        }
    }

    public void resetPlayerMessages(Player player) {
        // Reset the player's messages to default
        setMessage(player, "join", getConfig().getString("default-join-message"));
        setMessage(player, "leave", getConfig().getString("default-leave-message"));
    }

    @EventHandler
    public void handleJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();

        String joinPrefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("join-prefix"));
        String defaultJoinPrefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("default-join-prefix").replace("PLAYERNAME", playerName));
        String joinEditable = getMessage(player, "join", "default-join-message");

        String joinMessage = joinPrefix + " " + defaultJoinPrefix;
        if (getConfig().getBoolean("join-separator", true)) {
            joinMessage += ChatColor.GRAY + " - ";
        }
        joinMessage += parseMessage(joinEditable, player);

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            joinMessage = PlaceholderAPI.setPlaceholders(player, joinMessage);
        }

        event.setJoinMessage(joinMessage);
    }


    @EventHandler
    public void handleLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();

        String leavePrefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("leave-prefix"));
        String defaultLeavePrefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("default-leave-prefix").replace("PLAYERNAME", playerName));
        String leaveEditable = getMessage(player, "leave", "default-leave-message");

        String leaveMessage = leavePrefix + " " + defaultLeavePrefix;
        if (getConfig().getBoolean("leave-separator", true)) {
            leaveMessage += ChatColor.GRAY + " - ";
        }
        leaveMessage += parseMessage(leaveEditable, player);

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            leaveMessage = PlaceholderAPI.setPlaceholders(player, leaveMessage);
        }

        event.setQuitMessage(leaveMessage);
    }


    private String parseMessage(String message, Player player) {
        String parsedMessage = ChatColor.translateAlternateColorCodes('&', message).trim();

        if (parsedMessage.contains("PLAYERNAME")) {
            parsedMessage = parsedMessage.replace("PLAYERNAME", player.getName());
        }

        return parsedMessage;
    }


    private void reloadPlayersConfig() {
        playersFile = new File(getDataFolder(), "data.yml");
        playersConfig = YamlConfiguration.loadConfiguration(playersFile);
    }

    public void setMessage(Player player, String column, String message) {
        if (mysqlEnabled) {
            try {
                PreparedStatement statement = connection.prepareStatement("INSERT INTO player_messages (uuid, " + column + "_message) VALUES (?, ?) ON DUPLICATE KEY UPDATE " + column + "_message = ?");
                statement.setString(1, player.getUniqueId().toString());
                statement.setString(2, message);
                statement.setString(3, message);
                statement.executeUpdate();
                statement.close();
            } catch (SQLException e) {
                getLogger().severe("Failed to set " + column + " message for player: " + e.getMessage());
            }
        } else {
            playersConfig.set("players." + player.getUniqueId() + "." + column + "_message", message);
            updateLastChange(player, column);
            savePlayersConfig();
        }
    }

    private void updateLastChange(Player player, String messageType) {
        FileConfiguration playersConfig = getPlayersConfig();
        playersConfig.set("players." + player.getUniqueId() + ".last_change." + messageType, System.currentTimeMillis());
        savePlayersConfig();
    }

    public String getMessage(Player player, String messageType, String defaultMessageType) {
        if (mysqlEnabled) {
            try {
                PreparedStatement statement = connection.prepareStatement("SELECT " + messageType + "_message FROM player_messages WHERE uuid = ?");
                statement.setString(1, player.getUniqueId().toString());
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    String message = resultSet.getString(messageType + "_message");
                    if (message != null && !message.isEmpty()) {
                        return message;
                    }
                }

                resultSet.close();
                statement.close();
            } catch (SQLException e) {
                getLogger().severe("Failed to retrieve " + messageType + " message for player: " + e.getMessage());
            }
        } else {
            String message = playersConfig.getString("players." + player.getUniqueId() + "." + messageType + "_message");
            if (message != null && !message.isEmpty()) {
                return message;
            }
        }

        return getConfig().getString(defaultMessageType);
    }
}
