package com.joinleave.Handler;

import com.joinleave.JoinleaveMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class ReloadHandler {

    private final JoinleaveMessage plugin;

    // Constructor
    public ReloadHandler(JoinleaveMessage plugin) {
        this.plugin = plugin;
    }

    public boolean handleReloadCommand(CommandSender sender) {
        if (sender.hasPermission("joinleave.reload")) {
            // Reload the plugin
            plugin.reloadPlugin(sender);
            return true;
        } else {
            sender.sendMessage(ChatColor.RED + "You don't have permission to reload the plugin.");
            return true;
        }
    }
}
