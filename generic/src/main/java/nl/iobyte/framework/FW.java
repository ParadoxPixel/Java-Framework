package nl.iobyte.framework;

import nl.iobyte.framework.generic.config.ConfigService;
import nl.iobyte.framework.generic.event.EventService;
import nl.iobyte.framework.generic.invoker.FWInvoker;
import nl.iobyte.framework.generic.serializer.SerializerService;
import nl.iobyte.framework.generic.service.ServiceLoader;
import nl.iobyte.framework.generic.service.interfaces.Service;
import nl.iobyte.framework.structures.cmap.ClassMap;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class FW extends ServiceLoader {

    private static final AtomicReference<FW> instance = new AtomicReference<>(null);
    protected static final ClassMap<FWInvoker> invokers = new ClassMap<>();
    private final FWInvoker invoker;

    protected FW(FWInvoker invoker, boolean abortOnException) {
        super(abortOnException);
        instance.set(this);
        this.invoker = invoker;

        registerBulk(
                ConfigService.class,
                EventService.class,
                SerializerService.class
        );
    }

    /**
     * Get instance of Framework
     *
     * @return framework
     */
    public static FW getInstance() {
        return instance.get();
    }

    /**
     * Get invoker of type
     *
     * @param type of invoker
     * @param <T>  type of invokers
     * @return invoker instance
     */
    public static <T extends FWInvoker> T getInvoker(Class<T> type) {
        return invokers.get(type, true);
    }

    /**
     * Acquire new instance of non exist
     *
     * @param invoker framework invoker
     * @return framework instance
     */
    public static FW acquireInstance(FWInvoker invoker) {
        return acquireInstance(invoker, true);
    }

    /**
     * Acquire new instance of non exist
     *
     * @param invoker          framework invoker
     * @param abortOnException whether to abort when an exception occurs;
     * @return framework instance
     */
    public static FW acquireInstance(FWInvoker invoker, boolean abortOnException) {
        invokers.register(invoker);
        if(instance.get() != null)
            return instance.get();

        return new FW(invoker, abortOnException);
    }

    /**
     * Get framework invoker
     *
     * @return invoker instance
     */
    public static FWInvoker getInvoker() {
        return instance.get().invoker;
    }

    /**
     * Get service by type
     */
    public static <T extends Service> T service(Class<T> service) {
        return Optional.ofNullable(instance.get())
                       .map(instance -> instance.get(service))
                       .orElse(null);
    }

}
