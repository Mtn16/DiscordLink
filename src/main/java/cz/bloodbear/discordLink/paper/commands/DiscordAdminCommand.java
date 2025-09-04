package cz.bloodbear.discordLink.paper.commands;

import cz.bloodbear.discordLink.core.utils.TabCompleterHelper;
import cz.bloodbear.discordLink.paper.DiscordLink;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DiscordAdminCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {

        if((commandSender instanceof Player)) {
            if(!hasPermission(commandSender)) {
                commandSender.sendMessage(DiscordLink.getInstance().formatMessage(DiscordLink.getInstance().getMessage("command.admin.noperms")));
                return true;
            }
        }

        if(args.length == 0) {
            commandSender.sendMessage(DiscordLink.getInstance().formatMessage(DiscordLink.getInstance().getMessage("command.admin.usage")));
            return true;
        }

        if(!hasPermission(commandSender, args[0])) {
            commandSender.sendMessage(DiscordLink.getInstance().formatMessage(DiscordLink.getInstance().getMessage("command.admin.noperms")));
            return true;
        }


        if(args[0].equalsIgnoreCase("resync")) {
            if(args.length != 2) {
                commandSender.sendMessage(DiscordLink.getInstance().formatMessage(DiscordLink.getInstance().getMessage("command.admin.usage")));
                return true;
            }

            Player player = DiscordLink.getInstance().getServer().getPlayer(args[1]);
            DiscordLink.getInstance().getDiscordBot().syncRoles(player.getUniqueId().toString());
            commandSender.sendMessage(DiscordLink.getInstance().formatMessage(DiscordLink.getInstance().getMessage("command.admin.resync")));
            return true;
        }

        if(args[0].equalsIgnoreCase("resyncAll")) {
            DiscordLink.getInstance().getDiscordBot().syncAllRoles();
            commandSender.sendMessage(DiscordLink.getInstance().formatMessage(DiscordLink.getInstance().getMessage("command.admin.resync")));
            return true;
        }

        if(args[0].equalsIgnoreCase("reload")) {
            DiscordLink.getInstance().reloadConfig();
            commandSender.sendMessage(DiscordLink.getInstance().formatMessage(DiscordLink.getInstance().getMessage("command.admin.reload")));
            return true;
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if(!(commandSender instanceof Player)) return new ArrayList<>();
        if(args.length <= 1) {
            List<String> choices = Arrays.asList("resync", "resyncAll", "reload");
            List<String> finalChoices = new ArrayList<>();
            choices.forEach(choice -> {
                if(hasPermission(commandSender, choice)) finalChoices.add(choice);
            });
            if(args.length == 0) {
                return choices;
            }
            return TabCompleterHelper.getArguments(finalChoices, args[0]);
        } else if (args.length == 2 && args[0].equalsIgnoreCase("resync")) {
            List<String> suggestions = new ArrayList<>();
            DiscordLink.getInstance().getServer().getOnlinePlayers().forEach(player -> {
                suggestions.add(player.getName());
            });

            return suggestions;
        }

        return new ArrayList<>();
    }

    public static boolean hasPermission(CommandSender source) {
        if(source instanceof ConsoleCommandSender) return true;
        return LuckPermsProvider.get().getUserManager().getUser(((Player) source).getUniqueId()).getCachedData().getPermissionData().checkPermission("discordlink.admin").asBoolean();
    }

    public static boolean hasPermission(CommandSender source, String subcommand) {
        if(source instanceof ConsoleCommandSender) return true;
        return (LuckPermsProvider.get().getUserManager().getUser(((Player) source).getUniqueId()).getCachedData().getPermissionData().checkPermission(String.format("discordlink.admin.%s", subcommand.toLowerCase())).asBoolean()
                || LuckPermsProvider.get().getUserManager().getUser(((Player) source).getUniqueId()).getCachedData().getPermissionData().checkPermission("discordlink.admin.*").asBoolean());
    }
}
