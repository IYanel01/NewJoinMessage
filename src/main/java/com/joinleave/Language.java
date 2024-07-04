package com.joinleave;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.UUID;
import java.util.logging.Level;

public class Language implements Listener {

    private final Plugin plugin;
    private final LanguageConfigs languageConfigs;

    // Constructor
    public Language(Plugin plugin) {
        this.plugin = plugin;
        this.languageConfigs = new LanguageConfigs((JavaPlugin) plugin);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    // Open the GUI
    public void openLanguageGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 18, ChatColor.AQUA + "Language Selection");

        // Create the English language item with a custom basehead texture
        ItemStack englishItem = createBaseheadItem(
                "English",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGNhYzk3NzRkYTEyMTcyNDg1MzJjZTE0N2Y3ODMxZjY3YTEyZmRjY2ExY2YwY2I0YjM4NDhkZTZiYzk0YjQifX19"
        );

        // Create the German language item with a custom basehead texture
        ItemStack germanyItem = createBaseheadItem(
                "Germany",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWU3ODk5YjQ4MDY4NTg2OTdlMjgzZjA4NGQ5MTczZmU0ODc4ODY0NTM3NzQ2MjZiMjRiZDhjZmVjYzc3YjNmIn19fQ=="
        );

        // Create more language items similarly
        ItemStack frenchItem = createBaseheadItem(
                "French",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTEyNjlhMDY3ZWUzN2U2MzYzNWNhMWU3MjNiNjc2ZjEzOWRjMmRiZGRmZjk2YmJmZWY5OWQ4YjM1Yzk5NmJjIn19fQ=="
        );

        ItemStack spanishItem = createBaseheadItem(
                "Spanish",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGQzOTIzYjJkMDUwY2Q0MmNkOWZiYjg5ZWU2ODNhMmE5ODk5MzQ1ZTM1MThiZDZjN2YzY2JiNTNmZDE1MWQ3MiJ9fX0="
        );

        ItemStack italianItem = createBaseheadItem(
                "Italian",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODVjZTg5MjIzZmE0MmZlMDZhZDY1ZDhkNDRjYTQxMmFlODk5YzgzMTMwOWQ2ODkyNGRmZTBkMTQyZmRiZWVhNCJ9fX0="
        );

        ItemStack chineseItem = createBaseheadItem(
                "Chinese",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2Y5YmMwMzVjZGM4MGYxYWI1ZTExOThmMjlmM2FkM2ZkZDJiNDJkOWE2OWFlYjY0ZGU5OTA2ODE4MDBiOThkYyJ9fX0="
        );

        ItemStack japaneseItem = createBaseheadItem(
                "Japanese",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDZjMmNhNzIzODY2NmFlMWI5ZGQ5ZGFhM2Q0ZmM4MjlkYjIyNjA5ZmI1NjkzMTJkZWMxZmIwYzhkNmRkNmMxZCJ9fX0="
        );

        ItemStack koreanItem = createBaseheadItem(
                "Korean",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2ExMjkxM2Q3ZGY2NDBkMThiY2M3YTQ1YTgxNzJjNjhmZmEwNDc1NmU4NGM2ZjBhMmVkYTNkYTQ1ZTAwZGFkZCJ9fX0="
        );

        ItemStack russianItem = createBaseheadItem(
                "Russian",
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTZlYWZlZjk4MGQ2MTE3ZGFiZTg5ODJhYzRiNDUwOTg4N2UyYzQ2MjFmNmE4ZmU1YzliNzM1YTgzZDc3NWFkIn19fQ=="
        );

        // Add the items to the GUI
        gui.setItem(0, englishItem); // English item at slot 0
        gui.setItem(1, germanyItem); // German item at slot 1
        gui.setItem(2, frenchItem); // French item at slot 2
        gui.setItem(3, spanishItem); // Spanish item at slot 3
        gui.setItem(4, italianItem); // Italian item at slot 4
        gui.setItem(5, chineseItem); // Chinese item at slot 5
        gui.setItem(6, japaneseItem); // Japanese item at slot 6
        gui.setItem(7, koreanItem); // Korean item at slot 7
        gui.setItem(8, russianItem); // Russian item at slot 8

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

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        if (event.getView().getTitle().equals(ChatColor.AQUA + "Language Selection")) {
            event.setCancelled(true); // Prevent taking items

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

            if (clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasDisplayName()) {
                String displayName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());
                updatePlayerLanguage(player, displayName);
                player.closeInventory(); // Close the inventory
            }
        }
    }

    private void updatePlayerLanguage(Player player, String language) {
        File dataLangFile = new File(plugin.getDataFolder(), "Lang/DataLang.yml");
        YamlConfiguration dataLangConfig = YamlConfiguration.loadConfiguration(dataLangFile);

        String playerUUID = player.getUniqueId().toString();
        dataLangConfig.set(playerUUID + ".Language", language);

        try {
            dataLangConfig.save(dataLangFile);
            player.sendMessage(getLanguageMessage(language));
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save DataLang.yml", e);
            player.sendMessage(ChatColor.RED + "Failed to set language. Please try again.");
        }
    }

    private String getLanguageMessage(String language) {
        switch (language.toLowerCase()) {
            case "english":
                return ChatColor.GREEN + "Language set to English";
            case "Germany":
                return ChatColor.GREEN + "Sprache auf Deutsch eingestellt";
            case "french":
                return ChatColor.GREEN + "Langue définie sur le français";
            case "spanish":
                return ChatColor.GREEN + "Idioma establecido en español";
            case "italian":
                return ChatColor.GREEN + "Lingua impostata su italiano";
            case "chinese":
                return ChatColor.GREEN + "语言设置为中文";
            case "japanese":
                return ChatColor.GREEN + "言語を日本語に設定しました";
            case "korean":
                return ChatColor.GREEN + "언어가 한국어로 설정되었습니다";
            case "russian":
                return ChatColor.GREEN + "Язык установлен на русский";
            default:
                return ChatColor.GREEN + "Language set to " + language;
        }
    }
}
