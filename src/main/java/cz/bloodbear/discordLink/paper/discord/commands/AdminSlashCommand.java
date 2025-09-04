package cz.bloodbear.discordLink.paper.discord.commands;

import cz.bloodbear.discordLink.paper.DiscordLink;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class AdminSlashCommand {
    public static void invoke(@NotNull SlashCommandInteractionEvent event) {
        String subcommand = event.getSubcommandName();
        switch (subcommand) {
            case "unlink":
                unlinkSubcommand(event);
                break;
            case "resync":
                resyncSubcommand(event);
                break;
        }
    }

    private static void unlinkSubcommand(@NotNull SlashCommandInteractionEvent event) {
        OptionMapping option = event.getOption(
                DiscordLink.getInstance().getDiscordConfig().getString("commands.admin.subcommands.unlink.options.user.name", "user")
        );

        if(option == null) {
            event.getHook().editOriginal(DiscordLink.getInstance().getDiscordConfig().getString(
                    "commands.admin.subcommands.unlink.responses.invalid_user", "Invalid user account provided."));
            return;
        }

        Member member = option.getAsMember();
        if(!DiscordLink.getInstance().getDatabaseManager().isDiscordAccountLinked(member.getId())) {
            event.getHook().editOriginal(DiscordLink.getInstance().getDiscordConfig().getString(
                    "commands.admin.subcommands.unlink.responses.not_linked", "Selected user's account is not linked."));
            return;
        }
        UUID uuid = DiscordLink.getInstance().getDatabaseManager().getPlayerByDiscord(member.getId());

        DiscordLink.getInstance().getDatabaseManager().unlinkAccount(uuid.toString());
        event.getHook().editOriginal(DiscordLink.getInstance().getDiscordConfig().getString(
                "commands.admin.subcommands.unlink.responses.success", "Selected user's has been unlinked successfully."));
    }

    private static void resyncSubcommand(@NotNull SlashCommandInteractionEvent event) {
        OptionMapping option = event.getOption(
                DiscordLink.getInstance().getDiscordConfig().getString("commands.admin.subcommands.resync.options.user.name", "user")
        );

        if(option == null) {
            DiscordLink.getInstance().getDiscordBot().syncAllRoles();
            event.getHook().editOriginal(DiscordLink.getInstance().getDiscordConfig().getString(
                    "commands.admin.subcommands.resync.responses.success_all", "All roles have been resynced successfully.")).queue();
            return;
        }

        Member member = option.getAsMember();
        if(!DiscordLink.getInstance().getDatabaseManager().isDiscordAccountLinked(member.getId())) {
            event.getHook().editOriginal(DiscordLink.getInstance().getDiscordConfig().getString(
                    "commands.admin.subcommands.resync.responses.not_linked", "Selected user's account is not linked.")).queue();
            return;
        }
        UUID uuid = DiscordLink.getInstance().getDatabaseManager().getPlayerByDiscord(member.getId());

        DiscordLink.getInstance().getDiscordBot().syncRoles(uuid.toString());
        event.getHook().editOriginal(DiscordLink.getInstance().getDiscordConfig().getString(
                "commands.admin.subcommands.resync.responses.success", "Selected user's has been synced successfully.")).queue();
    }
}
