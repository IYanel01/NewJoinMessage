package com.joinleave;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LanguageHandler {

    private final JoinleaveMessage plugin;
    private final Map<String, YamlConfiguration> languageFiles;

    public LanguageHandler(JoinleaveMessage plugin) {
        this.plugin = plugin;
        this.languageFiles = new HashMap<>();

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
        plugin.getLogger().info("Loaded language file: " + language.toLowerCase() + ".yml");
    }

    public String getMessage(Player player, String key) {
        String playerLanguage = getPlayerLanguage(player);
        YamlConfiguration langConfig = languageFiles.get(playerLanguage.toLowerCase());
        if (langConfig != null) {
            String message = langConfig.getString(key);
            if (message != null) {
                return ChatColor.translateAlternateColorCodes('&', message);
            } else {
                plugin.getLogger().warning("Message with key '" + key + "' not found in language file for " + playerLanguage);
                return ChatColor.RED + "Message not found"; // Fallback message
            }
        } else {
            plugin.getLogger().warning("Language file not found for language " + playerLanguage);
            return ChatColor.RED + "Language file not found"; // Fallback message
        }
    }

    private String getPlayerLanguage(Player player) {
        if (player == null) {
            return "english"; // Default to English if player is null (for console commands)
        }

        File dataLangFile = new File(plugin.getDataFolder(), "Lang/DataLang.yml");
        YamlConfiguration dataLangConfig = YamlConfiguration.loadConfiguration(dataLangFile);

        String playerUUID = player.getUniqueId().toString();
        String language = dataLangConfig.getString(playerUUID + ".Language", "english").toLowerCase();

        // Debugging output
        plugin.getLogger().info("Player UUID: " + playerUUID);
        plugin.getLogger().info("Selected Language: " + language);

        // Check if the loaded language file exists in our set of language files
        if (!languageFiles.containsKey(language)) {
            plugin.getLogger().warning("Language file '" + language + "' specified in DataLang.yml not found, defaulting to English.");
            return "english"; // Default to English if specified language file does not exist
        }

        return language;
    }
}
