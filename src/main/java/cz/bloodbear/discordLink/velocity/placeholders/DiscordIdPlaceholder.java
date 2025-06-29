package cz.bloodbear.discordLink.velocity.placeholders;

import com.velocitypowered.api.proxy.Player;
import cz.bloodbear.discordLink.velocity.DiscordLink;
import cz.bloodbear.discordLink.velocity.interfaces.Placeholder;
import cz.bloodbear.discordLink.core.records.DiscordAccount;

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
