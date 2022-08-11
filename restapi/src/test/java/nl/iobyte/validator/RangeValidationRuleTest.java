package nl.iobyte.validator;

import nl.iobyte.framework.rest.validator.ValidatorService;
import nl.iobyte.framework.rest.validator.objects.impl.range.annotations.*;
import nl.iobyte.framework.rest.validator.objects.impl.range.enums.RangeType;
import org.junit.Assert;
import org.junit.Test;

public class RangeValidationRuleTest {

    @Test
    public void testIntegerRange() {
        ValidatorService service = new ValidatorService();
        service.register(TestIntegerRangeObject.class);

        TestIntegerRangeObject success = new TestIntegerRangeObject();
        success.min = 25565;
        success.max = 25665;
        success.range = 25566;

        TestIntegerRangeObject fail = new TestIntegerRangeObject();
        fail.min = 25500;
        fail.max = 25666;
        fail.range = 25500;

        test(service, success, fail);
    }

    @Test
    public void testLongRange() {
        ValidatorService service = new ValidatorService();
        service.register(TestLongRangeObject.class);

        TestLongRangeObject success = new TestLongRangeObject();
        success.min = 25565;
        success.max = 25665;
        success.range = 25566;

        TestLongRangeObject fail = new TestLongRangeObject();
        fail.min = 25500;
        fail.max = 25666;
        fail.range = 25500;

        test(service, success, fail);
    }

    @Test
    public void testDoubleRange() {
        ValidatorService service = new ValidatorService();
        service.register(TestDoubleRangeObject.class);

        TestDoubleRangeObject success = new TestDoubleRangeObject();
        success.min = 0.5D;
        success.max = 1.5D;
        success.range = 1D;

        TestDoubleRangeObject fail = new TestDoubleRangeObject();
        fail.min = 0.4D;
        fail.max = 1.6D;
        fail.range = 2D;

        test(service, success, fail);
    }

    @Test
    public void testFloatRange() {
        ValidatorService service = new ValidatorService();
        service.register(TestFloatRangeObject.class);

        TestFloatRangeObject success = new TestFloatRangeObject();
        success.min = 0.5F;
        success.max = 1.5F;
        success.range = 1F;

        TestFloatRangeObject fail = new TestFloatRangeObject();
        fail.min = 0.4F;
        fail.max = 1.6F;
        fail.range = 2F;

        test(service, success, fail);
    }

    @Test
    public void testStringRange() {
        ValidatorService service = new ValidatorService();
        service.register(TestStringRangeObject.class);

        TestStringRangeObject success = new TestStringRangeObject();
        success.min = "John Doe";
        success.max = "John Doe";
        success.range = "John Doe";

        TestStringRangeObject fail = new TestStringRangeObject();
        fail.min = "test";
        fail.max = "12345678910111213";
        fail.range = "hmm";

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

    private static class TestIntegerRangeObject {

        @IntRangeRule(type = RangeType.MIN, lower = 25565)
        private int min;

        @IntRangeRule(type = RangeType.MAX, upper = 25665)
        private int max;

        @IntRangeRule(type = RangeType.RANGE, lower = 25565, upper = 25665)
        private int range;

    }

    private static class TestLongRangeObject {

        @LongRangeRule(type = RangeType.MIN, lower = 25565)
        private long min;

        @LongRangeRule(type = RangeType.MAX, upper = 25665)
        private long max;

        @LongRangeRule(type = RangeType.RANGE, lower = 25565, upper = 25665)
        private long range;

    }

    private static class TestDoubleRangeObject {

        @DoubleRangeRule(type = RangeType.MIN, lower = 0.5D)
        private double min;

        @DoubleRangeRule(type = RangeType.MAX, upper = 1.5D)
        private double max;

        @DoubleRangeRule(type = RangeType.RANGE, lower = 0.5D, upper = 1.5D)
        private double range;

    }

    private static class TestFloatRangeObject {

        @FloatRangeRule(type = RangeType.MIN, lower = 0.5F)
        private float min;

        @FloatRangeRule(type = RangeType.MAX, upper = 1.5F)
        private float max;

        @FloatRangeRule(type = RangeType.RANGE, lower = 0.5F, upper = 1.5F)
        private float range;

    }

    private static class TestStringRangeObject {

        @StringRangeRule(type = RangeType.MIN, lower = 5)
        private String min;

        @StringRangeRule(type = RangeType.MAX, upper = 16)
        private String max;

        @StringRangeRule(type = RangeType.RANGE, lower = 5, upper = 16)
        private String range;

    }

}
