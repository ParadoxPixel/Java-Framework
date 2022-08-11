package nl.iobyte.framework.data.database.interfaces;

import nl.iobyte.framework.structures.omap.interfaces.IObject;

import java.util.List;
import java.util.Map;

public interface IDatabase extends IObject<String> {

    /**
     * Get query builder for table
     *
     * @param table name
     * @return query builder instance
     */
    IQueryBuilder builder(String table);

    /**
     * Execute query with parameters
     *
     * @param sql        query string
     * @param parameters list of parameters
     * @return successful or not
     */
    boolean execute(String sql, Object... parameters);

    /**
     * Execute update query with parameters
     *
     * @param sql        query string
     * @param parameters list of parameters
     * @return affected rows
     */
    int executeUpdate(String sql, Object... parameters);

    /**
     * Execute query with parameters
     *
     * @param sql        query string
     * @param parameters list of parameters
     * @return list of rows
     */
    List<Map<String, Object>> executeQuery(String sql, Object... parameters);

    /**
     * Close open connection(s) to database
     */
    void closeConnection();

}
