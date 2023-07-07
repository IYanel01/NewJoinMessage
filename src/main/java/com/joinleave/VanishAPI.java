package com.joinleave;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class VanishAPI {
    private static final Set<Player> vanishedPlayers = new HashSet<>();

    public static void addVanishedPlayer(Player player) {
        vanishedPlayers.add(player);
    }

    public static boolean isVanished(Player player) {
        return !vanishedPlayers.contains(player);
    }
}
