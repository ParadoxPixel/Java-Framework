package nl.iobyte.structures;

import nl.iobyte.framework.structures.pmap.PairMap;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

public class PairMapTest {

    @Test
    public void testInitialisation() {
        PairMap<String, UUID> pairMap;

        try {
            pairMap = PairMap.of(String.class, UUID.class);
        } catch(Exception e) {
            Assert.fail(e.getMessage());
            return;
        }

        UUID uuid = UUID.randomUUID();
        pairMap.set("SomeThing", uuid);

        Assert.assertNotNull(pairMap.get("SomeThing"));
        Assert.assertNotNull(pairMap.get(uuid));

        Assert.assertNotNull(pairMap.getLeft("SomeThing"));
        Assert.assertNotNull(pairMap.getRight(uuid));

        Assert.assertEquals(1, pairMap.size());
    }

    @Test
    public void testGetSet() {
        PairMap<String, UUID> pairMap;

        try {
            pairMap = PairMap.of(String.class, UUID.class);
        } catch(Exception e) {
            Assert.fail(e.getMessage());
            return;
        }

        UUID uuid = UUID.randomUUID();
        pairMap.set("SomeThing", uuid);

        Assert.assertNotNull(pairMap.get("SomeThing"));
        Assert.assertNotNull(pairMap.get(uuid));

        Assert.assertNotNull(pairMap.getLeft("SomeThing"));
        Assert.assertNotNull(pairMap.getRight(uuid));
    }

}
