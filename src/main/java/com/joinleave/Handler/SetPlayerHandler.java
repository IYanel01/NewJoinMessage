package com.joinleave.Handler;

import com.joinleave.JoinleaveMessage;
import com.joinleave.LanguageHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetPlayerHandler {

    private final JoinleaveMessage plugin;
    private final LanguageHandler languageHandler;

    // Constructor
    public SetPlayerHandler(JoinleaveMessage plugin, LanguageHandler languageHandler) {
        this.plugin = plugin;
        this.languageHandler = languageHandler;
    }

    public boolean handleSetPlayerCommand(CommandSender sender, String[] args) {
        if (args.length >= 4 && args[0].equalsIgnoreCase("setplayer")) {
            if (!sender.hasPermission("joinleave.setplayer")) {
                sender.sendMessage(languageHandler.getMessage(sender instanceof Player ? (Player) sender : null, "setplayer_no_permission"));
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);

            if (target == null) {
                sender.sendMessage(languageHandler.getMessage(sender instanceof Player ? (Player) sender : null, "setplayer_player_not_found"));
                return true;
            }

            String messageType = args[2].toLowerCase();
            String message = String.join(" ", args).substring(args[0].length() + args[1].length() + args[2].length() + 3);

            if (messageType.equals("join") || messageType.equals("leave")) {
                plugin.setMessage(target, messageType, message);
                sender.sendMessage(languageHandler.getMessage(sender instanceof Player ? (Player) sender : null, "setplayer_success")
                        .replace("%type%", messageType)
                        .replace("%player%", target.getName()));
            } else {
                sender.sendMessage(languageHandler.getMessage(sender instanceof Player ? (Player) sender : null, "setplayer_invalid_type"));
            }
            return true;
        }
        return false;
    }
}
