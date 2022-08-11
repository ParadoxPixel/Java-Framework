package nl.iobyte.snowflake;

import nl.iobyte.framework.FW;
import nl.iobyte.framework.data.snowflake.SnowflakeService;
import org.junit.Assert;
import org.junit.Test;

public class SnowflakeTest {

    /**
     * Setup framework needs to test
     */
    private void setupFramework() {
        FW framework = FW.acquireInstance(() -> null, false);
        framework.registerBulk(
                SnowflakeService.class
        );

        framework.init();
        framework.start();
    }

    @Test
    public void test() {
        setupFramework();

        Assert.assertNotEquals(0, FW.service(SnowflakeService.class).nextId());
    }

}
