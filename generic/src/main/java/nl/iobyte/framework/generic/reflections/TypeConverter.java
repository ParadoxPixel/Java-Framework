package nl.iobyte.framework.generic.reflections;

import com.google.gson.Gson;
import com.google.gson.internal.LazilyParsedNumber;
import nl.iobyte.framework.FW;
import nl.iobyte.framework.generic.serializer.SerializerService;
import nl.iobyte.framework.generic.serializer.objects.JsonSerializer;

import java.util.Optional;

public class TypeConverter {

    private static final Gson gson = new Gson();

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

        Class<?> finalType = type;
        Object finalObj = obj;
        return Optional.ofNullable(FW.getInstance())
                       .map(fw -> fw.get(SerializerService.class))
                       .map(service -> service.get(JsonSerializer.class))
                       .map(serializer -> {
                           if(String.class.equals(finalType))
                               return new String(serializer.to(finalObj));

                           if(finalObj instanceof String str)
                               return serializer.from(str, finalType);

                           return serializer.from(serializer.toTree(finalObj), finalType);
                       }).orElseGet(() -> {
                    if(String.class.equals(finalType))
                        return gson.toJson(finalObj);

                    if(finalObj instanceof String str)
                        return gson.fromJson(str, finalType);

                    return gson.fromJson(gson.toJsonTree(finalObj), finalType);
                });
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
