package cz.bloodbear.discordLink.paper.utils;

import cz.bloodbear.discordLink.core.utils.StringUtils;
import cz.bloodbear.discordLink.paper.DiscordLink;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CustomCommandManager {
    public static void InvokeLinkedCommands(String uuid) {
        new Thread(() -> {
            for (String command : DiscordLink.getInstance().getCommands().getStringList("linked")) {
                if(command.startsWith("--disabled")) continue;

                if(command.startsWith("{CONSOLE} ")) {
                    Player player = DiscordLink.getInstance().getServer().getPlayer(UUID.fromString(uuid));
                    String finalCommand = PlaceholderRegistry.replacePlaceholders(StringUtils.splitByString(command, "{CONSOLE} ")[1], player);
                    if(DiscordLink.getInstance().isPlaceholderAPIEnabled()) {
                        PlaceholderAPI.setPlaceholders(player, finalCommand);
                    }
                    DiscordLink.getInstance().getServer().dispatchCommand(
                            DiscordLink.getInstance().getServer().getConsoleSender(),
                            StringUtils.splitByString(finalCommand, "{CONSOLE} ")[1]
                    );
                } else if (command.startsWith("{PLAYER} ")) {
                    Player player = DiscordLink.getInstance().getServer().getPlayer(UUID.fromString(uuid));
                    String finalCommand = PlaceholderRegistry.replacePlaceholders(StringUtils.splitByString(command, "{CONSOLE} ")[1], player);
                    if(DiscordLink.getInstance().isPlaceholderAPIEnabled()) {
                        PlaceholderAPI.setPlaceholders(player, finalCommand);
                    }
                    if(player != null && player.isOnline()) {
                        DiscordLink.getInstance().getServer().dispatchCommand(
                                player,
                                finalCommand
                        );
                    }
                }
            }
        }).start();
    }

    public static void InvokeUnlinkedCommands(String uuid) {
        new Thread(() -> {
            for (String command : DiscordLink.getInstance().getCommands().getStringList("unlinked")) {
                if(command.startsWith("--disabled")) continue;

                if(command.startsWith("[CONSOLE] ")) {
                    DiscordLink.getInstance().getServer().dispatchCommand(
                            DiscordLink.getInstance().getServer().getConsoleSender(),
                            command.split("[CONSOLE] ")[1]
                    );
                } else if (command.startsWith("[PLAYER] ")) {
                    Player player = DiscordLink.getInstance().getServer().getPlayer(UUID.fromString(uuid));
                    if(player != null && player.isOnline()) {
                        DiscordLink.getInstance().getServer().dispatchCommand(
                                player,
                                command.split("[PLAYER] ")[1]
                        );
                    }
                }
            }
        }).start();
    }
}
