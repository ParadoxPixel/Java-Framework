package nl.iobyte.framework.structures.doublemap;

import nl.iobyte.framework.structures.reflected.ReflectedMap;
import nl.iobyte.framework.structures.suppliers.IMapSupplier;

import java.util.*;

public class DoubleMap<K1, K2, V> implements Map<K1, V> {

    private final Map<K1, V> leftMap;
    private final Map<K2, V> rightMap;

    public DoubleMap() {
        this(ReflectedMap.getMapSupplier(HashMap.class));
    }

    public DoubleMap(IMapSupplier supplier) {
        leftMap = supplier.get();
        rightMap = supplier.get();
    }

    @Override
    public int size() {
        return leftMap.size();
    }

    @Override
    public boolean isEmpty() {
        return leftMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        if(leftMap.containsKey(key))
            return true;

        return rightMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return leftMap.containsValue(value);
    }

    @Override
    public V get(Object key) {
        V value = leftMap.get(key);
        if(value == null)
            rightMap.get(key);

        return value;
    }

    public Optional<V> getLeft(K1 key) {
        return Optional.ofNullable(leftMap.get(key));
    }

    public Optional<V> getRight(K2 key) {
        return Optional.ofNullable(rightMap.get(key));
    }

    /**
     * Add value to map
     *
     * @param key1  first key
     * @param key2  second key
     * @param value value
     * @return old value
     */
    public V put(K1 key1, K2 key2, V value) {
        rightMap.put(key2, value);
        return leftMap.put(key1, value);
    }

    @Override
    public V put(K1 key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(Object key) {
        throw new UnsupportedOperationException();
    }

    /**
     * Remove entry with both keys
     *
     * @param key1 first key
     * @param key2 second key
     */
    public V removeEntry(K1 key1, K2 key2) {
        if(!leftMap.containsKey(key1) || !rightMap.containsKey(key2))
            return null;
        
        rightMap.remove(key2);
        return leftMap.remove(key1);
    }

    @Override
    public void putAll(Map<? extends K1, ? extends V> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        leftMap.clear();
        rightMap.clear();
    }

    @Override
    public Set<K1> keySet() {
        return leftMap.keySet();
    }

    @Override
    public Collection<V> values() {
        return leftMap.values();
    }

    @Override
    public Set<Entry<K1, V>> entrySet() {
        return leftMap.entrySet();
    }

}
