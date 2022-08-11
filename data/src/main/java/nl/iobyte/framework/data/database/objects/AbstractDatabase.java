package nl.iobyte.framework.data.database.objects;

import nl.iobyte.framework.data.database.interfaces.IDatabase;
import nl.iobyte.framework.data.database.objects.builder.BaseQueryBuilder;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractDatabase implements IDatabase {

    private final String id;

    protected AbstractDatabase(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    /**
     * Get query builder for table
     *
     * @param table name
     * @return base query builder instance
     */
    public BaseQueryBuilder builder(String table) {
        return new BaseQueryBuilder(table, this);
    }

    /**
     * Aquire a database connection
     *
     * @return connection instance
     * @throws SQLException when exception occurs
     */
    public abstract Connection getConnection() throws SQLException;

    /**
     * {@inheritDoc}
     *
     * @param query      {@inheritDoc}
     * @param parameters {@inheritDoc}
     * @return {@inheritDoc}
     */
    public boolean execute(String query, Object... parameters) {
        if(query == null || query.isEmpty())
            return false;

        boolean b = false;
        try(Connection conn = getConnection()) {
            try(PreparedStatement statement = conn.prepareStatement(query)) {
                for(int i = 0; i < parameters.length; i++)
                    statement.setObject(i + 1, parameters[i]);

                b = statement.execute();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        return b;
    }

    /**
     * {@inheritDoc}
     *
     * @param query      {@inheritDoc}
     * @param parameters {@inheritDoc}
     * @return {@inheritDoc}
     */
    public int executeUpdate(String query, Object... parameters) {
        if(query == null || query.isEmpty())
            return 0;

        int i = 0;
        try(Connection conn = getConnection()) {
            try(PreparedStatement statement = conn.prepareStatement(query)) {
                for(int j = 0; j < parameters.length; j++)
                    statement.setObject(j + 1, parameters[j]);

                i = statement.executeUpdate();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        return i;
    }

    /**
     * {@inheritDoc}
     *
     * @param query      {@inheritDoc}
     * @param parameters {@inheritDoc}
     * @return {@inheritDoc}
     */
    public List<Map<String, Object>> executeQuery(String query, Object... parameters) {
        if(query == null || query.isEmpty())
            return null;

        ArrayList<Map<String, Object>> set = null;
        try(Connection conn = getConnection()) {
            try(PreparedStatement statement = conn.prepareStatement(query)) {
                for(int j = 0; j < parameters.length; j++)
                    statement.setObject(j + 1, parameters[j]);

                try(ResultSet result = statement.executeQuery()) {
                    set = new ArrayList<>();
                    ResultSetMetaData metadata = result.getMetaData();

                    int columnCount = metadata.getColumnCount();
                    while(result.next()) {
                        Map<String, Object> map = new HashMap<>();
                        for(int i = 1; i <= columnCount; i++)
                            map.put(metadata.getColumnName(i), result.getObject(i));

                        set.add(map);
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        return set;
    }

}
