package cz.bloodbear.discordLink.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
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

        if((source instanceof Player) && !LuckPermsProvider.get().getUserManager().getUser(((Player)invocation.source()).getUniqueId()).getCachedData().getPermissionData().checkPermission("discordlink.admin").asBoolean()) {
            source.sendMessage(DiscordLink.getInstance().formatMessage(DiscordLink.getInstance().getMessage("command.admin.noperms")));
            return;
        }

        if(args.length == 0) {
            source.sendMessage(DiscordLink.getInstance().formatMessage(DiscordLink.getInstance().getMessage("command.admin.usage", (Player) invocation.source())));
            return;
        }


        if(args[0].equalsIgnoreCase("resync")) {
            if(args.length != 2) {
                source.sendMessage(DiscordLink.getInstance().formatMessage(DiscordLink.getInstance().getMessage("command.admin.usage", (Player) invocation.source())));
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
        if(invocation.arguments().length <= 1) {
            return Arrays.asList("resync", "resyncAll", "reload");
        } else if (invocation.arguments().length == 2) {
            List<String> suggestions = new ArrayList<>();
            DiscordLink.getInstance().getServer().getAllPlayers().forEach(player -> {
                suggestions.add(player.getUsername());
            });

            return suggestions;
        }

        return new ArrayList<>();
    }


    @Override
    public boolean hasPermission(Invocation invocation) {
        if((invocation.source() instanceof Player)) {
            return LuckPermsProvider.get().getUserManager().getUser(((Player)invocation.source()).getUniqueId()).getCachedData().getPermissionData().checkPermission("discordlink.admin").asBoolean();
        } else {
            return true;
        }
    }
}
