package cz.bloodbear.discordLink.placeholders;

import com.velocitypowered.api.proxy.Player;
import cz.bloodbear.discordLink.DiscordLink;
import cz.bloodbear.discordLink.interfaces.Placeholder;
import cz.bloodbear.discordLink.records.DiscordAccount;

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
