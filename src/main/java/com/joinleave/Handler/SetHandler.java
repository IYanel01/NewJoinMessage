package com.joinleave.Handler;

import com.joinleave.JoinleaveMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetHandler {

    private final JoinleaveMessage plugin;

    // Constructor
    public SetHandler(JoinleaveMessage plugin) {
        this.plugin = plugin;
    }

    public boolean handleSetCommand(CommandSender sender, String[] args) {
        if (args.length >= 3 && args[0].equalsIgnoreCase("set")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                String messageType = args[1].toLowerCase();
                String message = ChatColor.translateAlternateColorCodes('&', String.join(" ", args).substring(args[0].length() + args[1].length() + 2));

                if (messageType.equals("join") || messageType.equals("leave")) {
                    if ((messageType.equals("join") && !player.hasPermission("joinleave.set.join")) ||
                            (messageType.equals("leave") && !player.hasPermission("joinleave.set.leave"))) {
                        sender.sendMessage(ChatColor.RED + "You don't have permission to set " + messageType + " messages.");
                        return true;
                    }

                    plugin.setMessage(player, messageType, message);
                    sender.sendMessage(ChatColor.GREEN + "Successfully set your " + messageType + " message.");
                } else {
                    sender.sendMessage(ChatColor.RED + "Invalid message type. Use 'join' or 'leave'.");
                }
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "This command can only be executed by a player.");
                return true;
            }
        }
        return false;
    }
}
