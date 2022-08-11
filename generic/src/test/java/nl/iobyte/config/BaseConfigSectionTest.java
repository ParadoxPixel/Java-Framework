package nl.iobyte.config;

import nl.iobyte.framework.generic.config.objects.BaseConfigSection;
import org.junit.Assert;
import org.junit.Test;

import java.util.TreeMap;

public class BaseConfigSectionTest {

    @Test
    public void testSetContents() {
        BaseConfigSection section = new BaseConfigSection();
        try {
            section.setContents(getDummyMap());
        } catch(Exception e) {
            Assert.fail(e.getMessage());
        }

        try {
            TreeMap<String, Object> map = getDummyMap();
            map.put("nice", new TreeMap<>());
            section.setContents(map);
        } catch(Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testGet() {
        BaseConfigSection section = new BaseConfigSection();
        section.setContents(getDummyMap());

        Assert.assertNotNull(section.get("test"));
        Assert.assertNotNull(section.getSection("test"));
        Assert.assertEquals(BaseConfigSection.class, section.get("test").getClass());

        //Test type conversion
        Assert.assertEquals(5d, section.getDouble("test.nice"), 0);
        Assert.assertEquals(-10f, section.getFloat("test.yep"), 0);
        Assert.assertFalse(section.getBoolean("test.yep"));

        Assert.assertEquals("test", section.getString("test.obj.fieldA"));
        Assert.assertEquals(5, section.getInteger("test.obj.fieldB"));
    }

    @Test
    public void testSet() {
        BaseConfigSection section = new BaseConfigSection();
        section.setContents(new TreeMap<>());

        section.set("path.to.value", 5f);
        Assert.assertEquals(5D, section.getDouble("path.to.value"), 0);

        section.set("path.test", true);
        Assert.assertTrue(section.getBoolean("path.test"));

        section.set("path.test", false);
        Assert.assertFalse(section.getBoolean("path.test"));

        section.set("path.obj", new TestType());
        Assert.assertEquals("test", section.getString("path.obj.fieldA"));
        Assert.assertEquals(5, section.getInteger("path.obj.fieldB"));

    }

    /**
     * Get dummy map used for tests
     *
     * @return tree map
     */
    public TreeMap<String, Object> getDummyMap() {
        TreeMap<String, Object> map = new TreeMap<>();

        TreeMap<String, Object> nestedMap = new TreeMap<>();
        nestedMap.put("nice", 5);
        nestedMap.put("yep", -10D);
        nestedMap.put("obj", new TestType());
        map.put("test", nestedMap);

        return map;
    }

    private static class TestType {

        public String fieldA = "test";
        public int fieldB = 5;

    }

}
