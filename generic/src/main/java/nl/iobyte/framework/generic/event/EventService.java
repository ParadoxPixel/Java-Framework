package nl.iobyte.framework.generic.event;

import nl.iobyte.framework.generic.event.annotations.EventHandler;
import nl.iobyte.framework.generic.event.enums.HandlerPriority;
import nl.iobyte.framework.generic.event.interfaces.IEvent;
import nl.iobyte.framework.generic.event.interfaces.IEventHandler;
import nl.iobyte.framework.generic.event.interfaces.IEventListener;
import nl.iobyte.framework.generic.event.objects.EventListener;
import nl.iobyte.framework.generic.exceptional.ExceptionalFuture;
import nl.iobyte.framework.generic.invoker.enums.TaskType;
import nl.iobyte.framework.generic.reflections.objects.ReflectedType;
import nl.iobyte.framework.generic.service.interfaces.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class EventService implements Service {

    private final Map<Class<? extends IEvent>, EventListener<?>> listeners = new ConcurrentHashMap<>();

    /**
     * Register event listener from class
     *
     * @param listener Class<? extends IEventListener>
     */
    public void register(Class<? extends IEventListener> listener) {
        try {
            register(ReflectedType.of(listener).getConstructor().newInstance());
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Register multiple listeners from class
     *
     * @param listeners Class<? extends IEventListener>[]
     */
    @SafeVarargs
    public final void register(Class<? extends IEventListener>... listeners) {
        for(Class<? extends IEventListener> listener : listeners)
            register(listener);
    }

    /**
     * Register event listener from instance
     *
     * @param listener IEventListener
     */
    public void register(IEventListener listener) {
        assert listener != null;

        ReflectedType.of(listener.getClass())
                     .getMethods()
                     .stream()
                     .filter(method -> method.hasAnnotation(EventHandler.class))
                     .forEach(method -> {
                         EventHandler eventHandler = method.getAnnotation(EventHandler.class);
                         if(eventHandler.priority() == null)
                             return;

                         if(method.getParameterTypes().length != 1)
                             return;

                         ReflectedType<?> clazz = ReflectedType.of(method.getParameterTypes()[0]);
                         if(clazz.isAssignable(IEvent.class))
                             return;

                         //noinspection unchecked
                         on((Class<? extends IEvent>) clazz.getType(), eventHandler.priority(), e -> {
                             try {
                                 method.invoke(listener, e);
                             } catch(Exception ex) {
                                 ex.printStackTrace();
                             }
                         });
                     });
    }

    /**
     * Register multiple listeners from instance
     *
     * @param listeners IEventListener[]
     */
    public EventService register(IEventListener... listeners) {
        for(IEventListener listener : listeners)
            register(listener);

        return this;
    }

    /**
     * Register handler for event
     *
     * @param event   T
     * @param handler IEventHandler<T>
     * @param <T>     T
     * @return EventService
     */
    public <T extends IEvent> EventService on(Class<T> event, IEventHandler<T> handler) {
        return on(event, HandlerPriority.NORMAL, handler);
    }

    /**
     * Register handler for event with priority
     *
     * @param event    T
     * @param priority EventPriority
     * @param handler  IEventHandler<T>
     * @param <T>      T
     * @return EventService
     */
    public <T extends IEvent> EventService on(Class<T> event, HandlerPriority priority, IEventHandler<T> handler) {
        assert event != null;
        assert priority != null;
        assert handler != null;

        @SuppressWarnings("unchecked")
        EventListener<T> eventListener = (EventListener<T>) listeners.computeIfAbsent(
                event,
                key -> {
                    try {
                        return ReflectedType.of(EventListener.class)
                                            .getConstructor(ReflectedType.class)
                                            .newInstance(ReflectedType.of(event));
                    } catch(Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        eventListener.handle(priority, handler);
        return this;
    }

    /**
     * Fire event with callback
     *
     * @param event instance
     * @return callback
     */
    public CompletableFuture<Void> fire(IEvent event) {
        return ExceptionalFuture.of(() -> {
            fireSync(event);
            return (Void) null;
        }).schedule(TaskType.INTERNAL);
    }

    /**
     * Fire event sync
     *
     * @param event IEvent
     */
    public void fireSync(IEvent event) {
        assert event != null;

        listeners.values().forEach(eventListener -> {
            if(!eventListener.getType().isAssignable(event))
                return;

            eventListener.fireRaw(event);
        });
    }

}
