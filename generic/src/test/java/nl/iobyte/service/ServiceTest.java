package nl.iobyte.service;

import nl.iobyte.framework.generic.service.ServiceLoader;
import nl.iobyte.framework.generic.service.annotations.Inject;
import nl.iobyte.framework.generic.service.enums.ServiceState;
import nl.iobyte.framework.generic.service.interfaces.Service;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ServiceTest {

    @Test
    public void testRegister() {
        ServiceLoader loader = new ServiceLoader(true);
        Assert.assertNotNull(loader.register(TestServiceA.class));
        Assert.assertNotNull(loader.register(TestServiceB.class));

        loader.init();
        loader.start();
        loader.stop();
    }

    @Test
    public void testTask() {
        ServiceLoader loader = new ServiceLoader(true);
        Assert.assertNotNull(loader.register(TestServiceA.class));
        Assert.assertNotNull(loader.register(TestServiceB.class));

        loader.init();

        for(ServiceState state : List.of(
                ServiceState.NONE,
                ServiceState.INIT,
                ServiceState.START,
                ServiceState.STOP
        )) {
            try {
                loader.addTask(TestServiceA.class, state, () -> {});
                Assert.fail("cannot add task to state " + state);
            } catch(Exception ignored) {
            }
        }

        //Add tasks
        CountDownLatch latch = new CountDownLatch(2);
        loader.addTask(TestServiceB.class, ServiceState.PRE_START, latch::countDown);
        loader.addTask(TestServiceA.class, ServiceState.POST_START, latch::countDown);

        loader.start();

        //Check task state
        Assert.assertEquals(0, latch.getCount());

        //Add tasks
        latch = new CountDownLatch(2);
        loader.addTask(TestServiceB.class, ServiceState.PRE_STOP, latch::countDown);
        loader.addTask(TestServiceA.class, ServiceState.POST_STOP, latch::countDown);

        loader.stop();

        //Check task state
        Assert.assertEquals(0, latch.getCount());
    }

    @Test
    public void testInstancing() {
        ServiceLoader loader = new ServiceLoader(true);
        Assert.assertNotNull(loader.register(TestServiceA.class));
        Assert.assertNotNull(loader.register(TestServiceB.class));

        //Test existence before init
        Assert.assertNull(loader.get(TestServiceA.class));
        Assert.assertNull(loader.get(TestServiceB.class));

        loader.init();

        //Test existence after init
        Assert.assertNotNull(loader.get(TestServiceA.class));
        Assert.assertNotNull(loader.get(TestServiceB.class));

        loader.start();
        loader.stop();
    }

    @Test
    public void testInjection() {
        ServiceLoader loader = new ServiceLoader(true);
        Assert.assertNotNull(loader.register(TestServiceA.class));
        Assert.assertNotNull(loader.register(TestServiceB.class));

        loader.init();

        //Test injection before start
        Assert.assertNull(loader.get(TestServiceA.class).testService);

        loader.start();

        //Test injection after start
        Assert.assertNotNull(loader.get(TestServiceA.class).testService);

        loader.stop();
    }

    @Test
    public void testDependency() {
        ServiceLoader loader = new ServiceLoader(true);
        Assert.assertNotNull(loader.register(TestServiceA.class));
        Assert.assertNotNull(loader.register(TestServiceB.class));
        Assert.assertNotNull(loader.register(TestServiceC.class));

        loader.init();

        //Test injection before start
        Assert.assertNull(loader.get(TestServiceA.class).testService);
        Assert.assertNull(loader.get(TestServiceC.class).testServiceA);
        Assert.assertNull(loader.get(TestServiceC.class).testServiceB);

        loader.start();

        //Test injection after start
        Assert.assertNotNull(loader.get(TestServiceA.class).testService);
        Assert.assertNotNull(loader.get(TestServiceC.class).testServiceA);
        Assert.assertNotNull(loader.get(TestServiceC.class).testServiceB);

        loader.stop();
    }

    private static class TestServiceA implements Service {

        @Inject
        public TestServiceB testService;

    }

    private static class TestServiceB implements Service {


    }

    private static class TestServiceC implements Service {

        @Inject
        public TestServiceA testServiceA;

        @Inject
        public TestServiceB testServiceB;

    }

}
