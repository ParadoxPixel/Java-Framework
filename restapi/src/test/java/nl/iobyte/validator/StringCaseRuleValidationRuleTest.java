package nl.iobyte.validator;

import nl.iobyte.framework.rest.validator.ValidatorService;
import nl.iobyte.framework.rest.validator.annotations.Validate;
import nl.iobyte.framework.rest.validator.objects.impl.string.annotations.StringCaseRule;
import nl.iobyte.framework.rest.validator.objects.impl.string.enums.StringCase;
import org.junit.Assert;
import org.junit.Test;

public class StringCaseRuleValidationRuleTest {

    @Test
    public void testCheck() {
        ValidatorService service = new ValidatorService();
        service.register(TestCheckObject.class);

        TestCheckObject success = new TestCheckObject();
        success.lower = "lower";
        success.upper = "UPPER";

        TestCheckObject fail = new TestCheckObject();
        fail.lower = "Lower";
        fail.upper = "uPPER";

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

    @Test
    public void testTransform() {
        ValidatorService service = new ValidatorService();
        service.register(TestTransformObject.class);

        TestTransformObject success = new TestTransformObject();
        success.lower = "Lower";
        success.upper = "uPPER";

        try {
            Assert.assertTrue(service.test(success));
        } catch(Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        Assert.assertEquals(success.lower, "lower");
        Assert.assertEquals(success.upper, "UPPER");
    }

    @Validate
    private static class TestCheckObject {

        @StringCaseRule(type = StringCase.LOWER, transform = false)
        private String lower;

        @StringCaseRule(type = StringCase.UPPER, transform = false)
        private String upper;

    }

    @Validate
    private static class TestTransformObject {

        @StringCaseRule(type = StringCase.LOWER)
        private String lower;

        @StringCaseRule(type = StringCase.UPPER)
        private String upper;

    }

}
