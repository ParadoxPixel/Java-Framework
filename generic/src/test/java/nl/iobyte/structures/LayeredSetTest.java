package nl.iobyte.structures;

import nl.iobyte.framework.structures.layered.LayeredSet;
import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;
import java.util.Set;

public class LayeredSetTest {

    @Test
    public void testAdd() {
        LayeredSet<String> set = new LayeredSet<>();

        //Test add
        Assert.assertTrue(set.add("test0"));
        Assert.assertTrue(set.add(0, "test1"));
        Assert.assertTrue(set.add(1, "test2"));

        //Test duplication check
        Assert.assertFalse(set.add(1, "test1"));

        //Test max layer check
        try {
            set.add(3, "test3");
            Assert.fail("cannot add to layer two higher than current highest layer");
        } catch(Exception ignored) {
        }
    }

    @Test
    public void testRemove() {
        LayeredSet<String> set = new LayeredSet<>();
        set.add(0, "test1");
        set.add(1, "test2");
        set.add(2, "test3");

        //Test remove
        Assert.assertTrue(set.remove("test3"));
        Assert.assertFalse(set.remove(2, "test2"));

        //Test if empty layer gets cleared
        try {
            set.add(3, "test3");
            Assert.fail("empty layer does not get cleared from set");
        } catch(Exception ignored) {
        }
    }

    @Test
    public void testLayerOf() {
        LayeredSet<String> set = new LayeredSet<>();
        set.add(0, "test1");
        set.add(1, "test2");
        set.add(2, "test3");

        Assert.assertEquals(0, set.layerOf("test1"));
        Assert.assertEquals(1, set.layerOf("test2"));
        Assert.assertEquals(2, set.layerOf("test3"));
    }

    @Test
    public void testMove() {
        LayeredSet<String> set = new LayeredSet<>();
        set.add(0, "test1");
        set.add(1, "test2");
        set.add(2, "test3");

        Assert.assertTrue(set.move(1, "test3"));
        Assert.assertTrue(set.move(1, 2, "test2"));
        Assert.assertTrue(set.move(1, 0, "test3"));
    }

    @Test
    public void testIterator() {
        LayeredSet<String> set = new LayeredSet<>();
        set.add(0, "test1");
        set.add(1, "test2");
        set.add(2, "test3");

        Iterator<Set<String>> iterator = set.iterator();

        Assert.assertTrue(iterator.next().contains("test1"));
        Assert.assertTrue(iterator.next().contains("test2"));
        Assert.assertTrue(iterator.next().contains("test3"));
    }

}
