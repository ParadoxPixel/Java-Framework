package nl.iobyte.framework.structures.suppliers;

import java.util.Set;

public interface ISetSupplier {

    /**
     * Get set of type
     *
     * @param <T> entry type
     * @return set
     */
    <T> Set<T> get();

}
