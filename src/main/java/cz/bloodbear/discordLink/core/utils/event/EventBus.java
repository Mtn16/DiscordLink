package cz.bloodbear.discordLink.core.utils.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventBus {
    private final Map<Class<? extends Event>, List<EventListener<? extends Event>>> listeners = new HashMap<>();

    public <T extends Event> void registerListener(Class<T> eventType, EventListener<T> listener) {
        listeners.computeIfAbsent(eventType, c -> new ArrayList<>()).add(listener);
    }

    @SuppressWarnings("unchecked")
    public <T extends Event> void callEvent(T event) {
        new Thread(() -> {
            Class<? extends Event> eventType = event.getClass();
            List<EventListener<? extends Event>> list = listeners.get(eventType);
            if (list == null) return;

            for (EventListener<?> listener : list) {
                ((EventListener<T>) listener).onEvent(event);
            }
        }).start();
    }
}
