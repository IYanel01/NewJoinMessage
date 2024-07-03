package com.joinleave.Handler;

import com.joinleave.JoinleaveMessage;
import com.joinleave.Language;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GuiHandler {

    private final JoinleaveMessage plugin;

    // Constructor
    public GuiHandler(JoinleaveMessage plugin) {
        this.plugin = plugin;
    }

    public boolean handleGuiCommand(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            // Open the Language GUI
            Language language = new Language(plugin);
            language.openLanguageGUI(player);
            return true;
        } else {
            sender.sendMessage(ChatColor.RED + "This command can only be executed by a player.");
            return true;
        }
    }
}
