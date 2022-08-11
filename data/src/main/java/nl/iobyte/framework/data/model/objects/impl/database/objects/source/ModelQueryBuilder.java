package nl.iobyte.framework.data.model.objects.impl.database.objects.source;

import nl.iobyte.framework.data.database.enums.Order;
import nl.iobyte.framework.data.database.enums.Where;
import nl.iobyte.framework.data.database.interfaces.IQueryBuilder;
import nl.iobyte.framework.data.model.interfaces.IModel;
import nl.iobyte.framework.data.model.objects.ModelWrapper;
import nl.iobyte.framework.structures.pmap.PairMap;

import java.util.*;

public class ModelQueryBuilder<T extends IModel> {

    private final ModelWrapper<T> wrapper;
    private final PairMap<String, String> columnMapping;
    private final IQueryBuilder queryBuilder;

    public ModelQueryBuilder(
            ModelWrapper<T> wrapper,
            PairMap<String, String> columnMapping,
            IQueryBuilder queryBuilder
    ) {
        this.wrapper = wrapper;
        this.columnMapping = columnMapping;
        this.queryBuilder = queryBuilder;
    }

    /**
     * Set which columns to return
     *
     * @param columns array of column names
     * @return same instance
     */
    public ModelQueryBuilder<T> select(String... columns) {
        queryBuilder.select(
                Arrays.stream(columns)
                      .map(column -> columnMapping.getOptionalLeft(column).orElse(column))
                      .toArray(String[]::new)
        );
        return this;
    }

    /**
     * Where column matches value(auto detect operator)
     *
     * @param column name of column
     * @param values object to match
     * @return same instance
     */
    public ModelQueryBuilder<T> where(String column, Object... values) {
        queryBuilder.where(
                columnMapping.getOptionalLeft(column).orElse(column),
                values
        );
        return this;
    }

    /**
     * Where column matches value with operator
     *
     * @param column   name of column
     * @param operator where enum
     * @param values   object to match
     * @return same instance
     */
    public ModelQueryBuilder<T> where(String column, Where operator, Object... values) {
        queryBuilder.where(
                columnMapping.getOptionalLeft(column).orElse(column),
                operator,
                values
        );
        return this;
    }

    /**
     * Aesthetics(you can't mix them)
     */
    public ModelQueryBuilder<T> or() {
        queryBuilder.or();
        return this;
    }

    /**
     * Aesthetics(you can't mix them)
     */
    public ModelQueryBuilder<T> and() {
        queryBuilder.and();
        return this;
    }

    /**
     * Amount of rows to skip
     *
     * @param amount number of rows
     * @return same instance
     */
    public ModelQueryBuilder<T> skip(int amount) {
        queryBuilder.skip(amount);
        return this;
    }

    /**
     * Amount of rows to return
     *
     * @param amount number of rows
     * @return same instance
     */
    public ModelQueryBuilder<T> take(int amount) {
        queryBuilder.take(amount);
        return this;
    }

    /**
     * Set columns to group by
     *
     * @param columns list of column names
     * @return same instance
     */
    public ModelQueryBuilder<T> groupBy(String... columns) {
        queryBuilder.groupBy(
                Arrays.stream(columns)
                      .map(column -> columnMapping.getOptionalLeft(column).orElse(column))
                      .toArray(String[]::new)
        );
        return this;
    }

    /**
     * Set columns to sort on and in which order
     *
     * @param order   ASC/DESC
     * @param columns list of column names
     * @return same instance
     */
    public ModelQueryBuilder<T> orderBy(Order order, String... columns) {
        queryBuilder.orderBy(
                order,
                Arrays.stream(columns)
                      .map(column -> columnMapping.getOptionalLeft(column).orElse(column))
                      .toArray(String[]::new)
        );
        return this;
    }

    /**
     * Count rows matching query
     *
     * @return number of rows
     */
    public long count() {
        return queryBuilder.count();
    }

    /**
     * Get the result of the query
     *
     * @return list of rows
     */
    public List<T> get() {
        List<Map<String, Object>> results = queryBuilder.get();
        if(results == null || results.isEmpty())
            return new ArrayList<>();

        List<T> models = new ArrayList<>();
        for(Map<String, Object> row : results)
            models.add(load(row));

        return models;
    }

    /**
     * Get the first result of the query
     *
     * @return rows
     */
    public T first() {
        return load(queryBuilder.first());
    }

    /**
     * Load model from map
     *
     * @param result key -> value mapping
     * @return model instance
     */
    private T load(Map<String, Object> result) {
        try {
            if(result == null || result.isEmpty())
                return null;

            Map<String, Object> map = new HashMap<>();
            for(Map.Entry<String, Object> entry : result.entrySet())
                map.put(
                        columnMapping.getOptionalRight(entry.getKey()).orElse(entry.getKey()),
                        entry.getValue()
                );

            return wrapper.load(DatabaseModelSource.class, map);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

}
