package com.joinleave;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LangMessageChanger {

    private final JoinleaveMessage plugin;
    private final Map<String, YamlConfiguration> languageFiles;

    public LangMessageChanger(JoinleaveMessage plugin) {
        this.plugin = plugin;
        this.languageFiles = new HashMap<>();

        // Load language files
        loadLanguageFile("english");
        loadLanguageFile("germany");
        loadLanguageFile("french");
        loadLanguageFile("spanish");
        loadLanguageFile("italian");
        loadLanguageFile("chinese");
        loadLanguageFile("japanese");
        loadLanguageFile("korean");
        loadLanguageFile("russian");
    }

    private void loadLanguageFile(String language) {
        File langFile = new File(plugin.getDataFolder(), "Lang/" + language.toLowerCase() + ".yml");
        if (!langFile.exists()) {
            plugin.saveResource("Lang/" + language.toLowerCase() + ".yml", false);
        }
        YamlConfiguration langConfig = YamlConfiguration.loadConfiguration(langFile);
        languageFiles.put(language.toLowerCase(), langConfig);
    }

    public void sendJoinLeaveInfo(CommandSender sender, String playerName, String joinMessage, String leaveMessage, String lastJoinChange, String lastLeaveChange) {
        String playerLanguage = getPlayerLanguage(sender);
        YamlConfiguration langConfig = languageFiles.get(playerLanguage.toLowerCase());

        // Fetch messages from language file
        String title = replacePlaceholders(langConfig.getString("join_leave_info.title"), playerName, joinMessage, leaveMessage, lastJoinChange, lastLeaveChange);
        String joinMsg = replacePlaceholders(langConfig.getString("join_leave_info.join_message"), playerName, joinMessage, leaveMessage, lastJoinChange, lastLeaveChange);
        String leaveMsg = replacePlaceholders(langConfig.getString("join_leave_info.leave_message"), playerName, joinMessage, leaveMessage, lastJoinChange, lastLeaveChange);
        String lastJoin = replacePlaceholders(langConfig.getString("join_leave_info.last_join_change"), playerName, joinMessage, leaveMessage, lastJoinChange, lastLeaveChange);
        String lastLeave = replacePlaceholders(langConfig.getString("join_leave_info.last_leave_change"), playerName, joinMessage, leaveMessage, lastJoinChange, lastLeaveChange);

        // Send messages to sender
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', title));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', joinMsg));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', leaveMsg));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', lastJoin));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', lastLeave));
    }

    private String replacePlaceholders(String message, String playerName, String joinMessage, String leaveMessage, String lastJoinChange, String lastLeaveChange) {
        if (message == null) {
            return "";
        }

        // Replace custom placeholders
        message = message.replace("%player%", playerName)
                .replace("%joinMessage%", joinMessage)
                .replace("%leaveMessage%", leaveMessage)
                .replace("%lastJoinChange%", lastJoinChange)
                .replace("%lastLeaveChange%", lastLeaveChange);

        // Replace PlaceholderAPI placeholders if available
        if (message.contains("%") && plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            message = PlaceholderAPI.setPlaceholders(null, message);
        }

        return message;
    }

    private String getPlayerLanguage(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            File dataLangFile = new File(plugin.getDataFolder(), "Lang/DataLang.yml");
            YamlConfiguration dataLangConfig = YamlConfiguration.loadConfiguration(dataLangFile);

            String playerUUID = player.getUniqueId().toString();
            String playerLanguage = dataLangConfig.getString(playerUUID + ".Language", "english").toLowerCase(); // Default to English if language not found

            // Debug print
            plugin.getLogger().info("Player UUID: " + playerUUID + ", Language: " + playerLanguage);

            return playerLanguage;
        } else {
            return "english"; // Default language for console
        }
    }
}
