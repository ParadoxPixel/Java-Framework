package nl.iobyte.serializer;

import nl.iobyte.framework.generic.serializer.objects.JavaObjectSerializer;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class JavaObjectSerializerTest {

    @Test
    public void test() {
        JavaObjectSerializer serializer = new JavaObjectSerializer();

        Object data = Map.of("test", Map.of("nice", true));
        byte[] bytes = serializer.to(data);
        Assert.assertNotNull(bytes);

        //noinspection unchecked
        Map<String, Object> map = (Map<String, Object>) serializer.from(bytes, Map.class);

        Assert.assertTrue(map.containsKey("test"));
        //noinspection unchecked
        Assert.assertTrue(((Map<String, Object>) map.get("test")).containsKey("nice"));
    }

}
