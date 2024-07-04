package com.joinleave;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final LanguageManager languageManager;

    public PlayerJoinListener(LanguageManager languageManager) {
        this.languageManager = languageManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Set the default language to English for new players
        languageManager.setPlayerLanguage(event.getPlayer(), "English");
    }
}
