package cz.bloodbear.discordLink.paper.placeholders;

import cz.bloodbear.discordLink.paper.DiscordLink;
import cz.bloodbear.discordLink.paper.interfaces.Placeholder;
import cz.bloodbear.discordLink.core.records.DiscordAccount;
import org.bukkit.entity.Player;

public class DiscordIdPlaceholder implements Placeholder {
    @Override
    public String getIdentifier() {
        return "[discordId]";
    }

    @Override
    public String replace(String input, Player player) {
        if (player != null) {
            DiscordAccount discordAccount = DiscordLink.getInstance().getDatabaseManager().getDiscordAccount(player.getUniqueId().toString());
            String id = discordAccount != null ? discordAccount.id() : DiscordLink.getInstance().getMessage("generic.none");
            return input.replace(getIdentifier(), id);
        }
        return input;
    }
}
