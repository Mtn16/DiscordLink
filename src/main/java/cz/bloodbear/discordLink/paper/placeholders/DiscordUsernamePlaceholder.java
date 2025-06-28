package cz.bloodbear.discordLink.paper.placeholders;

import cz.bloodbear.discordLink.paper.DiscordLink;
import cz.bloodbear.discordLink.paper.interfaces.Placeholder;
import cz.bloodbear.discordLink.paper.records.DiscordAccount;
import org.bukkit.entity.Player;

public class DiscordUsernamePlaceholder implements Placeholder {
    @Override
    public String getIdentifier() {
        return "[discordUsername]";
    }

    @Override
    public String replace(String input, Player player) {
        if (player != null) {
            DiscordAccount discordAccount = DiscordLink.getInstance().getDatabaseManager().getDiscordAccount(player.getUniqueId().toString());
            String username = discordAccount != null ? discordAccount.username() : DiscordLink.getInstance().getMessage("generic.none");
            return input.replace(getIdentifier(), username);
        }
        return input;
    }
}
