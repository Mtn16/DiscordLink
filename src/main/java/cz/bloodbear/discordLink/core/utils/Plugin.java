package cz.bloodbear.discordLink.core.utils;

import cz.bloodbear.discordLink.core.records.RoleEntry;
import cz.bloodbear.discordLink.core.utils.event.EventBus;
import net.kyori.adventure.text.Component;

import java.time.Duration;
import java.util.List;

public interface Plugin {

    String getMessage(String key);

    Page getHtmlPage(String name);

    DB getDatabaseManager();

    AuthHandler getOAuth2Handler();

    Bot getDiscordBot();

    Component formatMessage(String input);

    String getClientId();

    String getRedirectUri();

    List<RoleEntry> getRoles();

    boolean isPlaceholderAPIEnabled();

    Config getCommands();

    Config getDiscordConfig();

    boolean getJoinGuild();

    String getGuildId();

    Duration getUptime();

    EventBus getEventBus();
}