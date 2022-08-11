package nl.iobyte.database;

import nl.iobyte.framework.data.database.interfaces.IDatabase;
import nl.iobyte.framework.data.database.interfaces.IQueryBuilder;

import java.util.List;
import java.util.Map;

public class TestDatabase implements IDatabase {

    @Override
    public IQueryBuilder builder(String table) {
        return new TestQueryBuilder();
    }

    @Override
    public boolean execute(String sql, Object... parameters) {
        return false;
    }

    @Override
    public int executeUpdate(String sql, Object... parameters) {
        return 0;
    }

    @Override
    public List<Map<String, Object>> executeQuery(String sql, Object... parameters) {
        return null;
    }

    @Override
    public void closeConnection() {

    }

    @Override
    public String getId() {
        return "test_database";
    }

}
