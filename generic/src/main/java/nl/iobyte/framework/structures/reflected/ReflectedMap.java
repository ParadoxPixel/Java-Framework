package nl.iobyte.framework.structures.reflected;

import nl.iobyte.framework.generic.reflections.objects.ReflectedType;
import nl.iobyte.framework.structures.suppliers.IMapSupplier;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

@SuppressWarnings("NullableProblems")
public class ReflectedMap<K, V> implements Map<K, V> {

    private final Map<K, V> internalMap;

    public ReflectedMap(@SuppressWarnings("rawtypes") Class<? extends Map> type) {
        try {
            //noinspection unchecked
            internalMap = (Map<K, V>) ReflectedType.of(type).getConstructor().newInstance();
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int size() {
        return internalMap.size();
    }

    @Override
    public boolean isEmpty() {
        return internalMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return internalMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return internalMap.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return internalMap.get(key);
    }

    @Override
    public V put(K key, V value) {
        return internalMap.put(key, value);
    }

    @Override
    public V remove(Object key) {
        return internalMap.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        internalMap.putAll(m);
    }

    @Override
    public void clear() {
        internalMap.clear();
    }

    @Override
    public Set<K> keySet() {
        return internalMap.keySet();
    }

    @Override
    public Collection<V> values() {
        return internalMap.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return internalMap.entrySet();
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        return internalMap.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        return internalMap.getOrDefault(key, defaultValue);
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        internalMap.forEach(action);
    }

    /**
     * Get map supplier of reflected map
     *
     * @param type of map
     * @return map supplier instance
     */
    public static IMapSupplier getMapSupplier(@SuppressWarnings("rawtypes") Class<? extends Map> type) {
        return new IMapSupplier() {
            @Override
            public <K, V> Map<K, V> get() {
                return new ReflectedMap<>(type);
            }
        };
    }

}
