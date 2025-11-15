package cz.bloodbear.discordLink.core.event;

import cz.bloodbear.discordLink.core.utils.event.Event;

public class PlayerSyncEvent implements Event {
    private final String uuid;

    public PlayerSyncEvent(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }
}
