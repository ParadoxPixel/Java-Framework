package nl.iobyte.framework.data.model.objects.impl.cache.objects;

import nl.iobyte.framework.data.cache.interfaces.ICache;
import nl.iobyte.framework.data.model.interfaces.IModel;
import nl.iobyte.framework.data.model.interfaces.source.IModelSource;
import nl.iobyte.framework.data.model.objects.ModelWrapper;
import nl.iobyte.framework.generic.reflections.TypeConverter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CacheModelSource<T extends IModel> implements IModelSource {

    private final String name;
    private final ModelWrapper<T> wrapper;
    private final ICache cache;

    //Timeout settings
    private final int timeout;
    private final TimeUnit unit;

    public CacheModelSource(
            String name,
            ModelWrapper<T> wrapper,
            ICache cache,
            int timeout,
            TimeUnit unit
    ) {
        this.name = name;
        this.wrapper = wrapper;
        this.cache = cache;
        this.timeout = timeout;
        this.unit = unit;
    }

    /**
     * Check whether an object with the given key exists
     *
     * @param key to check
     * @return whether model with key exists
     * @throws Exception thrown while checking cache or during serialization
     */
    public boolean has(Object key) throws Exception {
        if(!wrapper.getKeyField().isAssignable(key))
            throw new IllegalArgumentException(
                    "invalid key of type " +
                            key.getClass().getSimpleName() +
                            " expected " + wrapper.getKeyField().getRawType().getName()
            );

        return cache.has(name, (String) TypeConverter.normalise(key, String.class));
    }

    /**
     * Get model from cache by key
     *
     * @param key of model
     * @return model instance
     * @throws Exception thrown while getting value from cache or serializing
     */
    public T get(Object key) throws Exception {
        if(!wrapper.getKeyField().isAssignable(key))
            throw new IllegalArgumentException(
                    "invalid key of type " +
                            key.getClass().getSimpleName() +
                            " expected " + wrapper.getKeyField().getRawType().getName()
            );

        byte[] bytes = cache.get(name, (String) TypeConverter.normalise(key, String.class));
        if(bytes == null || bytes.length == 0)
            return null;

        //noinspection unchecked
        Map<String, Object> map = (Map<String, Object>) TypeConverter.normalise(bytes, HashMap.class);
        if(map == null)
            throw new IllegalArgumentException(
                    "unable to serialize cache entry to map for key \"" + key + "\" in table \"" + name + "\""
            );

        return wrapper.load(CacheModelSource.class, map);
    }

    /**
     * Save model to cache
     *
     * @param model to save
     * @throws Exception thrown while serializing or saving model
     */
    public void save(T model) throws Exception {
        Object key = wrapper.getKeyField().getRawValue(model);
        if(key == null)
            throw new IllegalArgumentException("model does not have a key value set");

        Map<String, Object> map = wrapper.save(CacheModelSource.class, model);
        String str = (String) TypeConverter.normalise(map, String.class);
        if(str == null || str.isEmpty())
            throw new IllegalArgumentException("unable to serialize model to json");

        if(timeout > 0) {
            cache.set(name, (String) TypeConverter.normalise(key, String.class), str.getBytes(), timeout, unit);
            return;
        }

        cache.set(name, (String) TypeConverter.normalise(key, String.class), str.getBytes());
    }

    /**
     * Delete model from cache
     *
     * @param key to remove
     * @throws Exception thrown while removing model
     */
    public void delete(Object key) throws Exception {
        if(!wrapper.getKeyField().isAssignable(key))
            throw new IllegalArgumentException(
                    "invalid key of type " +
                            key.getClass().getSimpleName() +
                            " expected " + wrapper.getKeyField().getRawType().getName()
            );

        cache.remove(name, (String) TypeConverter.normalise(key, String.class));
    }

}
