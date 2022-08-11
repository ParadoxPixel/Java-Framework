package nl.iobyte.framework.structures.omap;

import nl.iobyte.framework.structures.omap.interfaces.IObject;
import nl.iobyte.framework.structures.reflected.ReflectedMap;
import nl.iobyte.framework.structures.suppliers.IMapSupplier;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ObjectMap<T, R extends IObject<T>> implements Map<T, R> {

    private final Map<T, R> objects;

    public ObjectMap() {
        this(ReflectedMap.getMapSupplier(ConcurrentHashMap.class));
    }

    public ObjectMap(IMapSupplier supplier) {
        this.objects = supplier.get();
    }

    @Override
    public int size() {
        return objects.size();
    }

    @Override
    public boolean isEmpty() {
        return objects.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return objects.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return objects.containsValue(value);
    }

    @Override
    public R get(Object key) {
        return objects.get(key);
    }

    /**
     * Get optional of value
     *
     * @param key corresponding to value
     * @return optional value belonging to key
     */
    public Optional<R> getOptional(Object key) {
        return Optional.ofNullable(get(key));
    }

    /**
     * Add value to map
     *
     * @param value to add
     * @return old value for identity
     */
    public R put(R value) {
        return put(value.getId(), value);
    }

    @Override
    public R put(T key, R value) {
        return objects.put(key, value);
    }

    @Override
    public R remove(Object key) {
        return objects.remove(key);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void putAll(Map<? extends T, ? extends R> m) {
        objects.putAll(m);
    }

    @Override
    public void clear() {
        objects.clear();
    }

    @Override
    public Set<T> keySet() {
        return objects.keySet();
    }

    @Override
    public Collection<R> values() {
        return objects.values();
    }

    @Override
    public Set<Entry<T, R>> entrySet() {
        return objects.entrySet();
    }

}
