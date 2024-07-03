package com.joinleave;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.UUID;
import java.util.logging.Level;

public class Language {

    private final Plugin plugin;

    // Constructor
    public Language(Plugin plugin) {
        this.plugin = plugin;
    }

    // Open the GUI
    public void openLanguageGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 9, ChatColor.AQUA + "Language Selection");

        // Create the English language item with a custom basehead texture
        ItemStack englishItem = createBaseheadItem(
                "English",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGNhYzk3NzRkYTEyMTcyNDg1MzJjZTE0N2Y3ODMxZjY3YTEyZmRjY2ExY2YwY2I0YjM4NDhkZTZiYzk0YjQifX19"
        );

        // Add the item to the GUI
        gui.setItem(4, englishItem); // Place the item in the center slot

        // Open the GUI for the player
        player.openInventory(gui);
    }

    // Create a custom basehead item with a given name and texture value
    private ItemStack createBaseheadItem(String name, String textureValue) {
        ItemStack headItem = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta skullMeta = (SkullMeta) headItem.getItemMeta();

        // Set the item name
        skullMeta.setDisplayName(ChatColor.GOLD + name);

        // Set the custom texture
        setSkinViaBase64(skullMeta, textureValue);

        headItem.setItemMeta(skullMeta);
        return headItem;
    }

    private void setSkinViaBase64(SkullMeta meta, String base64) {
        try {
            Field profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);

            GameProfile profile = new GameProfile(UUID.randomUUID(), "skull-texture");
            profile.getProperties().put("textures", new Property("textures", base64));

            profileField.set(meta, profile);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            plugin.getLogger().log(Level.SEVERE, "There was a severe internal reflection error when attempting to set the skin of a player skull via base64!");
            e.printStackTrace();
        }
    }
}
