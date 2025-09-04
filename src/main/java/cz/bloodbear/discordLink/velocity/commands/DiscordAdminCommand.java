package cz.bloodbear.discordLink.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import cz.bloodbear.discordLink.core.utils.TabCompleterHelper;
import cz.bloodbear.discordLink.velocity.DiscordLink;
import net.luckperms.api.LuckPermsProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class DiscordAdminCommand implements SimpleCommand {
    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        if((source instanceof Player)) {
            if(!hasPermission(source)) {
                source.sendMessage(DiscordLink.getInstance().formatMessage(DiscordLink.getInstance().getMessage("command.admin.noperms", (Player) invocation.source())));
                return;
            }
        }

        if(args.length == 0) {
            source.sendMessage(DiscordLink.getInstance().formatMessage(DiscordLink.getInstance().getMessage("command.admin.usage")));
            return;
        }

        if(!hasPermission(invocation.source(), args[0])) {
            source.sendMessage(DiscordLink.getInstance().formatMessage(DiscordLink.getInstance().getMessage("command.admin.usage")));
            return;
        }


        if(args[0].equalsIgnoreCase("resync")) {
            if(args.length != 2) {
                source.sendMessage(DiscordLink.getInstance().formatMessage(DiscordLink.getInstance().getMessage("command.admin.usage")));
                return;
            }

            Optional<Player> player = DiscordLink.getInstance().getServer().getPlayer(args[1]);
            DiscordLink.getInstance().getDiscordBot().syncRoles(player.get().getUniqueId().toString());
            invocation.source().sendMessage(DiscordLink.getInstance().formatMessage(DiscordLink.getInstance().getMessage("command.admin.resync")));
            return;
        }

        if(args[0].equalsIgnoreCase("resyncAll")) {
            DiscordLink.getInstance().getDiscordBot().syncAllRoles();
            invocation.source().sendMessage(DiscordLink.getInstance().formatMessage(DiscordLink.getInstance().getMessage("command.admin.resync")));
            return;
        }

        if(args[0].equalsIgnoreCase("reload")) {
            DiscordLink.getInstance().reloadConfig();
            invocation.source().sendMessage(DiscordLink.getInstance().formatMessage(DiscordLink.getInstance().getMessage("command.admin.reload")));
            return;
        }

        return;
    }


    public List<String> suggest(Invocation invocation) {
        if(!(invocation.source() instanceof Player)) return new ArrayList<>();

        if(invocation.arguments().length <= 1) {
            List<String> choices = Arrays.asList("resync", "resyncAll", "reload");
            List<String> finalChoices = new ArrayList<>();
            choices.forEach(choice -> {
                if(hasPermission(invocation.source(), choice)) finalChoices.add(choice);
            });
            if(invocation.arguments().length == 0) {
                return choices;
            }
            return TabCompleterHelper.getArguments(finalChoices, invocation.arguments()[0]);
        } else if (invocation.arguments().length == 2 && invocation.arguments()[0].equalsIgnoreCase("resync")) {
            List<String> suggestions = new ArrayList<>();
            DiscordLink.getInstance().getServer().getAllPlayers().forEach(player -> {
                suggestions.add(player.getUsername());
            });

            return suggestions;
        }

        return new ArrayList<>();
    }
    
    public static boolean hasPermission(CommandSource source) {
        if(source instanceof ConsoleCommandSource) return true;
        return LuckPermsProvider.get().getUserManager().getUser(((Player) source).getUniqueId()).getCachedData().getPermissionData().checkPermission("discordlink.admin").asBoolean();
    }

    public static boolean hasPermission(CommandSource source, String subcommand) {
        if(source instanceof ConsoleCommandSource) return true;
        System.out.println(String.format("discordlink.admin.%s", subcommand));
        return (LuckPermsProvider.get().getUserManager().getUser(((Player) source).getUniqueId()).getCachedData().getPermissionData().checkPermission(String.format("discordlink.admin.%s", subcommand.toLowerCase())).asBoolean()
                || LuckPermsProvider.get().getUserManager().getUser(((Player) source).getUniqueId()).getCachedData().getPermissionData().checkPermission("discordlink.admin.*").asBoolean());
    }
}
