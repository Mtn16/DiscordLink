package cz.bloodbear.discordLink.utils;

import com.velocitypowered.api.proxy.Player;
import cz.bloodbear.discordLink.interfaces.Placeholder;

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
        return text;
    }
}
