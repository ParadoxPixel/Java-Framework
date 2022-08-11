package nl.iobyte.event;

import nl.iobyte.framework.generic.event.EventService;
import nl.iobyte.framework.generic.event.annotations.EventHandler;
import nl.iobyte.framework.generic.event.enums.HandlerPriority;
import nl.iobyte.framework.generic.event.interfaces.ICancellable;
import nl.iobyte.framework.generic.event.interfaces.IEvent;
import nl.iobyte.framework.generic.event.interfaces.IEventListener;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class EventTest {

    @Test
    public void on() {
        EventService service = new EventService();

        service.on(TestEvent.class, e -> {})
               .on(TestEvent.class, HandlerPriority.NORMAL, e -> {});
    }

    @Test
    public void fire() {
        EventService service = new EventService();

        CountDownLatch latch = new CountDownLatch(2);
        service.on(TestEvent.class, e -> latch.countDown())
               .on(TestEvent.class, HandlerPriority.NORMAL, e -> latch.countDown());

        service.fireSync(new TestEvent());

        Assert.assertEquals(0, latch.getCount());
    }

    @Test
    public void register() {
        EventService service = new EventService();

        service.register(TestEventListener.class);
    }

    @Test
    public void cancel() {
        EventService service = new EventService();

        CountDownLatch latch = new CountDownLatch(3);
        service.on(CancellableEvent.class, e -> {
                   e.cancel();
                   latch.countDown();
               }).on(CancellableEvent.class, HandlerPriority.HIGH, e -> latch.countDown())
               .on(CancellableEvent.class, HandlerPriority.MONITOR, e -> latch.countDown());

        service.fireSync(new CancellableEvent());

        Assert.assertEquals(1, latch.getCount());
    }

    public static class TestEvent implements IEvent {}

    public static class CancellableEvent implements IEvent, ICancellable {

        private boolean cancelled = false;

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public void cancel() {
            cancelled = true;
        }

    }

    public static class TestEventListener implements IEventListener {

        @EventHandler(priority = HandlerPriority.MONITOR)
        public void on(TestEvent e) {

        }

    }

}
