package cz.bloodbear.discordLink.placeholders;

import com.velocitypowered.api.proxy.Player;
import cz.bloodbear.discordLink.interfaces.Placeholder;

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
