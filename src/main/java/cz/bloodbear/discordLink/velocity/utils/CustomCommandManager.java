package cz.bloodbear.discordLink.velocity.utils;

import com.velocitypowered.api.proxy.Player;
import cz.bloodbear.discordLink.core.utils.StringUtils;
import cz.bloodbear.discordLink.velocity.DiscordLink;

import java.util.Optional;
import java.util.UUID;

public class CustomCommandManager {
    public static void InvokeLinkedCommands(String uuid) {
            for (String command : DiscordLink.getInstance().getCommands().getStringList("commands.linked")) {
                if(command.startsWith("--disabled")) continue;

                if(command.startsWith("{CONSOLE} ")) {
                    String finalCommand = StringUtils.splitByString(command, "{CONSOLE} ")[1];
                    Optional<Player> player = DiscordLink.getInstance().getServer().getPlayer(UUID.fromString(uuid));
                    if(player.isPresent()) {
                        finalCommand = PlaceholderRegistry.replacePlaceholders(command, player.get());
                    }
                    DiscordLink.getInstance().getServer().getCommandManager().executeAsync(
                            DiscordLink.getInstance().getServer().getConsoleCommandSource(),
                            finalCommand
                    );
                } else if (command.startsWith("{PLAYER} ")) {
                    Optional<Player> player = DiscordLink.getInstance().getServer().getPlayer(UUID.fromString(uuid));
                    player.ifPresent(presentPlayer -> {
                        String finalCommand = PlaceholderRegistry.replacePlaceholders(StringUtils.splitByString(command, "{PLAYER} ")[1], presentPlayer);
                        DiscordLink.getInstance().getServer().getCommandManager().executeAsync(
                                presentPlayer,
                                finalCommand
                        );
                    });
                } else {
                    DiscordLink.getInstance().getLogger().warn("Unknown command type: " + command);
                }
            }
    }

    public static void InvokeUnlinkedCommands(String uuid) {
            for (String command : DiscordLink.getInstance().getCommands().getStringList("commands.unlinked")) {
                if(command.startsWith("--disabled")) continue;

                if(command.startsWith("{CONSOLE} ")) {
                    String finalCommand = StringUtils.splitByString(command, "{CONSOLE} ")[1];
                    Optional<Player> player = DiscordLink.getInstance().getServer().getPlayer(UUID.fromString(uuid));
                    if(player.isPresent()) {
                        finalCommand = PlaceholderRegistry.replacePlaceholders(command, player.get());
                    }
                    DiscordLink.getInstance().getServer().getCommandManager().executeAsync(
                            DiscordLink.getInstance().getServer().getConsoleCommandSource(),
                            finalCommand
                    );
                } else if (command.startsWith("{PLAYER} ")) {
                    Optional<Player> player = DiscordLink.getInstance().getServer().getPlayer(UUID.fromString(uuid));
                    player.ifPresent(presentPlayer -> {
                        String finalCommand = PlaceholderRegistry.replacePlaceholders(StringUtils.splitByString(command, "{PLAYER} ")[1], presentPlayer);
                        DiscordLink.getInstance().getServer().getCommandManager().executeAsync(
                                presentPlayer,
                                finalCommand
                        );
                    });
                } else {
                    DiscordLink.getInstance().getLogger().warn("Unknown command type: " + command);
                }
            }
    }
}
