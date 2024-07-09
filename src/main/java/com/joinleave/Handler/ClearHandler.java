package com.joinleave.Handler;

import com.joinleave.JoinleaveMessage;
import com.joinleave.LanguageHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearHandler {

    private final JoinleaveMessage plugin;
    private final LanguageHandler languageHandler;

    // Constructor
    public ClearHandler(JoinleaveMessage plugin, LanguageHandler languageHandler) {
        this.plugin = plugin;
        this.languageHandler = languageHandler;
    }

    public boolean handleClearCommand(CommandSender sender, String[] args) {
        if (args.length >= 3 && args[0].equalsIgnoreCase("clear")) {
            if (!sender.hasPermission("joinleave.clearplayer")) {
                sender.sendMessage(languageHandler.getMessage(null, "clear_no_permission"));
                return true;
            }

            String messageType = args[1].toLowerCase();
            Player target = Bukkit.getPlayer(args[2]);

            if (target == null) {
                sender.sendMessage(languageHandler.getMessage(null, "clear_player_not_found"));
                return true;
            }

            if (messageType.equals("all") || messageType.equals("join") || messageType.equals("leave")) {
                plugin.clearMessage(target, messageType);
                sender.sendMessage(languageHandler.getMessage(target, "clear_success")
                        .replace("%type%", messageType)
                        .replace("%player%", target.getName()));

                if (messageType.equals("all")) {
                    plugin.resetPlayerMessages(target);
                    sender.sendMessage(languageHandler.getMessage(target, "clear_reset_success")
                            .replace("%player%", target.getName()));
                }

            } else {
                sender.sendMessage(languageHandler.getMessage(target, "clear_invalid_type"));
            }
            return true;
        }
        return false;
    }
}
