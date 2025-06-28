package cz.bloodbear.discordLink.paper.interfaces;

import org.bukkit.entity.Player;

public interface Placeholder {
    String getIdentifier();
    String replace(String input, Player player);
}
