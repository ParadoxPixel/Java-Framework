package nl.iobyte.database;

import nl.iobyte.framework.data.database.enums.Order;
import nl.iobyte.framework.data.database.enums.Where;
import nl.iobyte.framework.data.database.interfaces.IQueryBuilder;

import java.util.List;
import java.util.Map;

public class TestQueryBuilder implements IQueryBuilder {

    @Override
    public IQueryBuilder select(String... columns) {
        return null;
    }

    @Override
    public IQueryBuilder where(String column, Object... values) {
        return null;
    }

    @Override
    public IQueryBuilder where(String column, Where operator, Object... values) {
        return null;
    }

    @Override
    public IQueryBuilder or() {
        return null;
    }

    @Override
    public IQueryBuilder and() {
        return null;
    }

    @Override
    public IQueryBuilder skip(int amount) {
        return null;
    }

    @Override
    public IQueryBuilder take(int amount) {
        return null;
    }

    @Override
    public IQueryBuilder groupBy(String... columns) {
        return null;
    }

    @Override
    public IQueryBuilder orderBy(Order order, String... columns) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public List<Map<String, Object>> get() {
        return null;
    }

    @Override
    public Map<String, Object> first() {
        return null;
    }

}
