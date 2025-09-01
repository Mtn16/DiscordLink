package cz.bloodbear.discordLink.paper.discord;

import cz.bloodbear.discordLink.paper.DiscordLink;
import cz.bloodbear.discordLink.core.records.RoleEntry;
import cz.bloodbear.discordLink.core.utils.ConsoleColor;
import cz.bloodbear.discordLink.paper.discord.listener.SlashCommandListener;
import cz.bloodbear.discordLink.paper.utils.DatabaseManager;
import cz.bloodbear.discordLink.core.utils.DiscordUtils;
import cz.bloodbear.discordLink.paper.utils.JsonConfig;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class DiscordBot extends ListenerAdapter {
    private static DiscordBot instance;
    private final JDA jda;
    private final DatabaseManager databaseManager;
    private final String guildId;

    public DiscordBot(String token, String guildId, String presence) {
        this.guildId = guildId;
        this.databaseManager = DiscordLink.getInstance().getDatabaseManager();
        this.jda = JDABuilder.createDefault(token)
                .addEventListeners(this)
                .setActivity(Activity.customStatus(presence))
                .addEventListeners(new SlashCommandListener())
                .build();

        updateCommands();
        instance = this;
    }


    public void updateCommands() {
        JsonConfig config = DiscordLink.getInstance().getDiscordConfig();

        SlashCommandData unlinkCommand = Commands.slash(
                config.getString("commands.unlink.name", "unlink"),
                config.getString("commands.unlink.description", "Unlinks your Discord account from linked Minecraft server account."));

        SlashCommandData adminCommand = Commands.slash(
                        config.getString("commands.admin.name", "admin"),
                        config.getString("commands.admin.description", "DiscordLink admin command"));

        unlinkCommand.setContexts(InteractionContextType.GUILD);
        adminCommand.setContexts(InteractionContextType.GUILD);
        adminCommand.setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR));

        SubcommandData unlinkSubCommand = new SubcommandData(
                config.getString("commands.admin.subcommands.unlink.name", "unlink"),
                config.getString("commands.admin.subcommands.unlink.description", "Unlinks selected account from linked Minecraft server account."
                ));

        unlinkSubCommand.addOption(OptionType.MENTIONABLE,
                config.getString("commands.admin.subcommands.unlink.options.user.name", "user"),
                config.getString("commands.admin.subcommands.unlink.options.user.description", "The user account to unlink"), true);

        SubcommandData resyncSubCommand = new SubcommandData(
                config.getString("commands.admin.subcommands.resync.name", "resync"),
                config.getString("commands.admin.subcommands.resync.description", "Syncs selected account's roles."
                ));

        resyncSubCommand.addOption(OptionType.MENTIONABLE,
                config.getString("commands.admin.subcommands.resync.options.user.name", "user"),
                config.getString("commands.admin.subcommands.resync.options.user.description", "The user account to resync"), false);

        adminCommand.addSubcommands(
                unlinkSubCommand,
                resyncSubCommand
        );


        getGuild().updateCommands().addCommands(
                unlinkCommand,
                adminCommand
        );
    }

    public Guild getGuild() {
        return jda.getGuildById(guildId);
    }

    public static DiscordBot getInstance() { return instance; }

    public JDA getJdaInstance() {
        return jda;
    }

    public String getGuildId() {
        return guildId;
    }

    public String getClientId() {
        return DiscordLink.getInstance().getClientId();
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
            try {
                User user = jda.retrieveUserById(discordId).complete();
                if(user != null) {
                    syncRoles(user, uuid);
                }
            } catch (Exception ignored) {}
        });
    }

    public void syncRoles(User user, String uuid) {
        new Thread(() -> {
            Guild guild = jda.getGuildById(guildId);
            if (guild == null) return;

            Member member = guild.retrieveMemberById(user.getId()).complete();

            if (member == null) return;

            for (RoleEntry role : DiscordLink.getInstance().getRoles()) {
                try {
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
                } catch (Exception ignore) {}
            }
        }).start();
    }

    public void syncRoles(String uuid) {
        new Thread(() -> {
            User user = jda.retrieveUserById(databaseManager.getDiscordAccount(uuid).id()).complete();
            Guild guild = jda.getGuildById(guildId);
            if (guild == null) return;

            Member member = guild.retrieveMemberById(user.getId()).complete();

            if (member == null) return;

            for (RoleEntry role : DiscordLink.getInstance().getRoles()) {
                try {
                    Role guildRole = guild.getRoleById(role.roleId());
                    if(hasPermission(uuid, role.permission())) {
                        guild.addRoleToMember(member, guildRole).queue();
                        //System.out.println("Adding role " + guildRole.getName() + " to " + member.getUser().getGlobalName());

                    } else {
                        guild.removeRoleFromMember(member, guildRole).queue();
                    }
                } catch (Exception ignored) {}
            }
        }).start();
    }

    public boolean hasPermission(String uuid, String permission) {
        LuckPerms luckPerms = LuckPermsProvider.get();
        net.luckperms.api.model.user.User user = luckPerms.getUserManager().getUser(UUID.fromString(uuid));

        if (user == null) return false;

        return user.getCachedData().getPermissionData().checkPermission(permission).asBoolean();
    }

    public void removeSyncRoles(String memberId) {
        try {
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
        } catch (Exception e) {
            DiscordLink.getInstance().getLogger().severe(e.getMessage());
        }
    }
}
