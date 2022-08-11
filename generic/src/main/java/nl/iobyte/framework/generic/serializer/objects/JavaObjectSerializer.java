package nl.iobyte.framework.generic.serializer.objects;

import nl.iobyte.framework.generic.serializer.interfaces.ISerializer;

import java.io.*;

public class JavaObjectSerializer implements ISerializer {

    /**
     * {@inheritDoc}
     *
     * @param obj Object
     * @return Byte[]
     */
    @Override
    public byte[] to(Object obj) {
        try {
            try(ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
                try(ObjectOutputStream objectOutputStream = new ObjectOutputStream(stream)) {
                    objectOutputStream.writeObject(obj);
                }

                return stream.toByteArray();
            }
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param bytes byte array
     * @param type  of object
     * @param <T>   type
     * @return object of type
     */
    @Override
    public <T> T from(byte[] bytes, Class<T> type) {
        return from(new ByteArrayInputStream(bytes), type);
    }

    /**
     * {@inheritDoc}
     *
     * @param stream input stream
     * @param type   of object
     * @param <T>    type
     * @return object of type
     */
    @Override
    public <T> T from(InputStream stream, Class<T> type) {
        try {
            try(stream) {
                Object obj;
                try(ObjectInputStream objectInputStream = new ObjectInputStream(stream)) {
                    obj = objectInputStream.readObject();
                }

                return type.cast(obj);
            }
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

}
