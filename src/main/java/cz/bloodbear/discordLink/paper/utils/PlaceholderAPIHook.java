package cz.bloodbear.discordLink.paper.utils;

import cz.bloodbear.discordLink.core.records.DiscordAccount;
import cz.bloodbear.discordLink.paper.DiscordLink;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlaceholderAPIHook extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "discordlink";
    }

    @Override
    public @NotNull String getAuthor() {
        return DiscordLink.getInstance().getDescription().getAuthors().get(0);
    }

    @Override
    public @NotNull String getVersion() {
        return DiscordLink.getInstance().getDescription().getVersion();
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if(params.equalsIgnoreCase("discordusername")) {
            DiscordAccount acc = DiscordLink.getInstance().getDatabaseManager().getDiscordAccount(player.getUniqueId().toString());
            if(acc == null) return "Account not linked";
            return acc.username();
        } else if (params.equalsIgnoreCase("discordid")) {
            DiscordAccount acc = DiscordLink.getInstance().getDatabaseManager().getDiscordAccount(player.getUniqueId().toString());
            if(acc == null) return "Account not linked";
            return acc.id();
        }

        return "";
    }

    public static void registerHook() {
        new PlaceholderAPIHook().register();
    }
}
