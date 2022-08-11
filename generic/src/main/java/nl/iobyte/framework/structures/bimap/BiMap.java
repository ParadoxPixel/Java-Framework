package nl.iobyte.framework.structures.bimap;

import nl.iobyte.framework.structures.reflected.ReflectedMap;
import nl.iobyte.framework.structures.suppliers.IMapSupplier;

import java.util.*;

public class BiMap<K1, K2, V> implements Map<BiKey<K1, K2>, V> {

    private final Map<BiKey<K1, K2>, V> internalMap;

    public BiMap() {
        this(ReflectedMap.getMapSupplier(HashMap.class));
    }

    public BiMap(IMapSupplier supplier) {
        this.internalMap = supplier.get();
    }

    @Override
    public int size() {
        return internalMap.size();
    }

    @Override
    public boolean isEmpty() {
        return internalMap.isEmpty();
    }

    /**
     * Check if key pair is present
     *
     * @param key1 first key
     * @param key2 second key
     * @return whether pair is present
     */
    public boolean containsPair(K1 key1, K2 key2) {
        return containsKey(new BiKey<>(key1, key2));
    }

    @Override
    public boolean containsKey(Object key) {
        return internalMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return internalMap.containsValue(value);
    }

    /**
     * Get value belonging to key pair
     *
     * @param key1 first key
     * @param key2 second key
     * @return value
     */
    public V getPair(K1 key1, K2 key2) {
        return get(new BiKey<>(key1, key2));
    }

    /**
     * Get optional value belonging to key pair
     *
     * @param key1 first key
     * @param key2 second key
     * @return optional value
     */
    public Optional<V> getOptionalPair(K1 key1, K2 key2) {
        return Optional.ofNullable(getPair(key1, key2));
    }

    @Override
    public V get(Object key) {
        return internalMap.get(key);
    }

    /**
     * Set value at key pair
     *
     * @param key1  first key
     * @param key2  second key
     * @param value value
     * @return previous value
     */
    public V putPair(K1 key1, K2 key2, V value) {
        return put(new BiKey<>(key1, key2), value);
    }

    @Override
    public V put(BiKey<K1, K2> key, V value) {
        return internalMap.put(key, value);
    }

    /**
     * Remove key pair from map
     *
     * @param key1 first key
     * @param key2 second key
     * @return value
     */
    public V removePair(K1 key1, K2 key2) {
        return remove(new BiKey<>(key1, key2));
    }

    @Override
    public V remove(Object key) {
        return internalMap.remove(key);
    }

    @Override
    public void putAll(Map<? extends BiKey<K1, K2>, ? extends V> m) {
        internalMap.putAll(m);
    }

    @Override
    public void clear() {
        internalMap.clear();
    }

    @Override
    public Set<BiKey<K1, K2>> keySet() {
        return internalMap.keySet();
    }

    @Override
    public Collection<V> values() {
        return internalMap.values();
    }

    @Override
    public Set<Entry<BiKey<K1, K2>, V>> entrySet() {
        return internalMap.entrySet();
    }

    /**
     * Get flat map version from bi-map
     *
     * @return map
     */
    public Map<K1, Map<K2, V>> toFlatMap() {
        return toFlatMap(ReflectedMap.getMapSupplier(HashMap.class));
    }

    /**
     * Get flat map version from bi-map
     *
     * @param supplier of map instances
     * @return map
     */
    public Map<K1, Map<K2, V>> toFlatMap(IMapSupplier supplier) {
        Map<K1, Map<K2, V>> map = supplier.get();
        for(Map.Entry<BiKey<K1, K2>, V> entry : internalMap.entrySet()) {
            map.computeIfAbsent(
                    entry.getKey().key1(),
                    key -> supplier.get()
            ).put(
                    entry.getKey().key2(),
                    entry.getValue()
            );
        }

        return map;
    }

}
