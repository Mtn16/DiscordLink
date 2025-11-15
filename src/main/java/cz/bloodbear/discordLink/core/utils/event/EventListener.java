package cz.bloodbear.discordLink.core.utils.event;

public interface EventListener<T extends Event> {
    void onEvent(T event);
}
