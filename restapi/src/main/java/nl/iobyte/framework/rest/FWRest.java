package nl.iobyte.framework.rest;

import nl.iobyte.framework.FW;
import nl.iobyte.framework.data.cache.CacheService;
import nl.iobyte.framework.data.database.DatabaseService;
import nl.iobyte.framework.data.model.ModelService;
import nl.iobyte.framework.generic.invoker.FWInvoker;
import nl.iobyte.framework.generic.service.interfaces.Service;
import nl.iobyte.framework.network.message.MessageService;
import nl.iobyte.framework.network.rabbitmq.RabbitMQService;
import nl.iobyte.framework.rest.validator.ValidatorService;

import java.util.concurrent.atomic.AtomicReference;

public class FWRest extends FW {

    private static final AtomicReference<FWRest> instance = new AtomicReference<>(null);
    private final FWInvoker invoker;

    protected FWRest(FWInvoker invoker, boolean abortOnException) {
        super(invoker, abortOnException);
        instance.set(this);
        this.invoker = invoker;

        registerBulk(
                ValidatorService.class,

                //Data
                CacheService.class,
                DatabaseService.class,
                ModelService.class,

                //Network
                MessageService.class,
                RabbitMQService.class
        );
    }

    /**
     * Get instance of Framework
     *
     * @return framework
     */
    public static FWRest getInstance() {
        return instance.get();
    }

    /**
     * Get plugin invoker of type
     *
     * @param type of plugin invoker
     * @param <T>  type of plugin invokers
     * @return plugin invoker instance
     */
    public static <T extends FWInvoker> T getPluginInvoker(Class<T> type) {
        return getInvoker(type);
    }

    /**
     * Acquire new instance of non exist
     *
     * @param invoker framework invoker
     * @return framework instance
     */
    public static FWRest acquireInstance(FWInvoker invoker) {
        return acquireInstance(invoker, true);
    }

    /**
     * Acquire new instance of non exist
     *
     * @param invoker          framework plugin invoker
     * @param abortOnException whether to abort when an exception occurs;
     * @return framework instance
     */
    public static FWRest acquireInstance(FWInvoker invoker, boolean abortOnException) {
        if(instance.get() != null)
            return instance.get();

        return new FWRest(invoker, abortOnException);
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
        return FW.service(service);
    }

}
