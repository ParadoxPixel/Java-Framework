package nl.iobyte.structures;

import nl.iobyte.framework.structures.dtree.DependencyTree;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class DependencyTreeTest {

    @Test
    public void test() {
        DependencyTree<String, String> tree = new DependencyTree<>(str -> str);

        //Add nodes
        tree.add("test1");
        tree.add("test2");
        tree.add("test3");
        tree.add("test4");
        tree.add("test5");

        //Set dependencies
        Assert.assertTrue(tree.addDependency("test1", "test2"));
        Assert.assertTrue(tree.addDependency("test2", "test3"));
        Assert.assertTrue(tree.addDependency("test3", "test4"));
        Assert.assertTrue(tree.addDependency("test3", "test5"));
        Assert.assertTrue(tree.addDependency("test1", "test4"));
        Assert.assertTrue(tree.addDependency("test4", "test5"));

        //Test circular dependency
        Assert.assertFalse(tree.addDependency("test4", "test1"));
        Assert.assertFalse(tree.addDependency("test5", "test1"));

        //Test top down
        List<String> sequence = new ArrayList<>();
        tree.traverse(sequence::add);
        Assert.assertEquals(List.of("test1", "test2", "test3", "test4", "test5"), sequence);

        //Test bottom up
        sequence = new ArrayList<>();
        tree.traverseReverse(sequence::add);
        Assert.assertEquals(List.of("test5", "test4", "test3", "test2", "test1"), sequence);
    }

}
