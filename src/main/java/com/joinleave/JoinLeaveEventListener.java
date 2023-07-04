package com.joinleave;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.entity.Player;

public class JoinLeaveEventListener implements Listener {

    private final JoinleaveMessage plugin;

    public JoinLeaveEventListener(JoinleaveMessage plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Get the player who joined
        Player player = event.getPlayer();

        // Get the join message for the player from the plugin
        String joinMessage = plugin.getMessage(player, "join", "default-join-message");

        // Set the join message
        event.setJoinMessage(ChatColor.translateAlternateColorCodes('&', joinMessage));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Get the player who left
        Player player = event.getPlayer();

        // Get the leave message for the player from the plugin
        String leaveMessage = plugin.getMessage(player, "leave", "default-leave-message");

        // Set the leave message
        event.setQuitMessage(ChatColor.translateAlternateColorCodes('&', leaveMessage));
    }
}
