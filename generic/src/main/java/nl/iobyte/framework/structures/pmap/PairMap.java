package nl.iobyte.framework.structures.pmap;

import nl.iobyte.framework.structures.reflected.ReflectedMap;
import nl.iobyte.framework.structures.suppliers.IMapSupplier;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class PairMap<T, R> {

    private final Class<T> typeA;
    private final Class<R> typeB;
    private final Map<T, R> leftMapping;
    private final Map<R, T> rightMapping;

    public PairMap(Class<T> typeA, Class<R> typeB) {
        this(typeA, typeB, ReflectedMap.getMapSupplier(ConcurrentHashMap.class));
    }

    public PairMap(Class<T> typeA, Class<R> typeB, IMapSupplier supplier) {
        this.typeA = typeA;
        this.typeB = typeB;

        this.leftMapping = supplier.get();
        this.rightMapping = supplier.get();
    }

    public int size() {
        return leftMapping.size();
    }

    public boolean isEmpty() {
        return leftMapping.isEmpty();
    }

    /**
     * Set pair in map
     *
     * @param p1 typeA
     * @param p2 typeB
     */
    public void set(T p1, R p2) {
        leftMapping.put(p1, p2);
        rightMapping.put(p2, p1);
    }

    /**
     * Check if map contains object
     *
     * @param obj to look for
     * @return whether map contains object
     */
    public boolean contains(Object obj) {
        if(containsLeft(obj))
            return true;

        return containsRight(obj);
    }

    /**
     * Check for object in left mapping
     *
     * @param obj to look for
     * @return whether left mapping contains object
     */
    public boolean containsLeft(Object obj) {
        if(!typeA.isInstance(obj))
            return false;

        return leftMapping.containsKey(typeA.cast(obj));
    }

    /**
     * Check for object in right mapping
     *
     * @param obj to look for
     * @return whether right mapping contains object
     */
    public boolean containsRight(Object obj) {
        if(!typeB.isInstance(obj))
            return false;

        return rightMapping.containsKey(typeB.cast(obj));
    }

    /**
     * Get other object from pair
     *
     * @param obj to get pair from
     * @return object
     */
    public Object get(Object obj) {
        Object value = null;
        if(typeA.isInstance(obj))
            value = getLeft(typeA.cast(obj));

        if(value != null)
            return value;

        if(!typeB.isInstance(obj))
            return null;

        return getRight(typeB.cast(obj));
    }

    /**
     * Get object by object in left mapping
     *
     * @param obj to get object for
     * @return object
     */
    public R getLeft(T obj) {
        return leftMapping.get(obj);
    }

    /**
     * Get optional object by object in left mapping
     *
     * @param obj to get object for
     * @return optional object
     */
    public Optional<R> getOptionalLeft(T obj) {
        return Optional.ofNullable(getLeft(obj));
    }

    /**
     * Get object by object in right mapping
     *
     * @param obj to get object for
     * @return object
     */
    public T getRight(R obj) {
        return rightMapping.get(obj);
    }

    /**
     * Get optional object by object in right mapping
     *
     * @param obj to get object for
     * @return optional object
     */
    public Optional<T> getOptionalRight(R obj) {
        return Optional.ofNullable(getRight(obj));
    }

    /**
     * Clear map
     */
    public void clear() {
        leftMapping.clear();
        rightMapping.clear();
    }

    public static <T, R> PairMap<T, R> of(Class<T> typeA, Class<R> typeB) {
        return of(typeA, typeB, ReflectedMap.getMapSupplier(HashMap.class));
    }

    public static <T, R> PairMap<T, R> of(Class<T> typeA, Class<R> typeB, IMapSupplier supplier) {
        return new PairMap<>(typeA, typeB, supplier);
    }

}
