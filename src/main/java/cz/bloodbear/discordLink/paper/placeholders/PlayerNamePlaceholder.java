package cz.bloodbear.discordLink.paper.placeholders;

import cz.bloodbear.discordLink.paper.interfaces.Placeholder;
import org.bukkit.entity.Player;

public class PlayerNamePlaceholder implements Placeholder {
    @Override
    public String getIdentifier() {
        return "[playerName]";
    }

    @Override
    public String replace(String input, Player player) {
        if (player != null) {
            return input.replace(getIdentifier(), player.getName());
        }
        return input;
    }
}
