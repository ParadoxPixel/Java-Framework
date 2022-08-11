package nl.iobyte.framework.generic.reflections;

import com.google.gson.Gson;
import com.google.gson.internal.LazilyParsedNumber;

public class TypeConverter {

    private static final Gson gson = new Gson();//TODO Use JsonSerializer from SerializerService

    /**
     * Normalise type
     *
     * @param type to normalise
     * @return normalised type
     */
    public static Class<?> normalise(Class<?> type) {
        if(byte.class.equals(type)) return Byte.class;

        if(char.class.equals(type)) return Character.class;

        if(boolean.class.equals(type)) return Boolean.class;

        if(short.class.equals(type)) return Short.class;

        if(int.class.equals(type)) return Integer.class;

        if(double.class.equals(type)) return Double.class;

        if(float.class.equals(type)) return Float.class;

        if(long.class.equals(type)) return Long.class;

        if(void.class.equals(type)) return Void.class;

        return type;
    }

    /**
     * Normalise object to type
     *
     * @param obj  to convert
     * @param type to convert to
     * @return converted object
     */
    public static Object normalise(Object obj, Class<?> type) {
        type = TypeConverter.normalise(type);
        if(type.isInstance(obj))
            return obj;
        
        if(obj instanceof byte[] bytes)
            obj = new String(bytes);

        if(obj instanceof String str) {
            if(Character.class.equals(type))
                return str.charAt(0);

            if(Boolean.class.equals(type))
                return str.equalsIgnoreCase("true") || str.equals("1");

            if(Number.class.isAssignableFrom(type))
                obj = new LazilyParsedNumber(str);
        }

        if(obj instanceof Number number) {
            if(Byte.class.equals(type))
                return number.byteValue();

            if(Short.class.equals(type))
                return number.shortValue();

            if(Boolean.class.equals(type))
                return number.intValue() == 1;

            if(Integer.class.equals(type))
                return number.intValue();

            if(Long.class.equals(type))
                return number.longValue();

            if(Double.class.equals(type))
                return number.doubleValue();

            if(Float.class.equals(type))
                return number.floatValue();
        }

        if(String.class.equals(type))
            return gson.toJson(obj);

        if(obj instanceof String str)
            return gson.fromJson(str, type);

        return gson.fromJson(gson.toJsonTree(obj), type);
    }

    /**
     * Check if normalised type a is assignable from normalised type b
     *
     * @param a type to assign to
     * @param b type to assign from
     * @return if type b can be assigned to type a
     */
    public static boolean isAssignable(Class<?> a, Class<?> b) {
        return normalise(a).isAssignableFrom(normalise(b));
    }

}
