package com.joinleave;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;

public class JoinLeaveGUI implements Listener {

    private final JavaPlugin plugin;

    public JoinLeaveGUI(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private ItemStack createGrayGlass() {
        ItemStack grayGlass = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
        ItemMeta meta = grayGlass.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY + " ");
        grayGlass.setItemMeta(meta);
        return grayGlass;
    }

    public void openGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 9, "JoinLeave GUI");

        ItemStack joinItem = createItem(Material.SLIME_BLOCK, ChatColor.BOLD + " " + ChatColor.GREEN + "Set Join Message", "/njm set join <message>");
        ItemStack leaveItem = createItem(Material.REDSTONE_BLOCK, ChatColor.BOLD + " " + ChatColor.GREEN + "Set Leave Message", "/njm set leave <message>");
        ItemStack grayGlass = createGrayGlass();

        gui.setItem(3, joinItem);
        gui.setItem(5, leaveItem);

        for (int i = 0; i < gui.getSize(); i++) {
            if (gui.getItem(i) == null) {
                gui.setItem(i, grayGlass);
            }
        }

        player.openInventory(gui);
    }

    private ItemStack createItem(Material material, String name, String command) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Collections.singletonList(command));
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals("JoinLeave GUI")) {
            event.setCancelled(true);

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem != null && clickedItem.getType() != Material.AIR) {
                ItemMeta meta = clickedItem.getItemMeta();
                if (meta.hasLore()) {
                    String command = meta.getLore().get(0);
                    if (command.startsWith("/njm set join")) {
                        if (player.hasPermission("joinleave.set.join")) {
                            player.closeInventory();
                            startMessageSettingConversation(player, "join");
                        } else {
                            player.sendMessage(ChatColor.RED + "You don't have permission to set the join message.");
                        }
                    } else if (command.startsWith("/njm set leave")) {
                        if (player.hasPermission("joinleave.set.leave")) {
                            player.closeInventory();
                            startMessageSettingConversation(player, "leave");
                        } else {
                            player.sendMessage(ChatColor.RED + "You don't have permission to set the leave message.");
                        }
                    }
                }
            }
        }
    }

    private void startMessageSettingConversation(Player player, String messageType) {
        ConversationFactory conversationFactory = new ConversationFactory(plugin)
                .withModality(true)
                .withPrefix(context -> ChatColor.DARK_PURPLE + "[" + ChatColor.LIGHT_PURPLE + "NJM" + ChatColor.DARK_PURPLE + "] ")
                .withFirstPrompt(new StringPrompt() {
                    @Override
                    public Prompt acceptInput(ConversationContext context, String input) {
                        if (input.equalsIgnoreCase("cancel")) {
                            context.getForWhom().sendRawMessage(ChatColor.RED + "Your " + messageType + " Message Has been Canceled.");
                        } else {
                            setMessage(player, messageType, input);
                            context.getForWhom().sendRawMessage(ChatColor.GREEN + "Your " + messageType + " message has been set.");
                        }
                        return Prompt.END_OF_CONVERSATION;
                    }

                    @Override
                    public String getPromptText(ConversationContext context) {
                        boolean canceled = (boolean) context.getSessionData("cancel");
                        if (canceled) {
                            return ChatColor.RED + "Your " + messageType + " Message Has been Canceled.";
                        } else {
                            return ChatColor.YELLOW + "Please enter the " + messageType + " message or type 'cancel' to cancel:";
                        }
                    }
                })
                .withLocalEcho(false);

        Conversation conversation = conversationFactory.buildConversation(player);
        conversation.getContext().setSessionData("cancel", false); // Initialize cancel flag
        conversation.getContext().setSessionData("messageType", messageType); // Store message type
        conversation.begin();
    }

    private void setMessage(Player player, String messageType, String message) {
        JoinleaveMessage instance = (JoinleaveMessage) plugin;
        instance.setMessage(player, messageType, message);
    }
}
