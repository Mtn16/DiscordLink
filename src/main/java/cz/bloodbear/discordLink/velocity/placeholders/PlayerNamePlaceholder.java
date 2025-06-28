package cz.bloodbear.discordLink.velocity.placeholders;

import com.velocitypowered.api.proxy.Player;
import cz.bloodbear.discordLink.velocity.interfaces.Placeholder;

public class PlayerNamePlaceholder implements Placeholder {
    @Override
    public String getIdentifier() {
        return "[playerName]";
    }

    @Override
    public String replace(String input, Player player) {
        if (player != null) {
            return input.replace(getIdentifier(), player.getUsername());
        }
        return input;
    }
}
