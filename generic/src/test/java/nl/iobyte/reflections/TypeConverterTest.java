package nl.iobyte.reflections;

import nl.iobyte.framework.generic.reflections.TypeConverter;
import org.junit.Assert;
import org.junit.Test;

public class TypeConverterTest {

    @Test
    public void testNormaliseType() {
        //Test java type shorts
        assertTypeConversion(Byte.class, byte.class);
        assertTypeConversion(Character.class, char.class);
        assertTypeConversion(Boolean.class, boolean.class);
        assertTypeConversion(Short.class, short.class);
        assertTypeConversion(Integer.class, int.class);
        assertTypeConversion(Double.class, double.class);
        assertTypeConversion(Float.class, float.class);
        assertTypeConversion(Long.class, long.class);

        //Test regular types
        Assert.assertEquals(String.class, TypeConverter.normalise(String.class));
        Assert.assertEquals(Object.class, TypeConverter.normalise(Object.class));
    }

    /**
     * Check if type conversion works
     *
     * @param a normalised type
     * @param b regular type
     */
    public void assertTypeConversion(Class<?> a, Class<?> b) {
        Assert.assertEquals(a, TypeConverter.normalise(a));
        Assert.assertEquals(a, TypeConverter.normalise(b));
    }

}
