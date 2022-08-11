package nl.iobyte.framework.data.model.objects;

import nl.iobyte.framework.data.model.interfaces.IModel;
import nl.iobyte.framework.data.model.interfaces.source.IModelSource;
import nl.iobyte.framework.data.model.interfaces.source.IModelSourceStrategy;
import nl.iobyte.framework.generic.exceptional.ExceptionalFunction;
import nl.iobyte.framework.generic.exceptional.ExceptionalSupplier;
import nl.iobyte.framework.generic.reflections.components.ReflectedConstructor;
import nl.iobyte.framework.generic.reflections.components.ReflectedField;
import nl.iobyte.framework.generic.reflections.objects.ReflectedType;
import nl.iobyte.framework.structures.cmap.ClassMap;
import nl.iobyte.framework.structures.omap.ObjectMap;
import nl.iobyte.framework.structures.omap.interfaces.IObject;
import nl.iobyte.framework.structures.reflected.ReflectedMap;
import nl.iobyte.framework.structures.suppliers.IListSupplier;
import nl.iobyte.framework.structures.suppliers.IMapSupplier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ModelWrapper<T extends IModel> extends ObjectMap<String, ModelField> implements IObject<Class<? extends IModel>> {

    private final ReflectedType<T> type;
    private final ReflectedConstructor<T> constructor;
    private final ClassMap<IModelSource> sources;
    private final ClassMap<IModelSourceStrategy<T>> sourceStrategies;
    private ReflectedField<?> keyField;

    private final IMapSupplier mapSupplier;
    private final IListSupplier listSupplier;

    public ModelWrapper(ReflectedType<T> type) {
        this(type, ReflectedMap.getMapSupplier(HashMap.class), ArrayList::new);
    }

    public ModelWrapper(ReflectedType<T> type, IMapSupplier mapSupplier, IListSupplier listSupplier) {
        super(mapSupplier);
        this.type = type;
        try {
            this.constructor = type.getConstructor();
        } catch(Exception e) {
            throw new RuntimeException(e);
        }

        this.sources = new ClassMap<>(mapSupplier);
        this.sourceStrategies = new ClassMap<>(mapSupplier);

        this.mapSupplier = mapSupplier;
        this.listSupplier = listSupplier;
    }

    @Override
    public Class<? extends IModel> getId() {
        return type.getType();
    }

    /**
     * Get reflected type of model
     *
     * @return reflected type instance
     */
    public ReflectedType<T> getType() {
        return type;
    }

    /**
     * Get field holding model's key
     *
     * @return reflected field instance
     */
    public ReflectedField<?> getKeyField() {
        return keyField;
    }

    /**
     * Set which field holds model's key
     *
     * @param keyField reflected field instance
     */
    public void setKeyField(ReflectedField<?> keyField) {
        this.keyField = keyField;
    }

    /**
     * Add field to model wrapper
     *
     * @param field reflected field instance
     */
    public ModelField addField(ReflectedField<?> field) {
        return computeIfAbsent(
                field.getSnakeCase(),
                key -> new ModelField(
                        field,
                        mapSupplier,
                        listSupplier
                )
        );
    }

    /**
     * Get sources belonging to model
     *
     * @return collection of sources
     */
    public Collection<IModelSource> getSources() {
        return sources.values();
    }

    /**
     * Add source to model
     *
     * @param source to add
     */
    public void addSource(Class<? extends IModelSource> source) {
        sources.register(source);
    }

    /**
     * Add source to model
     *
     * @param source to add
     */
    public void addSource(IModelSource source) {
        sources.register(source);
    }

    /**
     * Get source from model by type
     *
     * @param type of source
     * @param <R>  type
     * @return source instance
     */
    public <R extends IModelSource> R getSource(Class<R> type) {
        return sources.get(type, true);
    }

    /**
     * Get source strategies belonging to model
     *
     * @return collection of source strategies
     */
    public Collection<IModelSourceStrategy<T>> getSourceStrategies() {
        return sourceStrategies.values();
    }

    /**
     * Add source strategy to model
     *
     * @param sourceStrategy to add
     */
    public void addSourceStrategy(Class<? extends IModelSourceStrategy<T>> sourceStrategy) {
        sourceStrategies.register(sourceStrategy);
    }

    /**
     * Add source strategy to model
     *
     * @param sourceStrategy to add
     */
    public void addSourceStrategy(IModelSourceStrategy<T> sourceStrategy) {
        sourceStrategies.register(sourceStrategy);
    }

    /**
     * Get source strategy from model by type
     *
     * @param type of source strategy
     * @param <R>  type
     * @return source strategy instance
     */
    public <R extends IModelSourceStrategy<T>> R getSourceStrategy(Class<R> type) {
        return sourceStrategies.get(type, true);
    }

    /**
     * Get new model instance
     *
     * @return model instance
     * @throws Exception thrown while getting new instance or acquiring/setting base values
     */
    public T create() throws Exception {
        T instance = constructor.newInstance();
        for(ModelField field : values()) {
            Object obj = field.onCreate();
            if(obj == null)
                continue;

            field.getField().setRawValue(instance, obj);
        }

        return instance;
    }

    /**
     * Add base value supplier for reflected field
     *
     * @param field    reflected field instance
     * @param supplier exceptional supplier of value for field
     */
    public void onCreate(ReflectedField<?> field, ExceptionalSupplier<?> supplier) {
        getOptional(field.getSnakeCase()).ifPresent(modelField -> modelField.onCreate(supplier));
    }

    /**
     * Load model from map and source type
     *
     * @param sourceType type of source to get load transformers for
     * @param model      field -> object mapping
     */
    public T load(Class<? extends IModelSource> sourceType, Map<String, Object> model) throws Exception {
        T instance = constructor.newInstance();

        ModelField modelField;
        for(Map.Entry<String, Object> entry : model.entrySet()) {
            modelField = get(entry.getKey());
            if(modelField == null)
                continue;

            Object obj = modelField.onLoad(sourceType, entry.getValue());
            if(obj == null)
                continue;

            modelField.getField().setRawValue(instance, obj);
        }

        return instance;
    }

    /**
     * Add object transformer to model field when loading model
     *
     * @param field      reflected field instance
     * @param sourceType type of source to get load transformers for
     * @param function   exceptional supplier of value for field
     */
    public void onLoad(
            ReflectedField<?> field,
            Class<? extends IModelSource> sourceType,
            ExceptionalFunction<Object, ?> function
    ) {
        getOptional(field.getSnakeCase()).ifPresent(modelField -> modelField.onLoad(sourceType, function));
    }

    /**
     * Get field -> object mapping from model for source type
     *
     * @param sourceType type of source to get save transformers for
     * @param model      instance of model to transform
     * @return field -> object mapping
     * @throws Exception thrown while acquiring/transforming field values
     */
    public Map<String, Object> save(Class<? extends IModelSource> sourceType, T model) throws Exception {
        Map<String, Object> map = new HashMap<>();
        for(ModelField field : values())
            map.put(
                    field.getId(),
                    field.onSave(
                            sourceType,
                            field.getField().getRawValue(model)
                    )
            );

        return map;
    }

    /**
     * Add object transformer to model field when saving model
     *
     * @param field      reflected field instance
     * @param sourceType type of source to get load transformers for
     * @param function   exceptional supplier of value for field
     */
    public void onSave(
            ReflectedField<?> field,
            Class<? extends IModelSource> sourceType,
            ExceptionalFunction<Object, ?> function
    ) {
        getOptional(field.getSnakeCase()).ifPresent(modelField -> modelField.onSave(sourceType, function));
    }

}
