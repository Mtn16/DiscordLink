package cz.bloodbear.discordLink.velocity.events;

import com.velocitypowered.api.event.Subscribe;
import cz.bloodbear.discordLink.velocity.DiscordLink;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerConnection {

    @Subscribe
    public void onLogin(PlayerLoginEvent event) {
        DiscordLink.getInstance().getDiscordBot().syncRoles(
                event.getPlayer().getUniqueId().toString()
        );
    }
}
