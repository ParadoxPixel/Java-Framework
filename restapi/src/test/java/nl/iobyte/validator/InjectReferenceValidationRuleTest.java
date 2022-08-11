package nl.iobyte.validator;

import nl.iobyte.framework.FW;
import nl.iobyte.framework.data.model.ModelService;
import nl.iobyte.framework.data.model.annotations.ModelKey;
import nl.iobyte.framework.data.model.interfaces.IModel;
import nl.iobyte.framework.data.model.interfaces.source.IModelSourceStrategy;
import nl.iobyte.framework.data.model.objects.impl.database.annotations.Column;
import nl.iobyte.framework.rest.validator.ValidatorService;
import nl.iobyte.framework.rest.validator.annotations.Validate;
import nl.iobyte.framework.rest.validator.objects.impl.reference.annotations.InjectReferenceRule;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

public class InjectReferenceValidationRuleTest {

    /**
     * Setup framework needs to test
     */
    private void setupFramework() {
        FW framework = FW.acquireInstance(() -> null, false);
        framework.registerBulk(
                ModelService.class,
                ValidatorService.class
        );

        framework.init();
        framework.start();

        framework.get(ModelService.class).register(TestModelObject.class);
        framework.get(ModelService.class)
                 .getOptionalWrapper(TestModelObject.class)
                 .ifPresent(model -> model.addSourceStrategy(new TestModelSourceStrategy()));
        framework.get(ValidatorService.class).register(TestObject.class);
    }

    @Test
    public void test() {
        setupFramework();

        TestModelObject model = new TestModelObject();
        model.uuid = UUID.randomUUID();
        model.name = "Random String";

        try {
            FW.service(ModelService.class).save(model);
        } catch(Exception e) {
            Assert.fail(e.getMessage());
        }

        TestObject success = new TestObject();
        success.uuid = model.uuid;

        TestObject fail = new TestObject();
        fail.uuid = null;

        ValidatorService service = FW.service(ValidatorService.class);
        try {
            Assert.assertTrue(service.test(success));
            Assert.assertNotNull(success.userModel);
        } catch(Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        try {
            Assert.assertFalse(service.test(fail));
            Assert.assertNull(fail.userModel);
            Assert.fail("test should fail");
        } catch(Exception ignored) {
        }
    }

    @Test
    public void testOptional() {
        setupFramework();

        TestModelObject model = new TestModelObject();
        model.uuid = UUID.randomUUID();
        model.name = "Random String";

        try {
            FW.service(ModelService.class).save(model);
        } catch(Exception e) {
            Assert.fail(e.getMessage());
        }

        TestOptionalObject success = new TestOptionalObject();
        success.uuid = model.uuid;

        ValidatorService service = FW.service(ValidatorService.class);
        try {
            Assert.assertTrue(service.test(success));
            Assert.assertNotNull(success.userModel);
        } catch(Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        success.uuid = null;
        try {
            Assert.assertTrue(service.test(success));
            Assert.assertNull(success.userModel);
        } catch(Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Validate
    private static class TestObject {

        @InjectReferenceRule(model = TestModelObject.class, modelField = "userModel")
        private UUID uuid;

        private TestModelObject userModel;

    }

    @Validate
    private static class TestOptionalObject {

        @InjectReferenceRule(model = TestModelObject.class, required = false, modelField = "userModel")
        private UUID uuid;

        private TestModelObject userModel;

    }

    private static class TestModelObject implements IModel {

        @ModelKey
        @Column(targetType = String.class)
        public UUID uuid;

        @Column
        public String name;

    }

    private static class TestModelSourceStrategy implements IModelSourceStrategy<TestModelObject> {

        private Object key;
        private TestModelObject model;

        @Override
        public boolean has(Object key) {
            return !(this.key == null || !this.key.equals(key));
        }

        @Override
        public TestModelObject get(Object key) {
            if(this.key == null || !this.key.equals(key))
                return null;

            return model;
        }

        @Override
        public void save(TestModelObject model) {
            this.key = model.uuid;
            this.model = model;
        }

        @Override
        public void delete(Object key) {
            this.key = null;
            model = null;
        }

    }

}
