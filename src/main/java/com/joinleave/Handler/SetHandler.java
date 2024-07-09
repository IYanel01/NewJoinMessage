package com.joinleave.Handler;

import com.joinleave.JoinleaveMessage;
import com.joinleave.LanguageHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetHandler {

    private final JoinleaveMessage plugin;
    private final LanguageHandler languageHandler;

    // Constructor
    public SetHandler(JoinleaveMessage plugin) {
        this.plugin = plugin;
        this.languageHandler = new LanguageHandler(plugin);
    }

    public boolean handleSetCommand(CommandSender sender, String[] args) {
        if (args.length >= 3 && args[0].equalsIgnoreCase("set")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                String messageType = args[1].toLowerCase();
                String message = String.join(" ", args).substring(args[0].length() + args[1].length() + 2);

                if (messageType.equals("join") || messageType.equals("leave")) {
                    if ((messageType.equals("join") && !player.hasPermission("joinleave.set.join")) ||
                            (messageType.equals("leave") && !player.hasPermission("joinleave.set.leave"))) {
                        sender.sendMessage(languageHandler.getMessage(player, "no_permission").replace("%type%", messageType));
                        return true;
                    }

                    plugin.setMessage(player, messageType, message);
                    sender.sendMessage(languageHandler.getMessage(player, "set_success").replace("%type%", messageType));
                } else {
                    sender.sendMessage(languageHandler.getMessage(player, "invalid_type"));
                }
                return true;
            } else {
                sender.sendMessage(languageHandler.getMessage(null, "console_error"));
                return true;
            }
        }
        return false;
    }
}
