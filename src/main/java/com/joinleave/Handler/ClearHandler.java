package com.joinleave.Handler;

import com.joinleave.JoinleaveMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearHandler {

    private final JoinleaveMessage plugin;

    // Constructor
    public ClearHandler(JoinleaveMessage plugin) {
        this.plugin = plugin;
    }

    public boolean handleClearCommand(CommandSender sender, String[] args) {
        if (args.length >= 3 && args[0].equalsIgnoreCase("clear")) {
            if (!sender.hasPermission("joinleave.clearplayer")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to clear join/leave messages for other players.");
                return true;
            }

            String messageType = args[1].toLowerCase();
            Player target = Bukkit.getPlayer(args[2]);

            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Player not found.");
                return true;
            }

            if (messageType.equals("all") || messageType.equals("join") || messageType.equals("leave")) {
                plugin.clearMessage(target, messageType);
                sender.sendMessage(ChatColor.GREEN + "Successfully cleared the " + messageType + " message for player " + target.getName());

                if (messageType.equals("all")) {
                    plugin.resetPlayerMessages(target);
                    sender.sendMessage(ChatColor.GREEN + "Player " + target.getName() + " has been reset to default messages.");
                }

            } else {
                sender.sendMessage(ChatColor.RED + "Invalid message type. Use 'all', 'join', or 'leave'.");
            }
            return true;
        }
        return false;
    }
}
