package com.joinleave.Handler;

import com.joinleave.JoinleaveMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetPlayerHandler {

    private final JoinleaveMessage plugin;

    // Constructor
    public SetPlayerHandler(JoinleaveMessage plugin) {
        this.plugin = plugin;
    }

    public boolean handleSetPlayerCommand(CommandSender sender, String[] args) {
        if (args.length >= 4 && args[0].equalsIgnoreCase("setplayer")) {
            if (!sender.hasPermission("joinleave.setplayer")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to set join/leave messages for other players.");
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);

            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Player not found.");
                return true;
            }

            String messageType = args[2].toLowerCase();
            String message = ChatColor.translateAlternateColorCodes('&', String.join(" ", args).substring(args[0].length() + args[1].length() + args[2].length() + 3));

            if (messageType.equals("join") || messageType.equals("leave")) {
                plugin.setMessage(target, messageType, message);
                sender.sendMessage(ChatColor.GREEN + "Successfully set the " + messageType + " message for player " + target.getName());
            } else {
                sender.sendMessage(ChatColor.RED + "Invalid message type. Use 'join' or 'leave'.");
            }
            return true;
        }
        return false;
    }
}
