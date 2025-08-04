package cz.bloodbear.discordLink.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import cz.bloodbear.discordLink.velocity.DiscordLink;
import cz.bloodbear.discordLink.core.utils.CodeGenerator;
import cz.bloodbear.discordLink.velocity.utils.DatabaseManager;
import cz.bloodbear.discordLink.core.utils.DiscordUtils;
import cz.bloodbear.discordLink.velocity.utils.PlaceholderRegistry;

import java.util.Arrays;
import java.util.List;

public class DiscordCommand implements SimpleCommand {
    public DiscordCommand() {
    }

    public void execute(SimpleCommand.Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        if (!(source instanceof Player)) {
            source.sendMessage(DiscordLink.getInstance().formatMessage(DiscordLink.getInstance().getMessage("command.generic.playeronly")));
            return;
        }

        if (args.length == 0) {
            source.sendMessage(DiscordLink.getInstance().formatMessage(DiscordLink.getInstance().getMessage("command.discord.invite", (Player)invocation.source())));
            return;
        }

        DatabaseManager databaseManager = DiscordLink.getInstance().getDatabaseManager();
        Player player = (Player)invocation.source();

        if (args[0].equalsIgnoreCase("link")) {
            if (databaseManager.isLinked(player.getUniqueId().toString())) {
                source.sendMessage(DiscordLink.getInstance().formatMessage(DiscordLink.getInstance().getMessage("command.discord.alreadylinked", player)));
                return;
            }

            databaseManager.deleteLinkCodes(player.getUniqueId().toString());
            String code = CodeGenerator.generateCode();
            databaseManager.saveLinkRequest(player.getUniqueId().toString(), code);
            String url = DiscordUtils.getOAuthLink(DiscordLink.getInstance().getClientId(), DiscordLink.getInstance().getRedirectUri(), code);
            player.sendMessage(DiscordLink.getInstance().formatMessage(PlaceholderRegistry.replacePlaceholders(DiscordLink.getInstance().getMessage("command.discord.link", player).replace("[linkUrl]", url), player)));
            return;
        }

        if (args[0].equalsIgnoreCase("unlink")) {
            if (!databaseManager.isLinked(player.getUniqueId().toString())) {
                source.sendMessage(DiscordLink.getInstance().formatMessage(DiscordLink.getInstance().getMessage("command.discord.notlinked", player)));
                return;
            }

            databaseManager.unlinkAccount(player.getUniqueId().toString());
            source.sendMessage(DiscordLink.getInstance().formatMessage(DiscordLink.getInstance().getMessage("command.discord.unlinked", player)));
            return;
        }

        if (args[0].equalsIgnoreCase("info")) {
            if (!databaseManager.isLinked(player.getUniqueId().toString())) {
                source.sendMessage(DiscordLink.getInstance().formatMessage(DiscordLink.getInstance().getMessage("command.discord.notlinked", player)));
                return;
            }

            source.sendMessage(DiscordLink.getInstance().formatMessage(DiscordLink.getInstance().getMessage("command.discord.info", player)));
        }
    }

    public List<String> suggest(SimpleCommand.Invocation invocation) {
        return Arrays.asList("link", "unlink", "info");
    }
}
