package nl.iobyte.validator;

import nl.iobyte.framework.rest.validator.ValidatorService;
import nl.iobyte.framework.rest.validator.objects.impl.empty.annotations.NotEmptyRule;
import org.junit.Assert;
import org.junit.Test;

public class NotEmptyRuleValidationRuleTest {

    @Test
    public void testEmpty() {
        ValidatorService service = new ValidatorService();
        service.register(TestEmptyObject.class);

        TestEmptyObject success = new TestEmptyObject();
        success.empty = "value";
        success.obj = 5;

        TestEmptyObject fail = new TestEmptyObject();
        fail.empty = null;
        fail.obj = null;

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

    private static class TestEmptyObject {

        @NotEmptyRule
        private String empty;

        @NotEmptyRule
        private Object obj;

    }

}
