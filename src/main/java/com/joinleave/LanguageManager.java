package com.joinleave;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;

public class LanguageManager {

    private final File dataLangFile;
    private final FileConfiguration dataLangConfig;

    public LanguageManager(File dataLangFile) {
        this.dataLangFile = dataLangFile;
        this.dataLangConfig = YamlConfiguration.loadConfiguration(dataLangFile);
    }

    public void setPlayerLanguage(Player player, String language) {
        String uuidStr = player.getUniqueId().toString();

        // Check if language is already set for the player
        if (!isLanguageSet(player)) {
            dataLangConfig.set(uuidStr + ".Language", language);

            try {
                dataLangConfig.save(dataLangFile);
            } catch (IOException e) {
                JoinleaveMessage.getInstance().getLogger().log(Level.SEVERE, "Failed to save player language data for " + player.getName(), e);
            }
        }
    }

    public String getPlayerLanguage(Player player) {
        String uuidStr = player.getUniqueId().toString();
        return dataLangConfig.getString(uuidStr + ".Language", "English"); // Default to English if not set
    }

    public boolean isLanguageSet(Player player) {
        String uuidStr = player.getUniqueId().toString();
        return dataLangConfig.contains(uuidStr + ".Language");
    }
}
