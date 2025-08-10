package cz.bloodbear.discordLink.paper.events;

import cz.bloodbear.discordLink.paper.DiscordLink;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerConnection implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if(DiscordLink.getInstance().getDatabaseManager().isLinked(event.getPlayer().getUniqueId().toString())) {
            DiscordLink.getInstance().getDiscordBot().syncRoles(
                    event.getPlayer().getUniqueId().toString()
            );
        }
    }
}
