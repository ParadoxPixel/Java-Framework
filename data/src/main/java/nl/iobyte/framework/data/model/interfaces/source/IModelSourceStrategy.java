package nl.iobyte.framework.data.model.interfaces.source;

import nl.iobyte.framework.data.model.interfaces.IModel;

public interface IModelSourceStrategy<T extends IModel> {

    /**
     * Check whether an object with the given key exists
     *
     * @param key to check
     * @return whether model with key exists
     * @throws Exception thrown while checking cache or during serialization
     */
    boolean has(Object key) throws Exception;

    /**
     * Get model by key
     *
     * @param key of model
     * @return model instance
     * @throws Exception thrown while getting value from cache or serializing
     */
    T get(Object key) throws Exception;

    /**
     * Save model to source
     *
     * @param model to save
     * @throws Exception thrown while serializing or saving model
     */
    void save(T model) throws Exception;

    /**
     * Delete model
     *
     * @param key to remove
     * @throws Exception thrown while removing model
     */
    void delete(Object key) throws Exception;

}
