package cz.bloodbear.discordLink.paper.discord.commands;

import cz.bloodbear.discordLink.core.utils.UptimeUtils;
import cz.bloodbear.discordLink.paper.DiscordLink;
import cz.bloodbear.discordLink.paper.utils.JsonConfig;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import org.bukkit.util.CachedServerIcon;
import org.jetbrains.annotations.NotNull;

public class StatusSlashCommand {
    public static void invoke(@NotNull SlashCommandInteractionEvent event) {
        CachedServerIcon icon = DiscordLink.getInstance().getServer().getServerIcon();

        JsonConfig config = DiscordLink.getInstance().getDiscordConfig();

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(config.getString("commands.status.responses.success.title", "status"))
                .setColor(config.getInt("commands.status.responses.success.color", 0x00FF00));

        config.getSectionObject("commands.status.responses.success.fields").getAsJsonArray().forEach(jsonElement -> {
            embed.addField(
                    jsonElement.getAsJsonObject().get("name").getAsString(),
                    resolveFieldType(jsonElement.getAsJsonObject().get("type").getAsString()),
                    jsonElement.getAsJsonObject().get("inline").getAsBoolean()
            );
        });

        if(config.getBoolean("commands.status.responses.success.thumbnail", false)) {
            embed.setThumbnail("attachment://server-icon.png");
            event.reply("").setFiles(FileUpload.fromData(icon.getData().getBytes(), "server-icon.png"));
        }
    }

    private static String resolveFieldType(String type) {
        return switch (type) {
            case "UPTIME" -> UptimeUtils.formatDuration(DiscordLink.getInstance().getUptime());
            case "MAX" -> String.valueOf(DiscordLink.getInstance().getServer().getMaxPlayers());
            case "ONLINE" -> String.valueOf(DiscordLink.getInstance().getServer().getOnlinePlayers().size());
            default -> "Unknown type";
        };
    }
}
