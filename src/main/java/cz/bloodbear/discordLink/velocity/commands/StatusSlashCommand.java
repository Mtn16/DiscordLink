package cz.bloodbear.discordLink.velocity.commands;

import com.velocitypowered.api.util.Favicon;
import cz.bloodbear.discordLink.core.utils.UptimeUtils;
import cz.bloodbear.discordLink.velocity.DiscordLink;
import cz.bloodbear.discordLink.velocity.utils.JsonConfig;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class StatusSlashCommand {
    public static void invoke(@NotNull SlashCommandInteractionEvent event) {
        Path iconPath = Paths.get("").toAbsolutePath().resolve("server-icon.png");
        byte[] icon = new byte[0];
        try {
            icon = Files.readAllBytes(iconPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        JsonConfig config = DiscordLink.getInstance().getDiscordConfig();

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(config.getString("commands.status.responses.success.title", "status"))
                .setColor(config.getInt("commands.status.responses.success.color", 0x00FF00));

        config.getSectionObject("commands.status.responses.success.fields").getAsJsonArray().forEach(jsonElement -> {
            if(!Objects.equals(resolveFieldType(jsonElement.getAsJsonObject().get("type").getAsString()), "MAX")) {
                embed.addField(
                        jsonElement.getAsJsonObject().get("name").getAsString(),
                        resolveFieldType(jsonElement.getAsJsonObject().get("type").getAsString()),
                        jsonElement.getAsJsonObject().get("inline").getAsBoolean()
                );
            }
        });

        if(config.getBoolean("commands.status.responses.success.thumbnail", false)) {
            embed.setThumbnail("attachment://server-icon.png");
            event.reply("").setFiles(FileUpload.fromData(icon, "server-icon.png"));
        }
    }

    private static String resolveFieldType(String type) {
        return switch (type) {
            case "UPTIME" -> UptimeUtils.formatDuration(DiscordLink.getInstance().getUptime());
            case "ONLINE" -> String.valueOf(DiscordLink.getInstance().getServer().getPlayerCount());
            default -> "Unknown type";
        };
    }
}
