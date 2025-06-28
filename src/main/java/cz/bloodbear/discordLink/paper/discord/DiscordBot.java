package cz.bloodbear.discordLink.paper.discord;

import cz.bloodbear.discordLink.paper.DiscordLink;
import cz.bloodbear.discordLink.paper.records.RoleEntry;
import cz.bloodbear.discordLink.paper.utils.ConsoleColor;
import cz.bloodbear.discordLink.paper.utils.DatabaseManager;
import cz.bloodbear.discordLink.paper.utils.DiscordUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class DiscordBot extends ListenerAdapter {
    private final JDA jda;
    private final DatabaseManager databaseManager;
    private final String guildId;

    public DiscordBot(String token, String guildId, String presence) {
        this.guildId = guildId;
        this.databaseManager = DiscordLink.getInstance().getDatabaseManager();
        this.jda = JDABuilder.createDefault(token)
                .addEventListeners(this)
                .setActivity(Activity.customStatus(presence))
                .build();
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        DiscordLink.getInstance().getLogger().info(ConsoleColor.green("Bot ready!"));
        startAutoSync();
    }

    public void startAutoSync() {
        DiscordLink.getInstance().getServer().getScheduler().runTaskTimerAsynchronously(
                DiscordLink.getInstance(),
                this::syncAllRoles,
                0L,
                2L * 60 * 60 * 20
                );
    }

    public void syncAllRoles() {
        Guild guild = jda.getGuildById(guildId);
        if(guild == null) {
            DiscordLink.getInstance().getLogger().warning(ConsoleColor.yellow("Guild not found: " + guildId));
            return;
        }

        DiscordLink.getInstance().getDatabaseManager().getAllLinkedAccounts().forEach((uuid, discordId) -> {
            User user = jda.retrieveUserById(discordId).complete();
            if(user != null) {
                syncRoles(user, uuid);
            }
        });
    }

    public void syncRoles(User user, String uuid) {
        Guild guild = jda.getGuildById(guildId);
        if (guild == null) return;

        Member member = guild.retrieveMemberById(user.getId()).complete();

        if (member == null) return;

        for (RoleEntry role : DiscordLink.getInstance().getRoles()) {
            Role guildRole = guild.getRoleById(role.roleId());
            if(hasPermission(uuid, role.permission())) {
                if(!DiscordUtils.hasRole(member, role.roleId())) {
                    assert guildRole != null;
                    guild.addRoleToMember(member, guildRole);
                }
            } else {
                if(DiscordUtils.hasRole(member, role.roleId())) {
                    assert guildRole != null;
                    guild.removeRoleFromMember(member, guildRole);
                }
            }
        }
    }

    public void syncRoles(String uuid) {
        User user = jda.retrieveUserById(databaseManager.getDiscordAccount(uuid).id()).complete();
        Guild guild = jda.getGuildById(guildId);
        if (guild == null) return;

        Member member = guild.retrieveMemberById(user.getId()).complete();

        if (member == null) return;

        for (RoleEntry role : DiscordLink.getInstance().getRoles()) {
            Role guildRole = guild.getRoleById(role.roleId());
            if(hasPermission(uuid, role.permission())) {
                guild.addRoleToMember(member, guildRole).queue();
                System.out.println("Adding role " + guildRole.getName() + " to " + member.getUser().getGlobalName());

            } else {
                guild.removeRoleFromMember(member, guildRole).queue();
            }
        }
    }

    public boolean hasPermission(String uuid, String permission) {
        LuckPerms luckPerms = LuckPermsProvider.get();
        net.luckperms.api.model.user.User user = luckPerms.getUserManager().getUser(UUID.fromString(uuid));

        if (user == null) return false;

        return user.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
    }

    public void removeSyncRoles(String memberId) {
        Guild guild = jda.getGuildById(guildId);
        if (guild == null) return;

        User user = jda.retrieveUserById(memberId).complete();
        Member member = guild.retrieveMemberById(user.getId()).complete();

        if(member == null) return;

        List<RoleEntry> roles = DiscordLink.getInstance().getRoles();

        roles.forEach(roleEntry -> {
            if(DiscordUtils.hasRole(member, roleEntry.roleId())) {
                guild.removeRoleFromMember(member, guild.getRoleById(roleEntry.roleId())).queue();
            }
        });
    }
}
