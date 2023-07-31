package com.joinleave;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.util.StringUtil;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JoinleaveMessage extends JavaPlugin implements Listener {

    private FileConfiguration playersConfig;
    private File playersFile;
    private Connection connection;
    private boolean mysqlEnabled;

    private final String pluginVersion = "1.0.0"; // Replace with your plugin's current version

    private final String updateURL = "https://www.spigotmc.org/resources/110979/"; // Replace with your plugin's update URL


    public void onEnable() {
        saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(this, this);
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
        checkForUpdates();

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
        savePlayersConfig();
        closeMySQLConnection();
    }

    private void checkForUpdates() {
        // Retrieve the latest version of your plugin from your update source
        String latestVersion = ""; // Replace with your logic to fetch the latest version

        // Retrieve the player's current plugin version
        String playerVersion = getDescription().getVersion();

        if (playerVersion.equals(latestVersion)) {
            // Player has the latest version
            getLogger().info("You are using the latest version of the plugin.");
        } else {
            // Player has an outdated version
            getLogger().warning("An update is available for the plugin!");
            getLogger().warning("Please update to version " + latestVersion + ".");
            getLogger().warning("Download the latest version from: " + updateURL);
            getLogger().warning("Current version: " + playerVersion);

            // Send clickable message to player
            Player[] onlinePlayers = getServer().getOnlinePlayers().toArray(new Player[0]);
            for (Player player : onlinePlayers) {
                if (player.hasPermission("yourplugin.update")) { // Adjust the permission node according to your needs
                    player.sendMessage(ChatColor.RED + "An update is available for the plugin!");
                    player.sendMessage(ChatColor.RED + "Please update to version " + latestVersion + ".");
                    player.sendMessage(ChatColor.RED + "Download the latest version from: " + ChatColor.BLUE + updateURL);
                }
            }
        }
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



    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            List<String> subCommands = new ArrayList<>();
            subCommands.add("setplayer");
            subCommands.add("set");
            subCommands.add("gui");
            subCommands.add("clear");
            subCommands.add("reload");
            StringUtil.copyPartialMatches(args[0], subCommands, completions);
        } else if (args.length == 2 && args[0].equalsIgnoreCase("setplayer")) {
            List<String> playerNames = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                playerNames.add(player.getName());
            }
            StringUtil.copyPartialMatches(args[1], playerNames, completions);
        } else if (args.length == 3 && args[0].equalsIgnoreCase("setplayer")) {
            List<String> messageTypes = new ArrayList<>();
            messageTypes.add("join");
            messageTypes.add("leave");
            StringUtil.copyPartialMatches(args[2], messageTypes, completions);
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("clear"))) {
            List<String> messageTypes = new ArrayList<>();
            messageTypes.add("join");
            messageTypes.add("leave");
            StringUtil.copyPartialMatches(args[1], messageTypes, completions);
        }

        Collections.sort(completions);
        return completions;
    }




    public void reloadPlugin(CommandSender sender) {
        reloadConfig();
        reloadPlayersConfig();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Plugin reloaded" + ChatColor.GREEN + " ✔");
        } else {
            // Handle console reload message
            getLogger().info("Plugin reloaded successfully.");
        }



        closeMySQLConnection();
        mysqlEnabled = getConfig().getBoolean("mysql.enabled");
        if (mysqlEnabled) {
            if (setupMySQL()) {
                createTableIfNotExists();
            } else {
                getLogger().severe("Failed to connect to MySQL. Please check your configuration.");
            }
        }
        if (sender instanceof Player) {
            Player player = (Player) sender;
            player.sendMessage(ChatColor.DARK_PURPLE + "Mysql has been reloaded" + ChatColor.GREEN + " ✔");
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

        String joinMessage = joinPrefix + " " + defaultJoinPrefix + ChatColor.GRAY + " - " + parseMessage(joinEditable, player);

        event.setJoinMessage(joinMessage);
    }

    @EventHandler
    public void handleLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();

        String leavePrefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("leave-prefix"));
        String defaultLeavePrefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("default-leave-prefix").replace("PLAYERNAME", playerName));
        String leaveEditable = getMessage(player, "leave", "default-leave-message");

        String leaveMessage = leavePrefix + " " + defaultLeavePrefix + ChatColor.GRAY + " - " + parseMessage(leaveEditable, player);

        event.setQuitMessage(leaveMessage);
    }

    private String parseMessage(String message, Player player) {
        String parsedMessage = ChatColor.translateAlternateColorCodes('&', message);

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


