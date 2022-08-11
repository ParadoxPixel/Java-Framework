package nl.iobyte.structures;

import nl.iobyte.framework.structures.cmap.ClassMap;
import org.junit.Assert;
import org.junit.Test;

public class ClassMapTest {

    @Test
    public void testRegister() {
        ClassMap<TestType> map = new ClassMap<>();
        map.register(TestTypeA.class);
        map.register(TestTypeB.class);
        map.register(new TestTypeC());
    }

    @Test
    public void testGet() {
        ClassMap<TestType> map = new ClassMap<>();
        map.register(TestTypeA.class);
        map.register(TestTypeB.class);
        map.register(new TestTypeC());

        Assert.assertNotNull(map.get(TestTypeA.class));
        Assert.assertNotNull(map.get(TestTypeB.class));
        Assert.assertNotNull(map.get(TestTypeC.class));
    }

    @Test
    public void testContains() {
        ClassMap<TestType> map = new ClassMap<>();
        map.register(TestTypeA.class);
        map.register(TestTypeB.class);
        map.register(new TestTypeC());

        Assert.assertTrue(map.contains(TestTypeA.class));
        Assert.assertTrue(map.contains(map.get(TestTypeB.class)));
        Assert.assertTrue(map.contains(TestTypeC.class));
    }

    @Test
    public void testIterator() {
        ClassMap<TestType> map = new ClassMap<>();
        map.register(TestTypeA.class);
        map.register(TestTypeB.class);
        map.register(new TestTypeC());

        int i = 0;
        for(TestType type : map)
            i++;

        Assert.assertEquals(3, i);
    }

    private interface TestType {}

    private static class TestTypeA implements TestType {}

    private static class TestTypeB implements TestType {}

    private static class TestTypeC implements TestType {}

}
