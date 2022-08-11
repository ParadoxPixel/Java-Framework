package nl.iobyte.framework.structures.dtree;

import nl.iobyte.framework.structures.suppliers.IListSupplier;

import java.util.ArrayList;
import java.util.List;

public class DTreeNode<T> {

    private final T id;
    private final List<DTreeNode<T>> parents;
    private final List<DTreeNode<T>> children;

    public DTreeNode(T id) {
        this(id, ArrayList::new);
    }

    public DTreeNode(T id, IListSupplier supplier) {
        this.id = id;
        this.parents = supplier.get();
        this.children = supplier.get();
    }

    /**
     * Get identity of dependency tree node
     *
     * @return identity
     */
    public T getId() {
        return id;
    }

    /**
     * Get parents belonging to dependency tree node
     *
     * @return list of dependency tree nodes current node belongs to
     */
    public List<DTreeNode<T>> getParents() {
        return parents;
    }

    public void addParent(DTreeNode<T> parent) {
        parents.add(parent);
    }

    /**
     * Get children belonging to dependency tree node
     *
     * @return list of dependency tree nodes belonging to current node
     */
    public List<DTreeNode<T>> getChildren() {
        return children;
    }

    /**
     * Add child to dependency tree node
     *
     * @param child dependency tree node
     */
    public void addChild(DTreeNode<T> child) {
        children.add(child);
        child.addParent(this);
    }

    /**
     * Check if this node is already a nested child of node
     *
     * @param child dependency tree node
     * @return whether the child was added or not
     */
    public boolean addCheckedChild(DTreeNode<T> child) {
        if(child.isParentOf(id))
            return false;

        addChild(child);
        return true;
    }

    /**
     * Check if there's a circular dependency with id
     *
     * @param id of node
     * @return whether node has a circular dependency with id
     */
    public boolean isCircular(T id) {
        if(isParentOf(id))
            return true;

        return isChildOf(id);
    }

    /**
     * Check if node is parent of id
     *
     * @param id of node
     * @return whether node is parent of id
     */
    public boolean isParentOf(T id) {
        if(this.id.equals(id))
            return true;

        for(DTreeNode<T> child : children)
            if(child.isParentOf(id))
                return true;

        return false;
    }

    /**
     * Check if node is child of id
     *
     * @param id of node
     * @return whether node is child of id
     */
    public boolean isChildOf(T id) {
        if(this.id.equals(id))
            return true;

        for(DTreeNode<T> parent : parents)
            if(parent.isChildOf(id))
                return true;

        return false;
    }

}
