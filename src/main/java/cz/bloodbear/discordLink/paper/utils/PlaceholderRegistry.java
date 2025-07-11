package cz.bloodbear.discordLink.paper.utils;

import cz.bloodbear.discordLink.paper.DiscordLink;
import cz.bloodbear.discordLink.paper.interfaces.Placeholder;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PlaceholderRegistry {
    private static final Map<String, Placeholder> placeholders = new HashMap<>();

    public static void registerPlaceholder(Placeholder placeholder) {
        placeholders.put(placeholder.getIdentifier(), placeholder);
    }

    public static String replacePlaceholders(String text, Player player) {
        for (Placeholder placeholder : placeholders.values()) {
            text = placeholder.replace(text, player);
        }
        if(DiscordLink.getInstance().isPlaceholderAPIEnabled()) {
            PlaceholderAPI.setPlaceholders(player, text);
        }
        return text;
    }
}
