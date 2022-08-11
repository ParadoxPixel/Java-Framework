package nl.iobyte.config;

import nl.iobyte.framework.generic.config.objects.BaseConfig;
import nl.iobyte.framework.generic.serializer.objects.JsonSerializer;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public class BaseConfigTest {

    @Test
    public void testLoad() {
        BaseConfig config = new BaseConfig(
                "test",
                new JsonSerializer(),
                () -> new ByteArrayInputStream("{\"path\":{\"test\":true,\"nice\":5}}".getBytes(StandardCharsets.UTF_8))
        );

        try {
            config.load();
        } catch(Exception e) {
            Assert.fail(e.getMessage());
        }

        config = new BaseConfig(
                "test",
                new JsonSerializer(),
                () -> null
        );

        try {
            config.load();
            Assert.fail("stream should either be null or unavailable");
        } catch(Exception ignored) {
        }
    }

    @Test
    public void testSave() {
        BaseConfig config = new BaseConfig(
                "test",
                new JsonSerializer(),
                () -> new ByteArrayInputStream("{\"path\":{\"test\":true,\"nice\":5}}".getBytes(StandardCharsets.UTF_8))
        );

        try(ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            config.save(stream);

            Assert.assertNotEquals(0, stream.toByteArray().length);
        } catch(Exception e) {
            Assert.fail(e.getMessage());
        }
    }

}