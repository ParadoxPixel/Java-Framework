package nl.iobyte.framework.generic.event.objects;

import nl.iobyte.framework.generic.event.enums.HandlerPriority;
import nl.iobyte.framework.generic.event.interfaces.ICancellable;
import nl.iobyte.framework.generic.event.interfaces.IEvent;
import nl.iobyte.framework.generic.event.interfaces.IEventHandler;
import nl.iobyte.framework.generic.reflections.objects.ReflectedType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventListener<T extends IEvent> {

    private static final HandlerPriority[] priorities = HandlerPriority.values();

    private final ReflectedType<T> type;
    private final List<IEventHandler<T>> empty = new ArrayList<>();
    private final Map<HandlerPriority, List<IEventHandler<T>>> handlers = new ConcurrentHashMap<>();

    public EventListener(ReflectedType<T> type) {
        this.type = type;
    }

    /**
     * Get type of listener
     *
     * @return Class<T>
     */
    public ReflectedType<T> getType() {
        return type;
    }

    /**
     * Register handler with priority for event T
     *
     * @param priority EventPriority
     * @param handler  IEventHandler<T>
     */
    public void handle(HandlerPriority priority, IEventHandler<T> handler) {
        handlers.computeIfAbsent(
                priority,
                key -> new CopyOnWriteArrayList<>()
        ).add(handler);
    }

    /**
     * Fire event of unknown type
     *
     * @param obj IEvent
     */
    public void fireRaw(IEvent obj) {
        try {
            fire(type.cast(obj));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Fire event T
     *
     * @param event T
     */
    public void fire(T event) {
        assert event != null;

        ICancellable cancellable = event instanceof ICancellable ? (ICancellable) event : null;

        //Run for all priorities except MONITOR
        for(int i = 0; i < priorities.length - 1; i++) {
            if(cancellable != null && cancellable.isCancelled())
                break;

            for(IEventHandler<T> handler : handlers.getOrDefault(priorities[i], empty)) {
                if(cancellable != null && cancellable.isCancelled())
                    break;

                try {
                    handler.handle(event);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }

        //Call monitor handlers
        handlers.getOrDefault(
                HandlerPriority.MONITOR,
                empty
        ).forEach(handler -> {
            try {
                handler.handle(event);
            } catch(Exception e) {
                e.printStackTrace();
            }
        });
    }

}
