package nl.iobyte.model;

import nl.iobyte.framework.data.model.interfaces.IModel;
import nl.iobyte.framework.data.model.objects.impl.cache.annotations.Cache;
import nl.iobyte.framework.data.model.objects.impl.database.annotations.Column;
import nl.iobyte.framework.data.model.objects.impl.database.annotations.Database;
import nl.iobyte.framework.data.model.objects.impl.snowflake.annotations.Snowflake;
import nl.iobyte.framework.data.model.objects.impl.strategies.annotations.CacheDatabaseStrategy;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.UUID;

@Cache(id = "test_cache")
@Database(id = "test_database")
@CacheDatabaseStrategy
public class TestModel implements IModel {

    @Snowflake //serves as @ModelKey
    @Column(targetType = BigInteger.class)
    public long id;

    @Column(targetType = String.class)
    public UUID uuid;

    @Column
    public String name;

    @Column
    public int age;

    @Column
    public String email;

    @Column
    public String password;

    @Column(targetType = Timestamp.class)
    public long createdAt;

    @Column(targetType = Timestamp.class)
    public long updatedAt;

}
