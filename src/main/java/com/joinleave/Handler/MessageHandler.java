package com.joinleave.Handler;

import com.joinleave.JoinleaveMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageHandler {

    private final JoinleaveMessage plugin;

    // Constructor
    public MessageHandler(JoinleaveMessage plugin) {
        this.plugin = plugin;
    }

    public boolean handleInfoCommand(CommandSender sender, String[] args) {
        if (args.length >= 2 && args[0].equalsIgnoreCase("info")) {
            String playerName = args[1];
            Player targetPlayer = plugin.getServer().getPlayerExact(playerName);

            if (targetPlayer == null) {
                sender.sendMessage(ChatColor.RED + "Player not found.");
                return true;
            }

            String joinMessage = plugin.getMessage(targetPlayer, "join", "default-join-message");
            String leaveMessage = plugin.getMessage(targetPlayer, "leave", "default-leave-message");
            String lastJoinChange = plugin.getLastChange(targetPlayer, "join");
            String lastLeaveChange = plugin.getLastChange(targetPlayer, "leave");

            sender.sendMessage(ChatColor.LIGHT_PURPLE + "Join/Leave Message Info for " + targetPlayer.getName());
            sender.sendMessage(ChatColor.DARK_PURPLE + "Join Message: " + ChatColor.RESET + (joinMessage.equals(plugin.getConfig().getString("default-join-message")) ? ChatColor.GREEN + "Default Message" : joinMessage));
            sender.sendMessage(ChatColor.DARK_PURPLE + "Leave Message: " + ChatColor.RESET + (leaveMessage.equals(plugin.getConfig().getString("default-leave-message")) ? ChatColor.GREEN + "Default Message" : leaveMessage));
            sender.sendMessage(ChatColor.DARK_PURPLE + "Last Join Message Change: " + ChatColor.RESET + lastJoinChange);
            sender.sendMessage(ChatColor.DARK_PURPLE + "Last Leave Message Change: " + ChatColor.RESET + lastLeaveChange);

            return true;
        }
        return false;
    }
}
