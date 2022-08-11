package nl.iobyte.framework.data.model.objects;

import nl.iobyte.framework.data.model.interfaces.source.IModelSource;
import nl.iobyte.framework.generic.exceptional.ExceptionalFunction;
import nl.iobyte.framework.generic.exceptional.ExceptionalSupplier;
import nl.iobyte.framework.generic.reflections.components.ReflectedField;
import nl.iobyte.framework.structures.omap.interfaces.IObject;
import nl.iobyte.framework.structures.reflected.ReflectedMap;
import nl.iobyte.framework.structures.suppliers.IListSupplier;
import nl.iobyte.framework.structures.suppliers.IMapSupplier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelField implements IObject<String> {

    private final ReflectedField<?> field;
    private final IListSupplier listSupplier;

    private ExceptionalSupplier<?> supplier;
    private final Map<Class<? extends IModelSource>, List<ExceptionalFunction<Object, ?>>> loadTransformers, saveTransformers;

    public ModelField(ReflectedField<?> field) {
        this(field, ReflectedMap.getMapSupplier(HashMap.class), ArrayList::new);
    }

    public ModelField(ReflectedField<?> field, IMapSupplier mapSupplier, IListSupplier listSupplier) {
        this.field = field;
        this.listSupplier = listSupplier;

        this.loadTransformers = mapSupplier.get();
        this.saveTransformers = mapSupplier.get();
    }

    /**
     * Get name of field as identifier
     *
     * @return field name
     */
    public String getId() {
        return field.getSnakeCase();
    }

    /**
     * Get reflected field of model
     *
     * @return reflected field instance
     */
    public ReflectedField<?> getField() {
        return field;
    }

    /**
     * Get default value of field
     *
     * @return default value of field
     * @throws Exception thrown while getting default value
     */
    public Object onCreate() throws Exception {
        if(supplier == null)
            return null;

        return supplier.get();
    }

    /**
     * Add base value supplier
     *
     * @param supplier exceptional supplier of value for field
     */
    public void onCreate(ExceptionalSupplier<?> supplier) {
        this.supplier = supplier;
    }

    /**
     * Apply load transformers to loaded value from source
     *
     * @param sourceType type of source to get load transformers for
     * @param obj        object to apply transformers to
     * @return transformed object
     */
    public Object onLoad(Class<? extends IModelSource> sourceType, Object obj) {
        if(loadTransformers.containsKey(sourceType))
            for(ExceptionalFunction<Object, ?> function : loadTransformers.get(sourceType))
                obj = function.apply(obj);

        if(!IModelSource.class.equals(sourceType))
            obj = onLoad(IModelSource.class, obj);

        return obj;
    }

    /**
     * Add object transformer to model field for source type when loading model
     *
     * @param sourceType type of source to apply to
     * @param function   exceptional supplier of value for field
     */
    public void onLoad(Class<? extends IModelSource> sourceType, ExceptionalFunction<Object, ?> function) {
        loadTransformers.computeIfAbsent(
                sourceType,
                key -> listSupplier.get()
        ).add(function);
    }

    /**
     * Apply save transformers to loaded value from source
     *
     * @param sourceType type of source to get save transformers for
     * @param obj        object to apply transformers to
     * @return transformed object
     */
    public Object onSave(Class<? extends IModelSource> sourceType, Object obj) {
        if(!IModelSource.class.equals(sourceType))
            obj = onSave(IModelSource.class, obj);

        if(saveTransformers.containsKey(sourceType))
            for(ExceptionalFunction<Object, ?> function : saveTransformers.get(sourceType))
                obj = function.apply(obj);

        return obj;
    }

    /**
     * Add object transformer to model field for source type when saving model
     *
     * @param sourceType type of source to apply to
     * @param function   exceptional supplier of value for field
     */
    public void onSave(Class<? extends IModelSource> sourceType, ExceptionalFunction<Object, ?> function) {
        saveTransformers.computeIfAbsent(
                sourceType,
                key -> listSupplier.get()
        ).add(function);
    }

}
