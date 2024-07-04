package com.joinleave;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class LanguageConfigs {

    private final JavaPlugin plugin;

    public LanguageConfigs(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadConfigs() {
        // Ensure the directories exist
        File langDir = new File(plugin.getDataFolder(), "Lang");
        if (!langDir.exists()) {
            langDir.mkdirs();
        }

        // Load or create DataLang.yml
        File dataLangFile = new File(langDir, "DataLang.yml");
        if (!dataLangFile.exists()) {
            plugin.saveResource("Lang/DataLang.yml", false); // Copy from resources if not exists
        }

        // Load or create English.yml
        loadLanguageFile("English");

        // Load or create German.yml
        loadLanguageFile("Germany");

        // Load or create French.yml
        loadLanguageFile("French");

        // Load or create Spanish.yml
        loadLanguageFile("Spanish");

        // Load or create Italian.yml
        loadLanguageFile("Italian");

        // Load or create Chinese.yml
        loadLanguageFile("Chinese");

        // Load or create Japanese.yml
        loadLanguageFile("Japanese");

        // Load or create Korean.yml
        loadLanguageFile("Korean");

        // Load or create Russian.yml
        loadLanguageFile("Russian");
    }

    private void loadLanguageFile(String language) {
        File langDir = new File(plugin.getDataFolder(), "Lang");
        File langFile = new File(langDir, language + ".yml");

        if (!langFile.exists()) {
            plugin.saveResource("Lang/" + language + ".yml", false); // Copy from resources if not exists
        }
    }

    public YamlConfiguration getDataLangConfig() {
        File dataLangFile = new File(plugin.getDataFolder(), "Lang/DataLang.yml");
        return YamlConfiguration.loadConfiguration(dataLangFile);
    }

    public YamlConfiguration getLanguageConfig(String language) {
        File langFile = new File(plugin.getDataFolder(), "Lang/" + language + ".yml");
        return YamlConfiguration.loadConfiguration(langFile);
    }
}
