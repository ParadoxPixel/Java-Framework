package nl.iobyte.reflections;

import nl.iobyte.framework.generic.reflections.components.ReflectedConstructor;
import nl.iobyte.framework.generic.reflections.components.ReflectedField;
import nl.iobyte.framework.generic.reflections.components.ReflectedMethod;
import nl.iobyte.framework.generic.reflections.objects.ReflectedType;
import org.junit.Assert;
import org.junit.Test;

public class ReflectionComponentsTest {

    @Test
    public void testType() throws Exception {
        ReflectedType<TestType> type = ReflectedType.of(TestType.class);

        //Test get field
        Assert.assertNotNull(type.getFieldByType(String.class));
        Assert.assertNotNull(type.getFieldByName("fieldB"));
        Assert.assertNotNull(type.getField("fieldC", double.class));

        //Test get method
        Assert.assertNotNull(type.getMethodByReturnType(boolean.class));
        Assert.assertNotNull(type.getMethodByName("methodB"));
        Assert.assertNotNull(type.getMethod(void.class, double.class));

        //Test get constructor
        Assert.assertNotNull(type.getConstructor());
        Assert.assertNotNull(type.getConstructor(String.class));
        Assert.assertNotNull(type.getConstructor(String.class, int.class));
        Assert.assertNotNull(type.getConstructor("test", 1));
    }

    @Test
    public void testField() throws Exception {
        ReflectedType<TestType> type = ReflectedType.of(TestType.class);
        ReflectedField<String> field = type.getFieldByType(String.class);

        TestType testType = new TestType();

        //Test set
        field.setRawValue(testType, "test");
        field.setValue(testType, "test");

        //Test get
        Assert.assertEquals("test", field.getRawValue(testType));
        Assert.assertEquals("test", field.getValue(testType));
    }

    @Test
    public void testMethod() throws Exception {
        ReflectedType<TestType> type = ReflectedType.of(TestType.class);
        ReflectedMethod<Boolean> methodA = type.getMethodByReturnType(boolean.class);

        TestType testType = new TestType();

        //Test invoke without parameters
        Assert.assertEquals(false, methodA.invokeRaw(testType));
        Assert.assertEquals(false, methodA.invoke(testType));

        ReflectedMethod<Void> methodB = type.getMethod(Void.class, double.class);

        //Test invoke with parameters
        Assert.assertNull(methodB.invokeRaw(testType, 0.0D));
        Assert.assertNull(methodB.invoke(testType, 0.0D));
    }

    @Test
    public void testConstructor() throws Exception {
        ReflectedType<TestType> type = ReflectedType.of(TestType.class);

        //Test empty constructor
        ReflectedConstructor<TestType> constructor = type.getConstructor();
        Assert.assertNotNull(constructor.newInstance());

        //Test parameter type constructor
        constructor = type.getConstructor(String.class);
        Assert.assertNotNull(constructor.newInstance("test"));

        //Test parameter objects constructor
        constructor = type.getConstructor("test", 0);
        Assert.assertNotNull(constructor.newInstance("test", 0));
    }

    protected static class TestType {

        public String fieldA;
        private Integer fieldB;

        protected Double fieldC;

        public TestType() {}

        private TestType(String fieldA) {
            this.fieldA = fieldA;
        }

        protected TestType(String fieldA, int fieldB) {
            this.fieldA = fieldA;
            this.fieldB = fieldB;
        }

        public Boolean methodA() {
            return false;
        }

        private Integer methodB() {
            return fieldB;
        }

        protected void methodC(Double amount) {}

    }

}
