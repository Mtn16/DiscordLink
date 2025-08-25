package cz.bloodbear.discordLink.paper.discord.listener;

import cz.bloodbear.discordLink.paper.DiscordLink;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class SlashCommandListener extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        String command = event.getName();
        event.deferReply().setEphemeral(true).queue();

        if(command.equals(DiscordLink.getInstance().getDiscordConfig().getString("commands.unlink.name", "unlink"))) {
            //TODO: Add unlink slash command UnlinkSlashCommand.invoke(event);
        } else if (command.equals(DiscordLink.getInstance().getDiscordConfig().getString("commands.admin.name", "admin"))) {
            //TODO: Add admin slash command AdminSlashCommand.invoke(event);
        } else {
            event.getHook().editOriginal(
                    DiscordLink.getInstance().getDiscordConfig().getString("commands.unknown.response", "Unknown command.")
            ).queue();
        }
    }
}
