package nl.iobyte.framework.structures.suppliers;

import java.util.List;

public interface IListSupplier {

    /**
     * Get list of type
     *
     * @param <T> entry type
     * @return list
     */
    <T> List<T> get();

}
