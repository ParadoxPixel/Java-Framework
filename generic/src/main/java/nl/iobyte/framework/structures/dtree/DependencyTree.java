package nl.iobyte.framework.structures.dtree;

import nl.iobyte.framework.structures.layered.LayeredSet;
import nl.iobyte.framework.structures.reflected.ReflectedMap;
import nl.iobyte.framework.structures.suppliers.IListSupplier;
import nl.iobyte.framework.structures.suppliers.IMapSupplier;
import nl.iobyte.framework.structures.suppliers.ISetSupplier;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class DependencyTree<T, R> {

    private final Function<T, R> resolver;
    private final LayeredSet<T> set;
    private final Map<T, DTreeNode<T>> nodes;
    private final IListSupplier listSupplier;

    public DependencyTree(Function<T, R> resolver) {
        this(
                resolver,
                ReflectedMap.getMapSupplier(HashMap.class),
                HashSet::new,
                ArrayList::new
        );
    }

    public DependencyTree(Function<T, R> resolver,
                          IMapSupplier mapSupplier,
                          ISetSupplier setSupplier,
                          IListSupplier listSupplier) {
        this.resolver = resolver;
        this.set = new LayeredSet<>(mapSupplier, setSupplier);
        this.nodes = mapSupplier.get();
        this.listSupplier = listSupplier;
    }

    public Collection<DTreeNode<T>> getNodes() {
        return nodes.values();
    }

    /**
     * Add node to dependency tree
     *
     * @param id of node
     */
    public void add(T id) {
        int before = nodes.size();
        nodes.computeIfAbsent(id, DTreeNode::new);

        //Update if change was made
        if(nodes.size() > before)
            update();
    }

    /**
     * Add dependency to node
     */
    public boolean addDependency(T id, T dependencyId) {
        DTreeNode<T> node = nodes.computeIfAbsent(id, key -> new DTreeNode<>(key, listSupplier));
        DTreeNode<T> dependency = nodes.computeIfAbsent(dependencyId, key -> new DTreeNode<>(key, listSupplier));
        boolean b = node.addCheckedChild(dependency);

        //Update if change was made
        if(b)
            update();

        return b;
    }

    /**
     * Update layered set
     */
    private void update() {
        set.clear();
        for(DTreeNode<T> node : nodes.values())
            if(node.getParents().isEmpty())
                addNode(0, node);
    }

    /**
     * Add node to layered set else move to correct layer
     *
     * @param layer index
     * @param node  to add
     */
    private void addNode(int layer, DTreeNode<T> node) {
        set.move(layer, node.getId());
        for(DTreeNode<T> child : node.getChildren()) {
            int childLayer = set.layerOf(child.getId());
            if(childLayer > layer)
                continue;

            addNode(layer + 1, child);
        }
    }

    /**
     * Traverse tree top down
     *
     * @param c consumes resolved entry
     */
    public void traverse(Consumer<R> c) {
        set.list().forEach(
                layer -> layer.stream()
                              .map(resolver)
                              .forEach(c)
        );
    }

    /**
     * Traverse tree bottom up
     *
     * @param c consumes resolved entry
     */
    public void traverseReverse(Consumer<R> c) {
        List<Set<T>> list = set.list();
        Collections.reverse(list);
        list.forEach(
                layer -> layer.stream()
                              .map(resolver)
                              .forEach(c)
        );
    }

}
