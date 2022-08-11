package nl.iobyte.validator;

import nl.iobyte.framework.FW;
import nl.iobyte.framework.rest.FWRest;
import nl.iobyte.framework.rest.validator.ValidatorService;
import nl.iobyte.framework.rest.validator.objects.impl.map.annotations.MapRule;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class MapValidationRuleTest {

    /**
     * Setup framework stuff
     */
    private void setupFramework() {
        FWRest.acquireInstance(() -> null, false);
        FWRest instance = FWRest.getInstance();
        instance.init();
        instance.start();
    }

    @Test
    public void testExplicit() {
        setupFramework();

        ValidatorService service = FW.service(ValidatorService.class);
        service.register(TestExplicitMapObject.class);

        TestExplicitMapObject success = new TestExplicitMapObject();
        success.map = new HashMap<>() {{
            put("some_key", 12345);
            put("anotherKey", true);
            put(5D, false);
        }};

        TestExplicitMapObject fail = new TestExplicitMapObject();
        fail.map = new HashMap<>() {{
            put("key_some", false);
            put("anotherKey", false);
        }};

        test(service, success, fail);
    }

    @Test
    public void test() {
        setupFramework();

        ValidatorService service = FW.service(ValidatorService.class);
        service.register(TestMapObject.class);

        TestMapObject success = new TestMapObject();
        success.map = new HashMap<>() {{
            put("some_key", 12345);
            put("anotherKey", true);
            put(5D, false);
            put(Integer.toString(ThreadLocalRandom.current().nextInt()), "random");
        }};

        TestMapObject fail = new TestMapObject();
        fail.map = new HashMap<>() {{
            put("key_some", false);
            put(Integer.toString(ThreadLocalRandom.current().nextInt()), "random");
        }};

        test(service, success, fail);
    }

    @Test
    public void testCaseInsensitive() {
        setupFramework();

        ValidatorService service = FW.service(ValidatorService.class);
        service.register(TestCaseInsensitiveMapObject.class);

        TestCaseInsensitiveMapObject success = new TestCaseInsensitiveMapObject();
        success.map = new HashMap<>() {{
            put("some_Key", 12345);
            put("anotherkey", true);
        }};

        TestCaseInsensitiveMapObject fail = new TestCaseInsensitiveMapObject();
        fail.map = new HashMap<>() {{
            put("key_some", false);
        }};

        test(service, success, fail);
    }

    @Test
    public void testType() {
        setupFramework();

        ValidatorService service = FW.service(ValidatorService.class);
        service.register(TestTypeMapObject.class);

        TestTypeMapObject success = new TestTypeMapObject();
        success.map = new HashMap<>() {{
            put("some_key", 12345);
            put("test", 5D);
        }};

        TestTypeMapObject fail = new TestTypeMapObject();
        fail.map = new HashMap<>() {{
            put("some_key", false);
            put(5D, "test");
        }};

        test(service, success, fail);
    }

    /**
     * Test objects
     *
     * @param service validator service
     * @param success object
     * @param fail    object
     */
    public void test(ValidatorService service, Object success, Object fail) {
        try {
            Assert.assertTrue(service.test(success));
        } catch(Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        try {
            Assert.assertFalse(service.test(fail));
            Assert.fail("test should fail");
        } catch(Exception ignored) {
        }
    }

    private static class TestExplicitMapObject {

        @MapRule(keys = {"some_key", "anotherKey", "5.0"})
        private Map<Object, Object> map;

    }

    private static class TestMapObject {

        @MapRule(keys = {"some_key", "anotherKey", "5.0"}, explicit = false)
        private Map<Object, Object> map;

    }

    private static class TestCaseInsensitiveMapObject {

        @MapRule(keys = {"some_key", "anotherKey"}, caseSensitive = false)
        private Map<Object, Object> map;

    }

    private static class TestTypeMapObject {

        @MapRule(keys = {"some_key", "test"}, explicit = false, keyType = String.class, valueType = Number.class)
        private Map<Object, Object> map;

    }

}
