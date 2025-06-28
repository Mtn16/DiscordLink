package cz.bloodbear.discordLink.velocity.interfaces;

import com.velocitypowered.api.proxy.Player;

public interface Placeholder {
    String getIdentifier();
    String replace(String input, Player player);
}
