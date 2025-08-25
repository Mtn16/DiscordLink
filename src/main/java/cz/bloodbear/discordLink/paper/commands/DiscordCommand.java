package cz.bloodbear.discordLink.paper.commands;

import cz.bloodbear.discordLink.core.utils.TabCompleterHelper;
import cz.bloodbear.discordLink.paper.DiscordLink;
import cz.bloodbear.discordLink.core.utils.CodeGenerator;
import cz.bloodbear.discordLink.paper.utils.DatabaseManager;
import cz.bloodbear.discordLink.core.utils.DiscordUtils;
import cz.bloodbear.discordLink.paper.utils.PlaceholderRegistry;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
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

        if(!hasPermission(commandSender)) {
            commandSender.sendMessage(DiscordLink.getInstance().formatMessage(DiscordLink.getInstance().getMessage("command.discord.noperms", (Player)commandSender)));
            return true;
        }

        if (args.length == 0) {
            commandSender.sendMessage(DiscordLink.getInstance().formatMessage(DiscordLink.getInstance().getMessage("command.discord.invite", (Player)commandSender)));
            return true;
        }

        if(!hasPermission(commandSender, args[0])) {
            commandSender.sendMessage(DiscordLink.getInstance().formatMessage(DiscordLink.getInstance().getMessage("command.discord.noperms", (Player)commandSender)));
            return true;
        }

        DatabaseManager databaseManager = DiscordLink.getInstance().getDatabaseManager();
        Player player = (Player)commandSender;

        if (args[0].equalsIgnoreCase("link")) {
            if (databaseManager.isLinked(player.getUniqueId().toString())) {
                commandSender.sendMessage(DiscordLink.getInstance().formatMessage(DiscordLink.getInstance().getMessage("command.discord.alreadylinked")));
                return true;
            }

            databaseManager.deleteLinkCodes(player.getUniqueId().toString());
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
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if(!(commandSender instanceof Player)) return new ArrayList<>();
        if(args.length == 1) {
            List<String> choices = Arrays.asList("link", "unlink", "info");
            List<String> finalChoices = new ArrayList<>();
            choices.forEach(choice -> {
                if(hasPermission(commandSender, choice)) finalChoices.add(choice);
            });
            return TabCompleterHelper.getArguments(finalChoices, args[0]);
        }

        return new ArrayList<>();
    }

    public static boolean hasPermission(CommandSender source) {
        if(source instanceof ConsoleCommandSender) return true;
        return LuckPermsProvider.get().getUserManager().getUser(((Player) source).getUniqueId()).getCachedData().getPermissionData().checkPermission("discordlink.player").asBoolean();
    }

    public static boolean hasPermission(CommandSender source, String subcommand) {
        if(source instanceof ConsoleCommandSender) return true;
        return (LuckPermsProvider.get().getUserManager().getUser(((Player) source).getUniqueId()).getCachedData().getPermissionData().checkPermission(String.format("discordlink.player.%s", subcommand)).asBoolean()
                || LuckPermsProvider.get().getUserManager().getUser(((Player) source).getUniqueId()).getCachedData().getPermissionData().checkPermission("discordlink.player.*").asBoolean());
    }
}
