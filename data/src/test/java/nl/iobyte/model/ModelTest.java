package nl.iobyte.model;

import nl.iobyte.cache.TestCache;
import nl.iobyte.database.TestDatabase;
import nl.iobyte.framework.FW;
import nl.iobyte.framework.data.cache.CacheService;
import nl.iobyte.framework.data.database.DatabaseService;
import nl.iobyte.framework.data.model.ModelService;
import nl.iobyte.framework.data.model.objects.ModelWrapper;
import nl.iobyte.framework.data.model.objects.impl.cache.objects.CacheModelSource;
import nl.iobyte.framework.data.model.objects.impl.database.objects.source.DatabaseModelSource;
import nl.iobyte.framework.data.model.objects.impl.strategies.objects.CacheDatabaseSourceStrategy;
import nl.iobyte.framework.data.snowflake.SnowflakeService;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;

public class ModelTest {

    private int age;

    /**
     * Setup framework needs to test
     */
    private void setupFramework() {
        FW framework = FW.acquireInstance(() -> null, false);
        framework.registerBulk(
                CacheService.class,
                DatabaseService.class,
                SnowflakeService.class,
                ModelService.class
        );

        framework.init();
        framework.start();

        framework.get(CacheService.class).put(new TestCache());
        framework.get(DatabaseService.class).put(new TestDatabase());
    }

    @Test
    public void test() {
        setupFramework();

        ModelWrapper<TestModel> wrapper = FW.service(ModelService.class).register(TestModel.class);
        Assert.assertNotNull(wrapper);
        Assert.assertNotNull(wrapper.getKeyField());

        Assert.assertEquals(2, wrapper.getSources().size());
        Assert.assertNotNull(wrapper.getSource(CacheModelSource.class));
        Assert.assertNotNull(wrapper.getSource(DatabaseModelSource.class));

        Assert.assertEquals(1, wrapper.getSourceStrategies().size());
        //noinspection unchecked
        Assert.assertNotNull(wrapper.getSourceStrategy(CacheDatabaseSourceStrategy.class));

        try {
            TestModel model = wrapper.create();

            //Assert snowflake and therefore onCreate worked
            Assert.assertNotEquals(0, model.id);

            //Check rest of model is empty as expected
            Assert.assertNotEquals(0, model.id);
            Assert.assertNull(model.uuid);
            Assert.assertNull(model.name);
            Assert.assertNull(model.email);
            Assert.assertNull(model.password);
            Assert.assertEquals(0, model.age);

            model.uuid = UUID.randomUUID();

            //Test save transformers
            Map<String, Object> map = wrapper.save(DatabaseModelSource.class, model);
            Assert.assertNotNull(map);
            Assert.assertEquals(BigInteger.class, map.get("id").getClass());
            Assert.assertEquals(String.class, map.get("uuid").getClass());
            Assert.assertEquals(Timestamp.class, map.get("created_at").getClass());
            Assert.assertEquals(Timestamp.class, map.get("updated_at").getClass());

            //Test load transformers
            model = wrapper.load(DatabaseModelSource.class, map);
            Assert.assertNotNull(model);

            //Test get/save/delete
            try {
                FW.service(ModelService.class).get(TestModel.class, model.id);
                FW.service(ModelService.class).save(model);
                FW.service(ModelService.class).delete(TestModel.class, model.id);
            } catch(Exception e) {
                if(!(e instanceof SQLException))
                    throw e;
            }
        } catch(Exception e) {
            e.printStackTrace();
            Assert.fail("failed to create model instance");
        }
    }

}
