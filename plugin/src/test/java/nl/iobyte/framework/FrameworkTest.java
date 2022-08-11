package nl.iobyte.framework;

import nl.iobyte.framework.generic.event.EventService;
import nl.iobyte.framework.generic.invoker.FWScheduler;
import nl.iobyte.framework.plugin.FWPlugin;
import nl.iobyte.framework.plugin.invoker.FWPluginInvoker;
import nl.iobyte.framework.plugin.invoker.Platform;
import nl.iobyte.framework.plugin.player.PlayerService;
import org.junit.Assert;
import org.junit.Test;

public class FrameworkTest {

    @Test
    public void test() {
        FWPlugin instance = FWPlugin.acquireInstance(new FWPluginInvoker() {
            @Override
            public Platform getPlatform() {
                return Platform.STANDALONE;
            }

            @Override
            public FWScheduler getScheduler() {
                return null;
            }
        });

        instance.init();
        instance.start();

        Assert.assertNotNull(instance.get(PlayerService.class));
        Assert.assertNotNull(instance.get(EventService.class));

        Assert.assertNotNull(FW.service(PlayerService.class));
        Assert.assertNotNull(FW.service(EventService.class));
    }

}
