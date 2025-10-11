package cz.bloodbear.discordLink.paper.discord.listeners;

import cz.bloodbear.discordLink.paper.DiscordLink;
import cz.bloodbear.discordLink.paper.discord.commands.AdminSlashCommand;
import cz.bloodbear.discordLink.paper.discord.commands.StatusSlashCommand;
import cz.bloodbear.discordLink.paper.discord.commands.UnlinkSlashCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class SlashCommandListener extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        String command = event.getName();
        event.deferReply().setEphemeral(true).queue();

        if(command.equals(DiscordLink.getInstance().getDiscordConfig().getString("commands.unlink.name", "unlink"))) {
            UnlinkSlashCommand.invoke(event);
        } else if (command.equals(DiscordLink.getInstance().getDiscordConfig().getString("commands.admin.name", "admin"))) {
            AdminSlashCommand.invoke(event);
        } else if(command.equals(DiscordLink.getInstance().getDiscordConfig().getString("commands.status.name", "status"))) {
            StatusSlashCommand.invoke(event);
        } else {
            event.getHook().editOriginal(
                    DiscordLink.getInstance().getDiscordConfig().getString("commands.unknown.response", "Unknown command.")
            ).queue();
        }
    }
}
