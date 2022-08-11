package nl.iobyte.framework.data.model.objects.impl.database.objects.source;

import nl.iobyte.framework.data.database.interfaces.IDatabase;
import nl.iobyte.framework.data.model.interfaces.IModel;
import nl.iobyte.framework.data.model.interfaces.source.IModelSource;
import nl.iobyte.framework.data.model.objects.ModelWrapper;
import nl.iobyte.framework.structures.pmap.PairMap;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseModelSource<T extends IModel> implements IModelSource {

    private final String name;
    private final ModelWrapper<T> wrapper;
    private final IDatabase database;
    private final PairMap<String, String> columnMapping;

    public DatabaseModelSource(String name, ModelWrapper<T> wrapper, IDatabase database) {
        this.name = name;
        this.wrapper = wrapper;
        this.database = database;
        this.columnMapping = new PairMap<>(String.class, String.class);
    }

    /**
     * Add mapping for column
     *
     * @param fieldName  name of field
     * @param columnName name of column
     */
    public void addColumnMapping(String fieldName, String columnName) {
        columnMapping.set(fieldName, columnName);
    }

    /**
     * Check whether an object with the given key exists
     *
     * @param key to check
     * @return whether model with key exists
     */
    public boolean has(Object key) {
        if(!wrapper.getKeyField().isAssignable(key))
            throw new IllegalArgumentException(
                    "invalid key of type " +
                            key.getClass().getSimpleName() +
                            " expected " + wrapper.getKeyField().getRawType().getName()
            );

        return findAll(Map.of(
                wrapper.getKeyField().getSnakeCase(),
                key
        )).count() > 0;
    }

    /**
     * Get model by key
     *
     * @param key of model
     * @return model instance
     */
    public T get(Object key) {
        if(!wrapper.getKeyField().isAssignable(key))
            throw new IllegalArgumentException(
                    "invalid key of type " +
                            key.getClass().getSimpleName() +
                            " expected " + wrapper.getKeyField().getRawType().getName()
            );

        return find(Map.of(
                wrapper.getKeyField().getSnakeCase(),
                key
        ));
    }

    /**
     * Find first value that matches attributes
     *
     * @param attributes key -> value mapping
     * @return model instance
     */
    public T find(Map<String, Object> attributes) {
        return findAll(attributes).first();
    }

    /**
     * Find all with attributes
     *
     * @param attributes key -> value mapping
     * @return model query builder
     */
    public ModelQueryBuilder<T> findAll(Map<String, Object> attributes) {
        ModelQueryBuilder<T> builder = findAll();
        attributes.forEach(builder::where);
        return builder;
    }

    /**
     * Get model query builder
     *
     * @return model query builder instance
     */
    public ModelQueryBuilder<T> findAll() {
        return new ModelQueryBuilder<>(
                wrapper,
                columnMapping,
                database.builder(name)
        );
    }

    /**
     * Save model to database
     *
     * @param model instance
     * @throws Exception thrown while serializing or saving model
     */
    public void save(T model) throws Exception {
        //Get field -> value mapping
        Map<String, Object> map = wrapper.save(DatabaseModelSource.class, model);
        if(map.isEmpty())
            throw new IllegalArgumentException("unable to serialize model to map");

        //Map field names to column names
        Map<String, Object> result = new HashMap<>();
        for(Map.Entry<String, Object> entry : map.entrySet()) {
            //Skip empty index to allow for auto increment
            if(entry.getKey().equals(wrapper.getKeyField().getSnakeCase()))
                if(entry.getValue() == null)
                    continue;

            result.put(
                    columnMapping.getOptionalLeft(entry.getKey()).orElse(entry.getKey()),
                    entry.getValue()
            );
        }

        //Build query
        StringBuilder builder = new StringBuilder();
        builder.append("INSERT INTO ").append(name).append("(");
        builder.append("?,".repeat(result.size() - 1));
        builder.append("?) ON DUPLICATE KEY UPDATE ");

        boolean b = false;
        for(String key : result.keySet()) {
            if(b)
                builder.append(", ");

            b = true;
            builder.append(key).append("=?");
        }

        List<Object> parameters = new ArrayList<>(map.values());
        parameters.addAll(map.values());

        //Execute query
        int i = database.executeUpdate(builder.toString(), parameters.toArray());
        if(i != 0)
            return;

        throw new SQLException("unable to save model of type \"" + wrapper.getType().getName() + "\" to database");
    }

    /**
     * Delete model from cache
     *
     * @param key to remove
     * @throws Exception thrown while serializing or removing model
     */
    public void delete(Object key) throws Exception {
        if(!wrapper.getKeyField().isAssignable(key))
            throw new IllegalArgumentException(
                    "invalid key of type " +
                            key.getClass().getSimpleName() +
                            " expected " + wrapper.getKeyField().getRawType().getName()
            );

        int i = database.executeUpdate(
                "DELETE FROM " +
                        name +
                        " WHERE " +
                        columnMapping.getOptionalLeft(
                                wrapper.getKeyField().getSnakeCase()
                        ).orElse(wrapper.getKeyField().getSnakeCase()) +
                        "=?",
                key
        );
        if(i != 0)
            return;

        throw new SQLException(
                "unable to delete model of type \"" +
                        wrapper.getType().getName() +
                        "\" with key \"" +
                        key +
                        "\" from database"
        );
    }

}
