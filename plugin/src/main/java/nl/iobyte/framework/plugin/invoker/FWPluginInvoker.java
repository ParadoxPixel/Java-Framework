package nl.iobyte.framework.plugin.invoker;

import nl.iobyte.framework.generic.invoker.FWInvoker;

public interface FWPluginInvoker extends FWInvoker {

    /**
     * Get platform of invoker
     *
     * @return Platform
     */
    Platform getPlatform();

}
