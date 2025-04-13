package cz.bloodbear.discordLink.interfaces;

import com.velocitypowered.api.proxy.Player;

public interface Placeholder {
    String getIdentifier();
    String replace(String input, Player player);
}
