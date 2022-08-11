package nl.iobyte.framework.structures.layered;

import nl.iobyte.framework.structures.reflected.ReflectedMap;
import nl.iobyte.framework.structures.suppliers.IMapSupplier;
import nl.iobyte.framework.structures.suppliers.ISetSupplier;

import java.util.*;

public class LayeredSet<T> implements Iterable<Set<T>> {

    private final Map<Integer, Set<T>> layers;
    private final ISetSupplier setSupplier;

    public LayeredSet() {
        this(
                ReflectedMap.getMapSupplier(HashMap.class),
                HashSet::new
        );
    }

    public LayeredSet(IMapSupplier mapSupplier, ISetSupplier setSupplier) {
        this.layers = mapSupplier.get();
        this.setSupplier = setSupplier;
    }

    /**
     * Get amount of items in set
     *
     * @return amount
     */
    public int size() {
        return layers.values()
                     .stream()
                     .map(Set::size)
                     .reduce(Integer::sum)
                     .orElse(0);
    }

    /**
     * Get amount of layers in set
     *
     * @return amount
     */
    public int layersSize() {
        return layers.size();
    }

    /**
     * Clear set
     */
    public void clear() {
        layers.clear();
    }

    /**
     * Add object to first layer
     *
     * @param obj of type
     */
    public boolean add(T obj) {
        return add(0, obj);
    }

    /**
     * Add object to n-th layer
     *
     * @param layer number
     * @param obj   of type
     */
    public boolean add(int layer, T obj) {
        if(layer < 0)
            throw new IllegalArgumentException("layer cannot be lower than 0");

        if(layer > layers.size())
            throw new IllegalArgumentException("layer cannot be more than one higher than current highest layer");

        if(obj == null)
            throw new NullPointerException("object cannot be null");

        if(layerOf(obj) != -1)
            return false;

        return layers.computeIfAbsent(layer, key -> setSupplier.get()).add(obj);
    }

    /**
     * Remove object from set
     *
     * @param obj to remove
     */
    public boolean remove(T obj) {
        return remove(layerOf(obj), obj);
    }

    /**
     * Remove object from layer
     *
     * @param layer index
     * @param obj   to remove
     */
    public boolean remove(int layer, T obj) {
        if(layer < 0 || layer >= layers.size())
            return false;

        Set<T> set = layers.get(layer);
        if(!set.remove(obj))
            return false;

        if(!set.isEmpty())
            return true;

        layers.remove(layer);
        if(layersSize() != layer)
            for(int i = layer; i < layersSize() - 1; i++)
                layers.put(i, layers.get(i + 1));

        return true;
    }

    /**
     * Move object to layer
     *
     * @param layer index
     * @param obj   to move
     */
    public boolean move(int layer, T obj) {
        int i = layerOf(obj);
        if(i == -1)
            return add(layer, obj);

        return move(i, layer, obj);
    }

    /**
     * Move object from layer to layer
     *
     * @param from layer index
     * @param to   layer index
     * @param obj  to move
     */
    public boolean move(int from, int to, T obj) {
        if(from == to)
            return true;

        if(!remove(from, obj))
            return false;

        return add(to, obj);
    }

    /**
     * Get layer
     *
     * @param layer number
     * @return set of type
     */
    public Set<T> get(int layer) {
        if(layer < 0 || layer >= layers.size())
            throw new IllegalArgumentException("layer " + layer + " does not exist");

        return layers.get(layer);
    }

    /**
     * Get layer object is in
     *
     * @param obj to look for
     * @return index of layer
     */
    public int layerOf(T obj) {
        for(Map.Entry<Integer, Set<T>> entry : layers.entrySet())
            if(entry.getValue().contains(obj))
                return entry.getKey();

        return -1;
    }

    /**
     * Get list of sets in ascending order
     *
     * @return list of sets
     */
    public List<Set<T>> list() {
        List<Set<T>> list = new ArrayList<>();
        for(int i = 0; i < layersSize(); i++)
            list.add(layers.get(i));

        return list;
    }

    @Override
    public Iterator<Set<T>> iterator() {
        return list().iterator();
    }

}
