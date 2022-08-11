package nl.iobyte.validator;

import nl.iobyte.framework.rest.validator.ValidatorService;
import nl.iobyte.framework.rest.validator.annotations.Validate;
import nl.iobyte.framework.rest.validator.exceptions.ValidatorFailException;
import nl.iobyte.framework.rest.validator.objects.Validator;
import nl.iobyte.framework.rest.validator.objects.impl.empty.annotations.NotEmptyRule;
import nl.iobyte.framework.rest.validator.objects.impl.range.annotations.FloatRangeRule;
import nl.iobyte.framework.rest.validator.objects.impl.range.annotations.IntRangeRule;
import nl.iobyte.framework.rest.validator.objects.impl.range.annotations.StringRangeRule;
import nl.iobyte.framework.rest.validator.objects.impl.range.enums.RangeType;
import org.junit.Assert;
import org.junit.Test;

public class ValidatorTest {

    @Test
    public void test() {
        ValidatorService service = new ValidatorService();

        service.register(TestObject.class);

        Validator<TestObject> validator = service.getValidator(TestObject.class);
        TestObject obj = new TestObject();
        obj.phrase = "hmm";
        obj.username = "John Doe";
        obj.port = 25566;
        obj.someMultiplier = 1.5F;

        try {
            Assert.assertTrue(validator.test(obj));
        } catch(Exception e) {
            Assert.fail(e.getMessage());
        }

        obj.phrase = null;
        obj.username = "test";
        obj.port = 25666;
        obj.someMultiplier = 0.5F;
        try {
            Assert.assertFalse(validator.test(obj));
            Assert.fail("test should have failed");
        } catch(Exception e) {
            if(!(e instanceof ValidatorFailException))
                Assert.fail(e.getMessage());
        }
    }

    @Validate
    protected static class TestObject {

        @NotEmptyRule
        public String phrase;

        @StringRangeRule(type = RangeType.RANGE, lower = 6, upper = 16)
        public String username;

        @IntRangeRule(type = RangeType.RANGE, lower = 25565, upper = 25665)
        public int port;

        @FloatRangeRule(type = RangeType.MIN, lower = 1F)
        public float someMultiplier;

    }

}
