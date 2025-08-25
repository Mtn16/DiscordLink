package cz.bloodbear.discordLink.velocity.discord.commands;

import cz.bloodbear.discordLink.paper.DiscordLink;
import cz.bloodbear.discordLink.paper.discord.DiscordBot;
import cz.bloodbear.discordLink.paper.utils.JsonConfig;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class UnlinkSlashCommand {
    public static void invoke(@NotNull SlashCommandInteractionEvent event) {
        JsonConfig config = DiscordLink.getInstance().getDiscordConfig();

        String userId = event.getUser().getId();
        if(!DiscordLink.getInstance().getDatabaseManager().isDiscordAccountLinked(userId)) {
            event.getHook().editOriginal(config.getString("commands.unlink.responses.not_linked", "Your account is not linked to any Minecraft account."));
            return;
        }

        UUID uuid = DiscordLink.getInstance().getDatabaseManager().getPlayerByDiscord(userId);

        if(!DiscordBot.getInstance().hasPermission(uuid.toString(), "discordlink.player.unlink")) {
            event.getHook().editOriginal(config.getString("commands.unlink.responses.no_permission", "You do not have permission to unlink your account."));
            return;
        }

        DiscordLink.getInstance().getDatabaseManager().unlinkAccount(uuid.toString());
        event.getHook().editOriginal(config.getString("commands.unlink.responses.success", "Your account has been unlinked successfully."));
    }
}
