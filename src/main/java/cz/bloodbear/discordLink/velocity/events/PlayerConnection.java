package cz.bloodbear.discordLink.velocity.events;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import cz.bloodbear.discordLink.velocity.DiscordLink;

public class PlayerConnection {

    @Subscribe
    public void onPostLogin(PostLoginEvent event) {
        if(DiscordLink.getInstance().getDatabaseManager().isLinked(event.getPlayer().getUniqueId().toString())) {
            DiscordLink.getInstance().getDiscordBot().syncRoles(
                    event.getPlayer().getUniqueId().toString()
            );
        }
    }
}
