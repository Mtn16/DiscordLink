package cz.bloodbear.discordLink.paper.commands;

import cz.bloodbear.discordLink.paper.DiscordLink;
import cz.bloodbear.discordLink.core.utils.CodeGenerator;
import cz.bloodbear.discordLink.paper.utils.DatabaseManager;
import cz.bloodbear.discordLink.core.utils.DiscordUtils;
import cz.bloodbear.discordLink.paper.utils.PlaceholderRegistry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class DiscordCommand implements CommandExecutor, TabCompleter {
    public DiscordCommand() {
    }


    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(DiscordLink.getInstance().formatMessage(DiscordLink.getInstance().getMessage("command.generic.playeronly")));
            return true;
        }

        if (args.length == 0) {
            commandSender.sendMessage(DiscordLink.getInstance().formatMessage(DiscordLink.getInstance().getMessage("command.discord.invite", (Player)commandSender)));
            return true;
        }

        DatabaseManager databaseManager = DiscordLink.getInstance().getDatabaseManager();
        Player player = (Player)commandSender;

        if (args[0].equalsIgnoreCase("link")) {
            if (databaseManager.isLinked(player.getUniqueId().toString())) {
                commandSender.sendMessage(DiscordLink.getInstance().formatMessage(DiscordLink.getInstance().getMessage("command.discord.alreadylinked")));
                return true;
            }

            String code = CodeGenerator.generateCode();
            databaseManager.saveLinkRequest(player.getUniqueId().toString(), code);
            String url = DiscordUtils.getOAuthLink(DiscordLink.getInstance().getClientId(), DiscordLink.getInstance().getRedirectUri(), code);
            player.sendMessage(DiscordLink.getInstance().formatMessage(PlaceholderRegistry.replacePlaceholders(DiscordLink.getInstance().getMessage("command.discord.link", player).replace("[linkUrl]", url), player)));
            return true;
        }

        if (args[0].equalsIgnoreCase("unlink")) {
            if (!databaseManager.isLinked(player.getUniqueId().toString())) {
                commandSender.sendMessage(DiscordLink.getInstance().formatMessage(DiscordLink.getInstance().getMessage("command.discord.notlinked", player)));
                return true;
            }

            databaseManager.unlinkAccount(player.getUniqueId().toString());
            player.sendMessage(DiscordLink.getInstance().formatMessage(DiscordLink.getInstance().getMessage("command.discord.unlinked", player)));
            return true;
        }

        if (args[0].equalsIgnoreCase("info")) {
            if (!databaseManager.isLinked(player.getUniqueId().toString())) {
                player.sendMessage(DiscordLink.getInstance().formatMessage(DiscordLink.getInstance().getMessage("command.discord.notlinked", player)));
                return true;
            }

            player.sendMessage(DiscordLink.getInstance().formatMessage(DiscordLink.getInstance().getMessage("command.discord.info", player)));
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        return Arrays.asList("link", "unlink", "info");
    }
}
