package nl.iobyte.reflections;

import nl.iobyte.framework.generic.reflections.builders.ReflectedBuilder;
import nl.iobyte.framework.generic.reflections.builders.ReflectiveBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class ReflectionBuildersTest {

    @Test
    public void testReflectiveBuilder() throws Exception {
        TestType testType = ReflectiveBuilder.of(TestType.class)
                                             .set("fieldA", "test")
                                             .set("fieldB", 1)
                                             .set("fieldC", 0.0D)
                                             .build();

        Assert.assertNotNull(testType);
    }

    @Test
    public void testReflectedBuilder() throws Exception {
        ReflectedBuilder<TestType> builder = ReflectedBuilder.of(TestType.class)
                                                             .addField("fieldA")
                                                             .addField("fieldB")
                                                             .addField("fieldC");

        TestType testType = builder.build(Map.of(
                "fieldA", "test",
                "fieldB", 1,
                "fieldC", 0.0D
        ));

        Assert.assertNotNull(testType);
    }

    protected static class TestType {

        public String fieldA;
        private int fieldB;
        protected Double fieldC;

    }

}
