package nl.iobyte.framework.data.database.interfaces;

import nl.iobyte.framework.data.database.enums.Order;
import nl.iobyte.framework.data.database.enums.Where;

import java.util.List;
import java.util.Map;

@SuppressWarnings("UnusedReturnValue")
public interface IQueryBuilder {

    /**
     * Set which columns to return
     *
     * @param columns array of column names
     * @return same instance
     */
    IQueryBuilder select(String... columns);

    /**
     * Where column matches value(auto detect operator)
     *
     * @param column name of column
     * @param values object to match
     * @return same instance
     */
    IQueryBuilder where(String column, Object... values);

    /**
     * Where column matches value with operator
     *
     * @param column   name of column
     * @param operator where enum
     * @param values   object to match
     * @return same instance
     */
    IQueryBuilder where(String column, Where operator, Object... values);

    //Aesthetics(don't mix them)
    IQueryBuilder or();

    IQueryBuilder and();

    /**
     * Amount of rows to skip
     *
     * @param amount number of rows
     * @return same instance
     */
    IQueryBuilder skip(int amount);

    /**
     * Amount of rows to return
     *
     * @param amount number of rows
     * @return same instance
     */
    IQueryBuilder take(int amount);

    /**
     * Set columns to group by
     *
     * @param columns list of column names
     * @return same instance
     */
    IQueryBuilder groupBy(String... columns);

    /**
     * Set columns to sort on and in which order
     *
     * @param order   ASC/DESC
     * @param columns list of column names
     * @return same instance
     */
    IQueryBuilder orderBy(Order order, String... columns);

    /**
     * Count rows matching query
     *
     * @return number of rows
     */
    long count();

    /**
     * Get the result of the query
     *
     * @return list of rows
     */
    List<Map<String, Object>> get();

    /**
     * Get the first result of the query
     *
     * @return rows
     */
    Map<String, Object> first();

}
