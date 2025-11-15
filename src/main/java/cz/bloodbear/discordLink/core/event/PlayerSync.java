package cz.bloodbear.discordLink.core.event;

import cz.bloodbear.discordLink.core.records.RoleEntry;
import cz.bloodbear.discordLink.core.utils.DB;
import cz.bloodbear.discordLink.core.utils.LuckPermsProvider;
import cz.bloodbear.discordLink.core.utils.event.EventBus;
import cz.bloodbear.discordLink.paper.DiscordLink;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

public class PlayerSync {
    public PlayerSync(EventBus eventBus, DB databaseManager, JDA jda, String guildId) {
        eventBus.registerListener(PlayerSyncEvent.class, event -> new Thread(() -> {
            User user = jda.retrieveUserById(databaseManager.getDiscordAccount(event.getUuid().toString()).id()).complete();
            Guild guild = jda.getGuildById(guildId);
            if (guild == null) return;

            Member member = guild.retrieveMemberById(user.getId()).complete();

            if (member == null) return;

            for (RoleEntry role : DiscordLink.getInstance().getRoles()) {
                try {
                    Role guildRole = guild.getRoleById(role.roleId());
                    if(LuckPermsProvider.hasPermission(event.getUuid().toString(), role.permission())) {
                        guild.addRoleToMember(member, guildRole).queue();
                    } else {
                        guild.removeRoleFromMember(member, guildRole).queue();
                    }
                } catch (Exception ignored) {}
            }
        }).start());
    }
}
