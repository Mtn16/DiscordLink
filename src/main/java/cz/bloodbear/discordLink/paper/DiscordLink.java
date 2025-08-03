package cz.bloodbear.discordLink.paper;

import com.google.inject.Inject;
import cz.bloodbear.discordLink.paper.commands.DiscordAdminCommand;
import cz.bloodbear.discordLink.paper.commands.DiscordCommand;
import cz.bloodbear.discordLink.paper.discord.DiscordBot;
import cz.bloodbear.discordLink.paper.placeholders.DiscordIdPlaceholder;
import cz.bloodbear.discordLink.paper.placeholders.DiscordUsernamePlaceholder;
import cz.bloodbear.discordLink.paper.placeholders.PlayerNamePlaceholder;
import cz.bloodbear.discordLink.core.records.RoleEntry;
import cz.bloodbear.discordLink.paper.utils.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

public class DiscordLink extends JavaPlugin {
    private static DiscordLink instance;

    @Inject
    private Logger logger;
    private Server server;
    private Path dataDirectory;

    private JsonConfig config;
    private JsonConfig messages;
    private JsonConfig sync;
    private JsonConfig commands;
    private MiniMessage miniMessage;

    private HtmlPage linkedPage;
    private HtmlPage failedPage;
    private HtmlPage missingCodePage;
    private HtmlPage missingStatePage;
    private HtmlPage invalidPage;
    private String redirect;

    private DatabaseManager databaseManager;
    private WebServer webServer;
    private OAuth2Handler oAuth2Handler;

    private DiscordBot discordBot;
    private LuckPerms luckPerms;

    private boolean isPlaceholderAPIEnabled;

    @Override
    public void onEnable() {
        instance = this;

        this.server = getServer();
        this.logger = getServer().getLogger();
        this.dataDirectory = getDataFolder().toPath();

        this.config = new JsonConfig(dataDirectory, "config.json");
        this.messages = new JsonConfig(dataDirectory, "messages.json");
        this.sync = new JsonConfig(dataDirectory, "sync.json");
        this.commands = new JsonConfig(dataDirectory, "commands.json");
        this.miniMessage = MiniMessage.miniMessage();

        loadHTML();

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

        try {
            webServer.start();
        } catch (IOException e) {
            logger.severe(e.getMessage());
            server.shutdown();
        }

        DiscordCommand discordCommand = new DiscordCommand();
        DiscordAdminCommand discordAdminCommand = new DiscordAdminCommand();

        getCommand("discord").setExecutor(discordCommand);
        getCommand("discord").setTabCompleter(discordCommand);
        getCommand("discordadmin").setExecutor(discordAdminCommand);
        getCommand("discordadmin").setTabCompleter(discordAdminCommand);

        this.discordBot = new DiscordBot(
                config.getString("discord.bot.token", ""),
                config.getString("discord.guildId", ""),
                config.getString("discord.bot.presence", "MC Sync")
        );
        this.luckPerms = LuckPermsProvider.get();
    }

    private void loadPlaceholders() {
        PlaceholderRegistry.registerPlaceholder(new PlayerNamePlaceholder());
        PlaceholderRegistry.registerPlaceholder(new DiscordIdPlaceholder());
        PlaceholderRegistry.registerPlaceholder(new DiscordUsernamePlaceholder());

        isPlaceholderAPIEnabled = getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");
        if(!isPlaceholderAPIEnabled) return;
        PlaceholderAPIHook.registerHook();
    }

    private void loadHTML() {
        this.linkedPage = new HtmlPage(dataDirectory, "linked.html");
        this.failedPage = new HtmlPage(dataDirectory, "failed.html");
        this.missingCodePage = new HtmlPage(dataDirectory, "missingCode.html");
        this.missingStatePage = new HtmlPage(dataDirectory, "missingState.html");
        this.invalidPage = new HtmlPage(dataDirectory, "invalid.html");
    }

    @Override
    public void onDisable() {
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

    public LuckPerms getLuckPerms() { return luckPerms; }
    public DiscordBot getDiscordBot() { return discordBot; }

    public Component formatMessage(String input) {
        return miniMessage.deserialize(input);
    }

    public String getClientId() { return config.getString("discord.client.id", ""); }
    public String getRedirectUri() { return redirect; }

    public List<RoleEntry> getRoles() { return sync.getRoles("roles"); }

    public boolean isPlaceholderAPIEnabled() {
        return isPlaceholderAPIEnabled;
    }

    @Override
    public void reloadConfig() {
        config.reload();
        messages.reload();
        sync.reload();
        commands.reload();

        loadHTML();
    }
}
