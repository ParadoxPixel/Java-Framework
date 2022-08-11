package nl.iobyte.framework.generic.config.objects;

import nl.iobyte.framework.generic.config.interfaces.IConfigSection;
import nl.iobyte.framework.generic.reflections.TypeConverter;

import java.lang.constant.Constable;
import java.util.*;

public class BaseConfigSection implements IConfigSection {

    private Map<String, Object> contents = new TreeMap<>();

    @Override
    public Object get(String path) {
        return get(path.split("\\."), 0);
    }

    /**
     * Get object at path
     *
     * @param path to object
     * @param i    index of path
     * @return object
     */
    protected Object get(String[] path, int i) {
        Object obj = contents.get(path[i]);
        if(obj == null)
            return null;

        if(obj instanceof BaseConfigSection section)
            if(i + 1 < path.length)
                return section.get(path, i + 1);

        if(i + 1 == path.length)
            return obj;

        return null;
    }

    @Override
    public <T> T getAs(String path, Class<T> type) {
        Object obj = get(path);
        if(obj == null)
            return null;

        //Transform object to type
        return type.cast(TypeConverter.normalise(obj, type));
    }

    @Override
    public void set(String path, Object value) {
        int lastIndex = path.lastIndexOf(".");
        String key = path.substring(lastIndex + 1);
        String[] parts = path.substring(0, lastIndex).split("\\.");

        set(parts, 0, key, value);
    }

    /**
     * Set object at path
     *
     * @param path  to section
     * @param i     index of path
     * @param key   of value
     * @param value object
     */
    protected void set(String[] path, int i, String key, Object value) {
        if(i == path.length) {
            if(value == null) {
                contents.remove(key);
                return;
            }

            if(!(value instanceof Constable)) {
                value = TypeConverter.normalise(value, TreeMap.class);

                Map<String, Object> map = transform((Map<?, ?>) value);
                if(map == null) {
                    contents.remove(key);
                    return;
                }

                BaseConfigSection section = new BaseConfigSection();
                section.setContents(map);
                value = section;
            }

            contents.put(key, value);
            return;
        }

        Object obj = contents.get(path[i]);
        BaseConfigSection section;
        if(!(obj instanceof BaseConfigSection)) {
            //Replace (existing) value for section
            section = new BaseConfigSection();
            section.setContents(new TreeMap<>());
            contents.put(path[i], section);
        } else {
            section = (BaseConfigSection) obj;
        }

        //continue set operation
        section.set(path, i + 1, key, value);
    }

    @Override
    public byte getByte(String path) {
        Object obj = get(path);
        if(obj == null)
            return 0;

        //Transform object to type
        return (byte) TypeConverter.normalise(obj, byte.class);
    }

    @Override
    public char getCharacter(String path) {
        Object obj = get(path);
        if(obj == null)
            return 0;

        //Transform object to type
        return (char) TypeConverter.normalise(obj, char.class);
    }

    @Override
    public String getString(String path) {
        return getAs(path, String.class);
    }

    @Override
    public short getShort(String path) {
        Object obj = get(path);
        if(obj == null)
            return 0;

        //Transform object to type
        return (short) TypeConverter.normalise(obj, short.class);
    }

    @Override
    public boolean getBoolean(String path) {
        Object obj = get(path);
        if(obj == null)
            return false;

        //Transform object to type
        return (boolean) TypeConverter.normalise(obj, boolean.class);
    }

    @Override
    public int getInteger(String path) {
        Integer i = getAs(path, Integer.class);
        if(i == null)
            return 0;

        return i;
    }

    @Override
    public long getLong(String path) {
        Long l = getAs(path, Long.class);
        if(l == null)
            return 0;

        return l;
    }

    @Override
    public double getDouble(String path) {
        Double d = getAs(path, Double.class);
        if(d == null)
            return 0;

        return d;
    }

    @Override
    public float getFloat(String path) {
        Float f = getAs(path, Float.class);
        if(f == null)
            return 0;

        return f;
    }

    @Override
    public Set<String> getKeys(String path) {
        IConfigSection section = getSection(path);
        if(section == null)
            return null;

        return section.getContents().keySet();
    }

    @Override
    public IConfigSection getSection(String path) {
        return getAs(path, IConfigSection.class);
    }

    /**
     * Get section contents
     *
     * @return contents
     */
    @Override
    public Map<String, Object> getContents() {
        TreeMap<String, Object> map = new TreeMap<>();
        for(Map.Entry<String, Object> entry : contents.entrySet()) {
            if(entry.getValue() instanceof BaseConfigSection section) {
                map.put(entry.getKey(), section.getContents());
                continue;
            }

            map.put(entry.getKey(), entry.getValue());
        }

        return map;
    }

    /**
     * Set map contents of section
     *
     * @param contents map
     */
    public void setContents(Map<String, Object> contents) {
        if(contents == null)
            throw new NullPointerException("cannot set empty contents");

        //Translate map values to sections
        translate(contents);

        this.contents = contents;
    }

    private static Map<String, Object> transform(Map<?, ?> m) {
        if(m.isEmpty())
            return null;

        //Verify map type
        Class<?> keyType = m.keySet().toArray()[0].getClass();
        Class<?> valueType = m.values().toArray()[0].getClass();
        if(!Object.class.isAssignableFrom(valueType))
            throw new IllegalArgumentException(
                    "invalid map value type " +
                            valueType.getSimpleName() +
                            " expected assignable to Object"
            );

        if(String.class.isAssignableFrom(keyType))
            //noinspection unchecked
            return (Map<String, Object>) m;

        TreeMap<String, Object> treeMap = new TreeMap<>();
        for(Map.Entry<?, ?> e : m.entrySet())
            treeMap.put(e.getKey().toString(), e.getValue());

        return treeMap;
    }

    /**
     * Translate map
     *
     * @param contents map
     */
    private static void translate(Map<String, Object> contents) {
        List<String> toRemove = new ArrayList<>();
        for(Map.Entry<String, Object> entry : contents.entrySet()) {
            if(entry.getValue() instanceof IConfigSection section)
                if(!(section instanceof BaseConfigSection))
                    entry.setValue(section.getContents());

            if(!(entry.getValue() instanceof Constable))
                entry.setValue(TypeConverter.normalise(
                        entry.getValue(),
                        TreeMap.class
                ));

            if(!(entry.getValue() instanceof Map<?, ?> m))
                continue;

            Map<String, Object> map = transform(m);
            if(map == null) {
                toRemove.add(entry.getKey());
                continue;
            }

            BaseConfigSection section = new BaseConfigSection();
            section.setContents(map);
            entry.setValue(section);
        }

        for(String key : toRemove)
            contents.remove(key);
    }

}
