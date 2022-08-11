package nl.iobyte.framework.data.model.objects.impl.strategies.objects;

import nl.iobyte.framework.data.model.interfaces.IModel;
import nl.iobyte.framework.data.model.interfaces.source.IModelSourceStrategy;
import nl.iobyte.framework.data.model.objects.impl.cache.objects.CacheModelSource;

public class CacheSourceStrategy<T extends IModel> implements IModelSourceStrategy<T> {

    private final CacheModelSource<T> source;

    public CacheSourceStrategy(CacheModelSource<T> source) {
        this.source = source;
    }

    /**
     * Check whether an object with the given key exists
     *
     * @param key to check
     * @return whether model with key exists
     * @throws Exception thrown while checking cache or during serialization
     */
    public boolean has(Object key) throws Exception {
        return source.has(key);
    }

    /**
     * Get model from cache by key
     *
     * @param key of model
     * @return model instance
     * @throws Exception thrown while getting value from cache or serializing
     */
    public T get(Object key) throws Exception {
        return source.get(key);
    }

    /**
     * Save model to cache
     *
     * @param model to save
     * @throws Exception thrown while serializing or saving model
     */
    public void save(T model) throws Exception {
        source.save(model);
    }

    /**
     * Delete model from cache
     *
     * @param key to remove
     * @throws Exception thrown while removing model
     */
    public void delete(Object key) throws Exception {
        source.delete(key);
    }

}
