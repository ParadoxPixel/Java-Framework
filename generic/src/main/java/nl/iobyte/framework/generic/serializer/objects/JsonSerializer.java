package nl.iobyte.framework.generic.serializer.objects;

import com.google.gson.*;
import com.google.gson.internal.bind.TreeTypeAdapter;
import com.google.gson.reflect.TypeToken;
import nl.iobyte.framework.generic.reflections.components.ReflectedField;
import nl.iobyte.framework.generic.reflections.objects.ReflectedType;
import nl.iobyte.framework.generic.serializer.interfaces.ISerializer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class JsonSerializer implements ISerializer {

    private final Gson gson;
    private final ReentrantLock lock = new ReentrantLock();

    public JsonSerializer() {
        gson = new GsonBuilder()
                .setObjectToNumberStrategy(ToNumberPolicy.LAZILY_PARSED_NUMBER)
                .create();
    }

    /**
     * Register adapter for type
     *
     * @param type    Type
     * @param adapter Object
     * @return JsonSerializer
     */
    public JsonSerializer registerTypeAdapter(Type type, Class<?> adapter) {
        try {
            return registerTypeAdapter(
                    type,
                    ReflectedType.of(adapter).getConstructor().newInstance()
            );
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Register adapter for type
     *
     * @param type    Type
     * @param adapter Object
     * @return JsonSerializer
     */
    public JsonSerializer registerTypeAdapter(Type type, Object adapter) {
        if(!(adapter instanceof com.google.gson.JsonSerializer || adapter instanceof JsonDeserializer))
            throw new IllegalArgumentException("adapter is of invalid type");

        ReflectedField<?> f = ReflectedType.of(Gson.class).getFieldByName("factories");
        if(f == null)
            throw new IllegalStateException("unable to find field \"factories\" in " + Gson.class.getName());

        lock.lock();
        try {
            @SuppressWarnings("unchecked")
            List<TypeAdapterFactory> factories = (List<TypeAdapterFactory>) f.getRawValue(gson);
            TypeToken<?> typeToken = TypeToken.get(type);
            List<TypeAdapterFactory> current = new ArrayList<>(factories);
            current.add(4, TreeTypeAdapter.newFactoryWithMatchRawType(typeToken, adapter));
            f.setRawValue(gson, Collections.unmodifiableList(current));
        } catch(Exception e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }

        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @param obj Object
     * @return Byte[]
     */
    public byte[] to(Object obj) {
        return gson.toJson(obj).getBytes(StandardCharsets.UTF_8);
    }

    /**
     * {@inheritDoc}
     *
     * @param bytes Byte[]
     * @param type  Class<T>
     * @param <T>   T
     * @return T
     */
    public <T> T from(byte[] bytes, Class<T> type) {
        return from(new ByteArrayInputStream(bytes), type);
    }

    /**
     * {@inheritDoc}
     *
     * @param stream InputStream
     * @param type   Class<T>
     * @param <T>    T
     * @return T
     */
    public <T> T from(InputStream stream, Class<T> type) {
        try {
            try(stream) {
                return gson.fromJson(
                        new InputStreamReader(stream),
                        type
                );
            }
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Convert object to different type
     *
     * @param obj  to convert
     * @param type of target object
     * @param <T>  type of target object
     * @return target object
     */
    public <T> T from(Object obj, Class<T> type) {
        return gson.fromJson(gson.toJsonTree(obj), type);
    }

}