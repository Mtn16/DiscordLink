package cz.bloodbear.discordLink.paper.commands;

import cz.bloodbear.discordLink.paper.DiscordLink;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DiscordAdminCommand implements CommandExecutor, TabCompleter {
    public boolean hasPermission(CommandSender sender) {
        if((sender instanceof Player)) {
            return LuckPermsProvider.get().getUserManager().getUser(((Player)sender).getUniqueId()).getCachedData().getPermissionData().checkPermission("discordlink.admin").asBoolean();
        } else {
            return true;
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {

        if((commandSender instanceof Player) && !LuckPermsProvider.get().getUserManager().getUser(((Player)commandSender).getUniqueId()).getCachedData().getPermissionData().checkPermission("discordlink.admin").asBoolean()) {
            commandSender.sendMessage(DiscordLink.getInstance().formatMessage(DiscordLink.getInstance().getMessage("command.admin.noperms")));
            return true;
        }

        if(args.length == 0) {
            commandSender.sendMessage(DiscordLink.getInstance().formatMessage(DiscordLink.getInstance().getMessage("command.admin.usage")));
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
        if(args.length <= 1) {
            return Arrays.asList("resync", "resyncAll", "reload");
        } else if (args.length == 2) {
            List<String> suggestions = new ArrayList<>();
            DiscordLink.getInstance().getServer().getOnlinePlayers().forEach(player -> {
                suggestions.add(player.getName());
            });

            return suggestions;
        }

        return new ArrayList<>();
    }
}
