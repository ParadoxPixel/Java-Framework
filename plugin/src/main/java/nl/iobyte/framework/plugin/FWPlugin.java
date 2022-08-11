package nl.iobyte.framework.plugin;

import nl.iobyte.framework.FW;
import nl.iobyte.framework.generic.service.interfaces.Service;
import nl.iobyte.framework.plugin.invoker.FWPluginInvoker;
import nl.iobyte.framework.plugin.player.PlayerService;

import java.util.concurrent.atomic.AtomicReference;

public class FWPlugin extends FW {

    private static final AtomicReference<FWPlugin> instance = new AtomicReference<>(null);
    private final FWPluginInvoker invoker;

    protected FWPlugin(FWPluginInvoker invoker, boolean abortOnException) {
        super(invoker, abortOnException);
        instance.set(this);
        this.invoker = invoker;

        registerBulk(
                PlayerService.class
        );
    }

    /**
     * Get instance of Framework
     *
     * @return framework
     */
    public static FWPlugin getInstance() {
        return instance.get();
    }

    /**
     * Get plugin invoker of type
     *
     * @param type of plugin invoker
     * @param <T>  type of plugin invokers
     * @return plugin invoker instance
     */
    public static <T extends FWPluginInvoker> T getPluginInvoker(Class<T> type) {
        return getInvoker(type);
    }

    /**
     * Acquire new instance of non exist
     *
     * @param invoker framework invoker
     * @return framework instance
     */
    public static FWPlugin acquireInstance(FWPluginInvoker invoker) {
        return acquireInstance(invoker, true);
    }

    /**
     * Acquire new instance of non exist
     *
     * @param invoker          framework plugin invoker
     * @param abortOnException whether to abort when an exception occurs;
     * @return framework instance
     */
    public static FWPlugin acquireInstance(FWPluginInvoker invoker, boolean abortOnException) {
        if(instance.get() != null)
            return instance.get();

        return new FWPlugin(invoker, abortOnException);
    }

    /**
     * Get framework invoker
     *
     * @return invoker instance
     */
    public static FWPluginInvoker getInvoker() {
        return instance.get().invoker;
    }

    /**
     * Get service by type
     */
    public static <T extends Service> T service(Class<T> service) {
        return FW.service(service);
    }

}
