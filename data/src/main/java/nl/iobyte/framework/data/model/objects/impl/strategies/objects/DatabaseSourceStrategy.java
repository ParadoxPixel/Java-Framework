package nl.iobyte.framework.data.model.objects.impl.strategies.objects;

import nl.iobyte.framework.data.model.interfaces.IModel;
import nl.iobyte.framework.data.model.interfaces.source.IModelSourceStrategy;
import nl.iobyte.framework.data.model.objects.impl.database.objects.source.DatabaseModelSource;
import nl.iobyte.framework.data.model.objects.impl.database.objects.source.ModelQueryBuilder;

import java.util.Map;

public class DatabaseSourceStrategy<T extends IModel> implements IModelSourceStrategy<T> {

    private final DatabaseModelSource<T> source;

    public DatabaseSourceStrategy(DatabaseModelSource<T> source) {
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
     * Get model by key from database
     *
     * @param key of model
     * @return model instance
     */
    public T get(Object key) {
        return source.get(key);
    }

    /**
     * Find first value that matches attributes
     *
     * @param attributes key -> value mapping
     * @return model instance
     */
    public T find(Map<String, Object> attributes) {
        return source.find(attributes);
    }

    /**
     * Find all with attributes
     *
     * @param attributes key -> value mapping
     * @return model query builder
     */
    public ModelQueryBuilder<T> findAll(Map<String, Object> attributes) {
        return source.findAll(attributes);
    }

    /**
     * Get model query builder
     *
     * @return model query builder instance
     */
    public ModelQueryBuilder<T> findAll() {
        return source.findAll();
    }

    /**
     * Save model to database
     *
     * @param model instance
     * @throws Exception thrown while serializing or saving model
     */
    public void save(T model) throws Exception {
        source.save(model);
    }

    /**
     * Delete model from database
     *
     * @param key to remove
     * @throws Exception thrown while serializing or removing model
     */
    public void delete(Object key) throws Exception {
        source.delete(key);
    }

}
