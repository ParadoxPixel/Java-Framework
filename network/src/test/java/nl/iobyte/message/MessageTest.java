package nl.iobyte.message;

import nl.iobyte.framework.FW;
import nl.iobyte.framework.generic.serializer.SerializerService;
import nl.iobyte.framework.generic.serializer.objects.JsonSerializer;
import nl.iobyte.framework.network.message.MessageService;
import nl.iobyte.framework.network.message.objects.Message;
import nl.iobyte.framework.network.message.objects.MessagePayload;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class MessageTest {

    public void setupFramework() {
        FW instance = FW.acquireInstance(() -> null);
        instance.registerBulk(
                SerializerService.class,
                MessageService.class
        );

        instance.init();
        instance.start();
    }

    @Test
    public void adapter() {
        setupFramework();

        //Map type
        MessageService service = FW.service(MessageService.class);
        service.map("test-type", TestPayload.class);
        Assert.assertEquals(service.map("test-type"), TestPayload.class);
        Assert.assertEquals(service.map(TestPayload.class), "test-type");

        //Serialize
        Message message = new Message("my-sender-id", new TestPayload("John Doe"));
        byte[] bytes = FW.service(SerializerService.class).get(JsonSerializer.class).to(message);
        Assert.assertNotNull(bytes);
        Assert.assertNotEquals(0, bytes.length);

        //Assert type gets mapped
        Assert.assertTrue(new String(bytes).contains("\"payload\":{\"type\":\"test-type\",\"data\":{"));

        //Deserialize
        message = FW.service(SerializerService.class).get(JsonSerializer.class).from(bytes, Message.class);
        Assert.assertNotNull(message);
        Assert.assertNotNull(message.getPayload());

        TestPayload payload = message.getPayload(TestPayload.class);
        Assert.assertEquals("John Doe", payload.name);
    }

    @Test
    public void handler() {
        setupFramework();

        MessageService service = FW.service(MessageService.class);

        CountDownLatch latch = new CountDownLatch(1);
        service.on(TestPayload.class, msg -> latch.countDown());

        Message message = new Message("my-sender-id", new TestPayload("John Doe"));
        try {
            service.handleSync(message);
        } catch(Exception e) {
            e.printStackTrace();
        }

        Assert.assertEquals(0, latch.getCount());
    }

    public static class TestPayload extends MessagePayload {

        public String name;

        public TestPayload(String name) {
            this.name = name;
        }

    }

}
