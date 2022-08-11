package nl.iobyte.validator;

import nl.iobyte.framework.FW;
import nl.iobyte.framework.rest.FWRest;
import nl.iobyte.framework.rest.validator.ValidatorService;
import nl.iobyte.framework.rest.validator.annotations.Validate;
import nl.iobyte.framework.rest.validator.objects.impl.range.annotations.IntRangeRule;
import nl.iobyte.framework.rest.validator.objects.impl.range.enums.RangeType;
import org.junit.Assert;
import org.junit.Test;

public class ValidateValidationRuleTest {

    @Test
    public void test() {
        FWRest instance = FWRest.acquireInstance(() -> null, false);
        instance.init();
        instance.start();

        ValidatorService service = FW.service(ValidatorService.class);
        service.register(TestValidateObject.class);
        service.register(TestIntegerRangeObject.class);

        TestValidateObject success = new TestValidateObject();
        TestIntegerRangeObject successObj = new TestIntegerRangeObject();
        successObj.min = 25565;
        successObj.max = 25665;
        successObj.range = 25566;
        success.obj = successObj;

        TestValidateObject fail = new TestValidateObject();
        TestIntegerRangeObject failObj = new TestIntegerRangeObject();
        failObj.min = 25500;
        failObj.max = 25666;
        failObj.range = 25500;
        fail.obj = failObj;

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

    private static class TestValidateObject {

        @Validate
        private TestIntegerRangeObject obj;

    }

    private static class TestIntegerRangeObject {

        @IntRangeRule(type = RangeType.MIN, lower = 25565)
        private int min;

        @IntRangeRule(type = RangeType.MAX, upper = 25665)
        private int max;

        @IntRangeRule(type = RangeType.RANGE, lower = 25565, upper = 25665)
        private int range;

    }

}
