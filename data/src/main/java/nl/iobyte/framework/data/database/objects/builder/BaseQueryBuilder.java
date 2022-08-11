package nl.iobyte.framework.data.database.objects.builder;

import nl.iobyte.framework.data.database.enums.Order;
import nl.iobyte.framework.data.database.enums.Where;
import nl.iobyte.framework.data.database.interfaces.IDatabase;
import nl.iobyte.framework.data.database.interfaces.IQueryBuilder;
import nl.iobyte.framework.data.database.objects.builder.parts.WhereClause;
import nl.iobyte.framework.generic.reflections.TypeConverter;

import java.lang.constant.Constable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BaseQueryBuilder implements IQueryBuilder {

    private final String table;
    private final IDatabase database;

    private final List<String> select = new ArrayList<>();

    private final List<WhereClause> whereClauses = new ArrayList<>();
    private boolean and = true;

    private int skip = 0;
    private int take = 0;

    private final List<String> groupBy = new ArrayList<>();

    private final List<String> orderBy = new ArrayList<>();
    private Order order = Order.ASC;

    public BaseQueryBuilder(String table, IDatabase database) {
        this.table = table;
        this.database = database;
    }

    @Override
    public IQueryBuilder select(String... columns) {
        select.addAll(List.of(columns));
        return this;
    }

    @Override
    public IQueryBuilder where(String column, Object... values) {
        List<Object> list = new ArrayList<>();
        for(Object value : values) {
            if(value instanceof List<?> l) {
                list.addAll(l);
                continue;
            }

            if(value instanceof Object[] array) {
                list.addAll(List.of(array));
                continue;
            }

            if(value instanceof Map<?, ?>)
                continue;

            list.add(value);
        }

        if(list.isEmpty())
            return this;

        Where operator = list.size() == 1 ? Where.EQUALS : Where.IN;
        if(list.size() == 2)
            if(list.get(0) instanceof Number)
                if(list.get(0).getClass().isInstance(list.get(1)))
                    operator = Where.BETWEEN;

        return where(column, operator, list);
    }

    @Override
    public IQueryBuilder where(String column, Where operator, Object... values) {
        List<Object> list;
        if(values.length == 1 && values[0] instanceof List<?> l) {
            //noinspection unchecked
            list = (List<Object>) l;
        } else {
            list = new ArrayList<>();
            for(Object value : values) {
                if(value instanceof List<?> l) {
                    list.addAll(l);
                    continue;
                }

                if(value instanceof Object[] array) {
                    list.addAll(List.of(array));
                    continue;
                }

                if(value instanceof Map<?, ?>)
                    continue;

                list.add(value);
            }
        }

        if(list.isEmpty())
            return this;

        //Transform any parameters to match column types
        for(int i = 0; i < list.size(); i++)
            if(!(list.get(i) instanceof Constable))
                list.set(i, TypeConverter.normalise(list.get(i), String.class));

        boolean b1 = operator == Where.EQUALS || operator == Where.IN;
        boolean b2 = operator == Where.NOT_EQUALS || operator == Where.NOT_IN;
        if(!b1 && !b2) {
            whereClauses.add(new WhereClause(
                    column,
                    operator,
                    list
            ));
            return this;
        }

        boolean b = whereClauses.stream().anyMatch(clause -> {
            if(!column.equals(clause.column()))
                return false;

            if(clause.operator() == Where.EQUALS || clause.operator() == Where.NOT_EQUALS) {
                list.addAll(clause.values());
                whereClauses.remove(clause);
                whereClauses.add(new WhereClause(
                        column,
                        b1 ? Where.IN : Where.NOT_IN,
                        list
                ));
                return true;
            }

            if(b1)
                if(clause.operator() != Where.IN)
                    return false;

            if(b2)
                if(clause.operator() != Where.NOT_IN)
                    return false;

            clause.values().addAll(list);
            return true;
        });

        if(!b) {
            whereClauses.add(new WhereClause(
                    column,
                    operator,
                    list
            ));
        }

        return this;
    }

    public IQueryBuilder or() {
        and = false;
        return this;
    }

    public IQueryBuilder and() {
        and = true;
        return this;
    }

    @Override
    public IQueryBuilder skip(int amount) {
        skip = amount;
        return this;
    }

    @Override
    public IQueryBuilder take(int amount) {
        take = amount;
        return this;
    }

    @Override
    public IQueryBuilder groupBy(String... columns) {
        groupBy.addAll(List.of(columns));
        return this;
    }

    @Override
    public IQueryBuilder orderBy(Order order, String... columns) {
        this.order = order;
        orderBy.addAll(List.of(columns));
        return this;
    }

    /**
     * Build select part of query
     *
     * @return String
     */
    private String buildSelect() {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT ");
        if(select.isEmpty()) {
            builder.append("*");
            return builder.toString();
        }

        boolean first = true;
        for(String column : select) {
            if(!first)
                builder.append(", ");

            first = false;
            builder.append(commentName(column));
        }

        return builder.toString();
    }

    private String buildWhere() {
        StringBuilder builder = new StringBuilder();
        builder.append("WHERE ");

        boolean first = true;
        for(WhereClause whereClause : whereClauses) {
            if(!first)
                builder.append(" ").append(and ? "AND" : "OR").append(" ");

            builder.append(whereClause);
        }

        return builder.toString();
    }

    /**
     * Build groupBy part of query
     *
     * @return String
     */
    private String buildGroupBy() {
        StringBuilder builder = new StringBuilder();
        builder.append("GROUP BY ");

        boolean first = true;
        for(String column : groupBy) {
            if(!first)
                builder.append(", ");

            first = false;
            builder.append(commentName(column));
        }

        return builder.toString();
    }

    /**
     * Build orderBy part of query
     *
     * @return String
     */
    private String buildOrderBy() {
        StringBuilder builder = new StringBuilder();
        builder.append("ORDER BY ");

        boolean first = true;
        for(String column : orderBy) {
            if(!first)
                builder.append(", ");

            first = false;
            builder.append(commentName(column));
        }

        return builder.append(" ").append(order).toString();
    }

    @Override
    public long count() {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT COUNT(*) AS `c` FROM ").append(commentName(table));
        if(!whereClauses.isEmpty())
            builder.append(" ").append(buildWhere());

        if(!groupBy.isEmpty())
            builder.append(" ").append(buildGroupBy());

        List<Object> parameters = new ArrayList<>();
        for(WhereClause whereClause : whereClauses)
            parameters.addAll(whereClause.values());

        List<Map<String, Object>> rows = database.executeQuery(builder.toString(), parameters);
        if(rows == null || rows.isEmpty())
            return 0;

        Object count = rows.get(0).get("c");
        if(count instanceof Integer i)
            return i;

        if(count instanceof Long l)
            return l;

        if(count instanceof BigInteger bi)
            return bi.longValue();

        throw new IllegalStateException("unknown type " + count.getClass().getSimpleName() + " for COUNT(*)");
    }

    /**
     * Comment column/database name in string
     *
     * @param name column/database name
     * @return commented column/database name
     */
    private static String commentName(String name) {
        return "`" + name.replace(".", "`.`") + "`";
    }

    @Override
    public List<Map<String, Object>> get() {
        StringBuilder builder = new StringBuilder();
        builder.append(buildSelect()).append(" FROM ").append(commentName(table));
        if(!whereClauses.isEmpty())
            builder.append(" ").append(buildWhere());

        if(!groupBy.isEmpty())
            builder.append(" ").append(buildGroupBy());

        if(!orderBy.isEmpty())
            builder.append(" ").append(buildOrderBy());

        if(skip > 0)
            builder.append(" OFFSET ").append(skip);

        if(take > 0)
            builder.append(" LIMIT ").append(take);

        List<Object> parameters = new ArrayList<>();
        for(WhereClause whereClause : whereClauses)
            parameters.addAll(whereClause.values());

        return database.executeQuery(builder.toString(), parameters.toArray(Object[]::new));
    }

    @Override
    public Map<String, Object> first() {
        int localTake = take;
        take = 1;
        List<Map<String, Object>> results = get();
        take = localTake;

        if(results.size() == 0)
            return null;

        return results.get(0);
    }

}
