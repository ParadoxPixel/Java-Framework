package nl.iobyte.framework.structures.suppliers;

import java.util.Map;

public interface IMapSupplier {

    /**
     * Get map of with types
     *
     * @param <K> key type
     * @param <V> value type
     * @return map
     */
    <K, V> Map<K, V> get();

}
