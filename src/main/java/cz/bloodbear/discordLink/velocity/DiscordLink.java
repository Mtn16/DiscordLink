package cz.bloodbear.discordLink.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import cz.bloodbear.discordLink.velocity.commands.DiscordAdminCommand;
import cz.bloodbear.discordLink.velocity.commands.DiscordCommand;
import cz.bloodbear.discordLink.velocity.discord.DiscordBot;
import cz.bloodbear.discordLink.velocity.placeholders.DiscordIdPlaceholder;
import cz.bloodbear.discordLink.velocity.placeholders.DiscordUsernamePlaceholder;
import cz.bloodbear.discordLink.velocity.placeholders.PlayerNamePlaceholder;
import cz.bloodbear.discordLink.core.records.RoleEntry;
import cz.bloodbear.discordLink.velocity.utils.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Plugin(id = "discordlink", name = "DiscordLink", version = "25.3",
        authors = {"Mtn16"}, url = "https://github.com/Mtn16/DiscordLink",
        description = "A Velocity plugin for Discord integration.",
        dependencies = {
            @Dependency(id = "luckperms", optional = false)
        })
public class DiscordLink {
    private static DiscordLink instance;

    @Inject
    private final Logger logger;
    private final ProxyServer server;
    private final Path dataDirectory;

    private final JsonConfig config;
    private final JsonConfig messages;
    private final JsonConfig sync;
    private final MiniMessage miniMessage;

    private final HtmlPage linkedPage;
    private final HtmlPage failedPage;
    private final HtmlPage missingCodePage;
    private final HtmlPage missingStatePage;
    private final HtmlPage invalidPage;
    private final String redirect;

    private final DatabaseManager databaseManager;
    private final WebServer webServer;
    private final OAuth2Handler oAuth2Handler;

    private DiscordBot discordBot;
    private LuckPerms luckPerms;

    @Inject
    public DiscordLink(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        instance = this;

        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;

        this.config = new JsonConfig(dataDirectory, "config.json");
        this.messages = new JsonConfig(dataDirectory, "messages.json");
        this.sync = new JsonConfig(dataDirectory, "sync.json");
        this.miniMessage = MiniMessage.miniMessage();

        this.linkedPage = new HtmlPage(dataDirectory, "linked.html");
        this.failedPage = new HtmlPage(dataDirectory, "failed.html");
        this.missingCodePage = new HtmlPage(dataDirectory, "missingCode.html");
        this.missingStatePage = new HtmlPage(dataDirectory, "missingState.html");
        this.invalidPage = new HtmlPage(dataDirectory, "invalid.html");

        this.databaseManager = new DatabaseManager(
                config.getString("database.host", ""),
                config.getInt("database.port", 3306),
                config.getString("database.name", ""),
                config.getString("database.username", ""),
                config.getString("database.password", ""),
                config.getBoolean("database.useSSL", false)
        );

        this.webServer = new WebServer(
                config.getInt("webserver.port", 80),
                config.getBoolean("webserver.domain.use", false),
                config.getString("webserver.domain.domain", "")
        );

        if(config.getBoolean("webserver.domain.use", false)) {
            if(config.getBoolean("webserver.domain.https", false)) {
                redirect = "https://" + config.getString("webserver.domain.domain", "") + "/callback";
            } else {
                redirect = "http://" + config.getString("webserver.domain.domain", "") + "/callback";
            }
        } else {
            redirect = "http://" + config.getString("webserver.ip", "") + ":" + config.getString("webserver.port", "")  + "/callback";
        }
        this.oAuth2Handler = new OAuth2Handler(
                config.getString("discord.client.id", ""),
                config.getString("discord.client.secret", ""),
                redirect
        );

        loadPlaceholders();
    }

    private void loadPlaceholders() {
        PlaceholderRegistry.registerPlaceholder(new PlayerNamePlaceholder());
        PlaceholderRegistry.registerPlaceholder(new DiscordIdPlaceholder());
        PlaceholderRegistry.registerPlaceholder(new DiscordUsernamePlaceholder());
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        try {
            webServer.start();
        } catch (IOException e) {
            logger.error(e.getMessage());
            server.shutdown();
        }

        CommandManager commandManager = server.getCommandManager();
        CommandMeta discordCommandMeta = commandManager.metaBuilder("discord")
                .plugin(this)
                .build();

        CommandMeta adminCommandMeta = commandManager.metaBuilder("discordadmin")
                .plugin(this)
                .build();

        commandManager.register(discordCommandMeta, new DiscordCommand());
        commandManager.register(adminCommandMeta, new DiscordAdminCommand());

        this.discordBot = new DiscordBot(
                config.getString("discord.bot.token", ""),
                config.getString("discord.guildId", ""),
                config.getString("discord.bot.presence", "MC Sync")
        );
        this.luckPerms = LuckPermsProvider.get();
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        databaseManager.close();
        webServer.stop();
    }

    public static DiscordLink getInstance() { return instance; }

    public @NotNull String getMessage(String key) {
        return messages.getString(key, "<red>Unknown message: " + key + "</red>");
    }

    public @NotNull String getMessage(String key, Player player) {
        return PlaceholderRegistry.replacePlaceholders(messages.getString(key, "<red>Unknown message: " + key + "</red>"), player);
    }

    public HtmlPage getHtmlPage(String name) {
        if (name.equalsIgnoreCase("linked")) {
            return linkedPage;
        } else if (name.equalsIgnoreCase("stateMissing")) {
            return missingStatePage;
        } else if (name.equalsIgnoreCase("codeMissing")) {
            return missingCodePage;
        } else if (name.equalsIgnoreCase("invalid")) {
            return invalidPage;
        } else if (name.equalsIgnoreCase("failed")) {
            return failedPage;
        }
        return null;
    }

    public DatabaseManager getDatabaseManager() { return databaseManager; }

    public OAuth2Handler getOAuth2Handler() { return oAuth2Handler; }

    public Logger getLogger() { return logger; }
    public ProxyServer getServer() { return server; }
    public LuckPerms getLuckPerms() { return luckPerms; }
    public DiscordBot getDiscordBot() { return discordBot; }

    public Component formatMessage(String input) {
        return miniMessage.deserialize(input);
    }

    public String getClientId() { return config.getString("discord.client.id", ""); }
    public String getRedirectUri() { return redirect; }

    public List<RoleEntry> getRoles() { return sync.getRoles("roles"); }
}
