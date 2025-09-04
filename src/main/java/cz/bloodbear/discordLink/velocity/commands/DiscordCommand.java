package cz.bloodbear.discordLink.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import cz.bloodbear.discordLink.core.utils.TabCompleterHelper;
import cz.bloodbear.discordLink.velocity.DiscordLink;
import cz.bloodbear.discordLink.core.utils.CodeGenerator;
import cz.bloodbear.discordLink.velocity.utils.DatabaseManager;
import cz.bloodbear.discordLink.core.utils.DiscordUtils;
import cz.bloodbear.discordLink.velocity.utils.PlaceholderRegistry;
import net.luckperms.api.LuckPermsProvider;

import java.util.ArrayList;
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

        if(!hasPermission(source)) {
            source.sendMessage(DiscordLink.getInstance().formatMessage(DiscordLink.getInstance().getMessage("command.discord.noperms", (Player)invocation.source())));
            return;
        }

        if (args.length == 0) {
            source.sendMessage(DiscordLink.getInstance().formatMessage(DiscordLink.getInstance().getMessage("command.discord.invite", (Player)invocation.source())));
            return;
        }

        if(!hasPermission(source, args[0].toLowerCase())) {
            source.sendMessage(DiscordLink.getInstance().formatMessage(DiscordLink.getInstance().getMessage("command.discord.noperms", (Player)invocation.source())));
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
        if(!(invocation.source() instanceof Player)) return new ArrayList<>();

        if(invocation.arguments().length <= 1) {
            List<String> choices = Arrays.asList("link", "unlink", "info");
            List<String> finalChoices = new ArrayList<>();
            choices.forEach(choice -> {
                if(hasPermission(invocation.source(), choice)) finalChoices.add(choice);
            });
            if(invocation.arguments().length == 0) {
                return choices;
            }
            return TabCompleterHelper.getArguments(finalChoices, invocation.arguments()[0]);
        }

        return new ArrayList<>();
    }

    public static boolean hasPermission(CommandSource source) {
        if(source instanceof ConsoleCommandSource) return true;
        return LuckPermsProvider.get().getUserManager().getUser(((Player) source).getUniqueId()).getCachedData().getPermissionData().checkPermission("discordlink.player").asBoolean();
    }

    public static boolean hasPermission(CommandSource source, String subcommand) {
        if(source instanceof ConsoleCommandSource) return true;
        return (LuckPermsProvider.get().getUserManager().getUser(((Player) source).getUniqueId()).getCachedData().getPermissionData().checkPermission(String.format("discordlink.player.%s", subcommand.toLowerCase())).asBoolean()
        || LuckPermsProvider.get().getUserManager().getUser(((Player) source).getUniqueId()).getCachedData().getPermissionData().checkPermission("discordlink.player.*").asBoolean());
    }
}
