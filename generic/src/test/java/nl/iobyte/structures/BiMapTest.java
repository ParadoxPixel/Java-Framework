package nl.iobyte.structures;

import nl.iobyte.framework.structures.bimap.BiMap;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class BiMapTest {

    @Test
    public void testPut() {
        BiMap<String, Integer, Boolean> map = new BiMap<>();
        Assert.assertNull(map.putPair("test", 1, false));
        Assert.assertNull(map.putPair("test", 2, true));

        Assert.assertNotNull(map.putPair("test", 1, true));
        Assert.assertNotNull(map.putPair("test", 2, false));
    }

    @Test
    public void testGet() {
        BiMap<String, Integer, Boolean> map = new BiMap<>();
        map.putPair("test", 1, false);
        map.putPair("test", 2, true);

        Assert.assertFalse(map.getPair("test", 1));
        Assert.assertTrue(map.getPair("test", 2));
        Assert.assertNull(map.getPair("nice", 2));
    }

    @Test
    public void testFlatMap() {
        BiMap<String, Integer, Boolean> map = new BiMap<>();
        map.putPair("test", 1, false);
        map.putPair("test", 2, true);

        Map<String, Map<Integer, Boolean>> flatMap = map.toFlatMap();
        Assert.assertEquals(1, flatMap.size());
        Assert.assertNotNull(flatMap.get("test"));

        Map<Integer, Boolean> nestedMap = flatMap.get("test");
        Assert.assertEquals(2, nestedMap.size());
        Assert.assertNotNull(nestedMap.get(1));
        Assert.assertNotNull(nestedMap.get(2));
    }

}
