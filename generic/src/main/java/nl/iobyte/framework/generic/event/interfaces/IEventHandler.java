package nl.iobyte.framework.generic.event.interfaces;

public interface IEventHandler<T extends IEvent> {

    /**
     * Handle event T
     *
     * @param event T
     */
    void handle(T event);

}
