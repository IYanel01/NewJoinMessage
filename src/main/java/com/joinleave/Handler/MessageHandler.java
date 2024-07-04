package com.joinleave.Handler;

import com.joinleave.JoinleaveMessage;
import com.joinleave.LangMessageChanger;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageHandler {

    private final JoinleaveMessage plugin;
    private final LangMessageChanger langMessageChanger;

    // Constructor
    public MessageHandler(JoinleaveMessage plugin) {
        this.plugin = plugin;
        this.langMessageChanger = new LangMessageChanger(plugin);
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

            // Send join/leave info using LangMessageChanger
            langMessageChanger.sendJoinLeaveInfo(sender, targetPlayer.getName(), joinMessage, leaveMessage, lastJoinChange, lastLeaveChange);

            return true;
        }
        return false;
    }
}
