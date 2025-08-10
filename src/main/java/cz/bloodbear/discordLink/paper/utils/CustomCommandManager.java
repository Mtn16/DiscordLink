package cz.bloodbear.discordLink.paper.utils;

import cz.bloodbear.discordLink.core.utils.StringUtils;
import cz.bloodbear.discordLink.paper.DiscordLink;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CustomCommandManager {
    public static void InvokeLinkedCommands(String uuid) {
            for (String command : DiscordLink.getInstance().getCommands().getStringList("commands.linked")) {
                if(command.startsWith("--disabled")) continue;

                if(command.startsWith("{CONSOLE} ")) {
                    Player player = DiscordLink.getInstance().getServer().getPlayer(UUID.fromString(uuid));
                    String finalCommand = PlaceholderRegistry.replacePlaceholders(StringUtils.splitByString(command, "{CONSOLE} ")[1], player);
                    if(DiscordLink.getInstance().isPlaceholderAPIEnabled()) {
                        finalCommand = PlaceholderAPI.setPlaceholders(player, finalCommand);
                    }
                    DiscordLink.getInstance().getServer().dispatchCommand(
                            DiscordLink.getInstance().getServer().getConsoleSender(),
                            finalCommand
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
                } else {
                    DiscordLink.getInstance().getLogger().warning("Unknown command type: " + command);
                }
            }
    }

    public static void InvokeUnlinkedCommands(String uuid) {
            for (String command : DiscordLink.getInstance().getCommands().getStringList("commands.unlinked")) {
                if(command.startsWith("--disabled")) continue;

                if(command.startsWith("{CONSOLE} ")) {
                    Player player = DiscordLink.getInstance().getServer().getPlayer(UUID.fromString(uuid));
                    String finalCommand = PlaceholderRegistry.replacePlaceholders(StringUtils.splitByString(command, "{CONSOLE} ")[1], player);
                    if(DiscordLink.getInstance().isPlaceholderAPIEnabled()) {
                        finalCommand = PlaceholderAPI.setPlaceholders(player, finalCommand);
                    }
                    DiscordLink.getInstance().getServer().dispatchCommand(
                            DiscordLink.getInstance().getServer().getConsoleSender(),
                            finalCommand
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
                } else {
                    DiscordLink.getInstance().getLogger().warning("Unknown command type: " + command);
                }
            }
    }
}
