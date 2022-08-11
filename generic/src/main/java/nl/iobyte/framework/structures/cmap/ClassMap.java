package nl.iobyte.framework.structures.cmap;

import nl.iobyte.framework.generic.reflections.objects.ReflectedType;
import nl.iobyte.framework.structures.reflected.ReflectedMap;
import nl.iobyte.framework.structures.suppliers.IMapSupplier;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClassMap<T> implements Iterable<T> {

    private final Map<Class<? extends T>, T> mapping;

    public ClassMap() {
        this(ReflectedMap.getMapSupplier(ConcurrentHashMap.class));
    }

    public ClassMap(IMapSupplier supplier) {
        this.mapping = supplier.get();
    }

    /**
     * Get size of map
     *
     * @return size
     */
    public int size() {
        return mapping.size();
    }

    /**
     * Clear map
     */
    public void clear() {
        mapping.clear();
    }

    /**
     * Get map values
     *
     * @return collection of values
     */
    public Collection<T> values() {
        return mapping.values();
    }

    /**
     * Register type
     *
     * @param type of object
     * @param <R>  type
     */
    public <R extends T> void register(Class<R> type) {
        ReflectedType<R> reflectedType = ReflectedType.of(type);
        T obj;
        try {
            obj = reflectedType.getConstructor().newInstance();
        } catch(Exception e) {
            throw new IllegalArgumentException("type " + type.getName() + " does not have an empty constructor");
        }

        register(obj);
    }

    /**
     * Register object
     *
     * @param obj instance
     * @param <R> type
     */
    public <R extends T> void register(R obj) {
        if(obj == null)
            throw new NullPointerException("cannot register empty object");

        //noinspection unchecked
        mapping.put((Class<? extends T>) obj.getClass(), obj);
    }

    /**
     * Get instance belonging to type
     *
     * @param type to get
     * @param <R>  type
     * @return instance belonging to type
     */
    public <R extends T> R get(Class<R> type) {
        return get(type, false);
    }

    /**
     * Get with possible inheritance
     *
     * @param type    to get
     * @param inherit whether to inherit type
     * @param <R>     type
     * @return instance belonging to type
     */
    public <R extends T> R get(Class<R> type, boolean inherit) {
        T obj = mapping.get(type);
        if(obj != null || !inherit)
            return type.cast(obj);

        for(Map.Entry<Class<? extends T>, T> entry : mapping.entrySet())
            if(type.isAssignableFrom(entry.getKey()))
                return type.cast(entry.getValue());

        return null;
    }

    /**
     * Check if map contains type
     *
     * @param type of object
     * @return whether it is present
     */
    public boolean contains(Class<? extends T> type) {
        return mapping.containsKey(type);
    }

    /**
     * Check if map contains object
     *
     * @param obj instance
     * @return whether it is present
     */
    public boolean contains(T obj) {
        return mapping.containsValue(obj);
    }

    /**
     * Remove entry by type
     *
     * @param type to remove
     * @param <R>  type
     * @return instance belonging to type
     */
    public <R extends T> R remove(Class<R> type) {
        return type.cast(mapping.remove(type));
    }

    /**
     * Remove entry by object
     *
     * @param obj to remove
     * @param <R> type
     * @return object
     */
    public <R extends T> R remove(R obj) {
        //noinspection unchecked
        return remove((Class<R>) obj.getClass());
    }

    @Override
    public Iterator<T> iterator() {
        return mapping.values().iterator();
    }

}
