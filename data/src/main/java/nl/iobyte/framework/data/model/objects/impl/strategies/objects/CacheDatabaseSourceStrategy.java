package nl.iobyte.framework.data.model.objects.impl.strategies.objects;

import nl.iobyte.framework.data.model.interfaces.IModel;
import nl.iobyte.framework.data.model.interfaces.source.IModelSourceStrategy;
import nl.iobyte.framework.data.model.objects.ModelWrapper;
import nl.iobyte.framework.data.model.objects.impl.cache.objects.CacheModelSource;
import nl.iobyte.framework.data.model.objects.impl.database.objects.source.DatabaseModelSource;
import nl.iobyte.framework.data.model.objects.impl.database.objects.source.ModelQueryBuilder;

import java.util.Map;
import java.util.function.Supplier;

//TODO Add save/get strategy when to use cache and when to bypass for consistency
public class CacheDatabaseSourceStrategy<T extends IModel> implements IModelSourceStrategy<T> {

    private final CacheModelSource<T> cacheSource;
    private final DatabaseModelSource<T> databaseSource;
    private final Supplier<Boolean> bypass;

    public CacheDatabaseSourceStrategy(CacheModelSource<T> cacheSource,
                                       DatabaseModelSource<T> databaseSource,
                                       Supplier<Boolean> bypass) {
        this.cacheSource = cacheSource;
        this.databaseSource = databaseSource;
        this.bypass = bypass;
    }

    public CacheDatabaseSourceStrategy(
            CacheModelSource<?> cacheSource,
            DatabaseModelSource<?> databaseSource,
            ModelWrapper<T> wrapper,
            Supplier<Boolean> bypass
    ) {
        //noinspection unchecked
        this((CacheModelSource<T>) cacheSource, (DatabaseModelSource<T>) databaseSource, bypass);
    }

    /**
     * Check whether an object with the given key exists
     *
     * @param key to check
     * @return whether model with key exists
     * @throws Exception thrown while checking cache or during serialization
     */
    @Override
    public boolean has(Object key) throws Exception {
        if(!bypass.get())
            if(cacheSource.has(key))
                return true;

        return databaseSource.has(key);
    }

    /**
     * Get model by key from cache then database
     *
     * @param key of model
     * @return model instance
     * @throws Exception thrown while getting from cache
     */
    public T get(Object key) throws Exception {
        T obj = null;
        if(!bypass.get())
            obj = cacheSource.get(key);

        if(obj != null)
            return obj;

        obj = databaseSource.get(key);
        if(obj != null)
            cacheSource.save(obj);

        return obj;
    }

    /**
     * Find first value that matches attributes
     *
     * @param attributes key -> value mapping
     * @return model instance
     */
    public T find(Map<String, Object> attributes) {
        return databaseSource.find(attributes);
    }

    /**
     * Find all with attributes
     *
     * @param attributes key -> value mapping
     * @return model query builder
     */
    public ModelQueryBuilder<T> findAll(Map<String, Object> attributes) {
        return databaseSource.findAll(attributes);
    }

    /**
     * Get model query builder
     *
     * @return model query builder instance
     */
    public ModelQueryBuilder<T> findAll() {
        return databaseSource.findAll();
    }

    /**
     * Save model to cache and database
     *
     * @param model instance
     * @throws Exception thrown while serializing or saving model
     */
    public void save(T model) throws Exception {
        cacheSource.save(model);
        databaseSource.save(model);
    }

    /**
     * Delete model from cache
     *
     * @param key to remove
     * @throws Exception thrown while serializing or removing model
     */
    public void deleteFromCache(Object key) throws Exception {
        cacheSource.delete(key);
    }

    /**
     * Delete model from cache and database
     *
     * @param key to remove
     * @throws Exception thrown while serializing or removing model
     */
    public void delete(Object key) throws Exception {
        cacheSource.delete(key);
        databaseSource.delete(key);
    }

}
