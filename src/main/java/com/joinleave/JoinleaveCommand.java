package com.joinleave;

import com.joinleave.Handler.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JoinleaveCommand implements CommandExecutor, TabCompleter {

    private final JoinleaveMessage plugin;
    private final MessageHandler messageHandler;
    private final SetPlayerHandler setPlayerHandler;
    private final SetHandler setHandler;
    private final GuiHandler guiHandler;
    private final ClearHandler clearHandler;
    private final ReloadHandler reloadHandler;

    // Constructor
    public JoinleaveCommand(JoinleaveMessage plugin) {
        this.plugin = plugin;
        this.messageHandler = new MessageHandler(plugin);
        this.setPlayerHandler = new SetPlayerHandler(plugin);
        this.setHandler = new SetHandler(plugin);
        this.guiHandler = new GuiHandler(plugin);
        this.clearHandler = new ClearHandler(plugin);
        this.reloadHandler = new ReloadHandler(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            displayHelpMenu(sender);
            return true;
        }

        if (args.length >= 2 && args[0].equalsIgnoreCase("info")) {
            return messageHandler.handleInfoCommand(sender, args);
        }

        if (args.length >= 4 && args[0].equalsIgnoreCase("setplayer")) {
            return setPlayerHandler.handleSetPlayerCommand(sender, args);
        }

        if (args.length >= 3 && args[0].equalsIgnoreCase("set")) {
            return setHandler.handleSetCommand(sender, args);
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("gui")) {
            return guiHandler.handleGuiCommand(sender);
        }

        if (args.length >= 3 && args[0].equalsIgnoreCase("clear")) {
            return clearHandler.handleClearCommand(sender, args);
        }

        if (args[0].equalsIgnoreCase("reload")) {
            return reloadHandler.handleReloadCommand(sender);
        }

        // Display help menu
        displayHelpMenu(sender);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            List<String> subCommands = new ArrayList<>();
            subCommands.add("setplayer");
            subCommands.add("set");
            subCommands.add("gui");
            subCommands.add("clear");
            subCommands.add("reload");
            subCommands.add("info");
            StringUtil.copyPartialMatches(args[0], subCommands, completions);
        } else if (args.length == 2 && args[0].equalsIgnoreCase("setplayer")) {
            List<String> playerNames = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                playerNames.add(player.getName());
            }
            StringUtil.copyPartialMatches(args[1], playerNames, completions);
        } else if (args.length == 3 && args[0].equalsIgnoreCase("setplayer")) {
            List<String> messageTypes = new ArrayList<>();
            messageTypes.add("join");
            messageTypes.add("leave");
            StringUtil.copyPartialMatches(args[2], messageTypes, completions);
        } else if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
            List<String> messageTypes = new ArrayList<>();
            messageTypes.add("join");
            messageTypes.add("leave");
            completions = messageTypes;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("info")) {
            List<String> playerNames = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                playerNames.add(player.getName());
            }
            StringUtil.copyPartialMatches(args[1], playerNames, completions);
        } else if (args.length == 2 && args[0].equalsIgnoreCase("clear")) {
            List<String> messageTypes = new ArrayList<>();
            messageTypes.add("all");
            messageTypes.add("join");
            messageTypes.add("leave");
            StringUtil.copyPartialMatches(args[1], messageTypes, completions);
        }

        Collections.sort(completions);
        return completions;
    }

    private void displayHelpMenu(CommandSender sender) {
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.DARK_PURPLE + "         NewJoinMessage Help");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.GRAY + "    JoinMessage version: " + ChatColor.GREEN + "2.5 Beta" + ChatColor.GREEN + " ✔");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.GRAY + "    Made With " + ChatColor.RED + "❤" + ChatColor.GRAY + " by Yanel");

        boolean hasSetJoinPermission = sender.hasPermission("joinleave.set.join");
        boolean hasSetLeavePermission = sender.hasPermission("joinleave.set.leave");
        boolean hasSetPlayerPermission = sender.hasPermission("joinleave.setplayer");
        boolean hasClearPlayerPermission = sender.hasPermission("joinleave.clearplayer");
        boolean hasReloadPermission = sender.hasPermission("joinleave.reload");
        boolean hasGuiPermission = sender.hasPermission("joinleave.gui");
        boolean hasInfoPermission = sender.hasPermission("joinleave.Info");
        boolean hasAnyPermission = hasSetJoinPermission || hasSetLeavePermission || hasSetPlayerPermission || hasClearPlayerPermission || hasReloadPermission || hasGuiPermission || hasInfoPermission;
        if (!hasAnyPermission) {
            sender.sendMessage(" ");
            sender.sendMessage(ChatColor.RED + "Sorry, it seems that you don't have any permissions. Please contact an administrator or obtain a rank to get permissions.");
            sender.sendMessage(" ");
        } else {
            if (hasSetJoinPermission) {
                sender.sendMessage(" ");
                sender.sendMessage(ChatColor.LIGHT_PURPLE + "  /njm set join <message>" + ChatColor.DARK_PURPLE + " - Set your join message");
            }
            if (hasSetLeavePermission) {
                sender.sendMessage(" ");
                sender.sendMessage(ChatColor.LIGHT_PURPLE + "  /njm set leave <message>" + ChatColor.DARK_PURPLE + " - Set your leave message");
            }
            if (hasSetPlayerPermission) {
                sender.sendMessage(" ");
                sender.sendMessage(ChatColor.LIGHT_PURPLE + "  /njm setplayer <player> join/leave <message>" + ChatColor.DARK_PURPLE + " - Set another player's join message");
            }
            if (hasClearPlayerPermission) {
                sender.sendMessage(" ");
                sender.sendMessage(ChatColor.LIGHT_PURPLE + "  /njm clear all/join/leave [player]" + ChatColor.DARK_PURPLE + " - To clear the join/leave message for players");
            }
            if (hasGuiPermission) {
                sender.sendMessage(" ");
                sender.sendMessage(ChatColor.LIGHT_PURPLE + "  /njm gui" + ChatColor.DARK_PURPLE + " - To open the GUI");
            }
            if (hasInfoPermission) {
                sender.sendMessage(" ");
                sender.sendMessage(ChatColor.LIGHT_PURPLE + "  /njm Info [Player]" + ChatColor.DARK_PURPLE + " - To See Another players Info");
            }
            if (hasReloadPermission) {
                sender.sendMessage(" ");
                sender.sendMessage(ChatColor.LIGHT_PURPLE + "  /njm reload" + ChatColor.DARK_PURPLE + " - Reload the plugin");

            }
        }

        Player player = (sender instanceof Player) ? (Player) sender : null;
        if (player != null) {
            String joinMessage = plugin.getMessage(player, "join", "default-join-message");
            String leaveMessage = plugin.getMessage(player, "leave", "default-leave-message");
            sender.sendMessage(" ");
            sender.sendMessage(ChatColor.GREEN + "Your current Join Message: " + ChatColor.RESET + (joinMessage.equals(plugin.getConfig().getString("default-join-message")) ? ChatColor.GRAY + "Using the default message" : joinMessage));
            sender.sendMessage(ChatColor.GREEN + "Your current Leave Message: " + ChatColor.RESET + (leaveMessage.equals(plugin.getConfig().getString("default-leave-message")) ? ChatColor.GRAY + "Using the default message" : leaveMessage));
        }

        sender.sendMessage(ChatColor.LIGHT_PURPLE + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
}
