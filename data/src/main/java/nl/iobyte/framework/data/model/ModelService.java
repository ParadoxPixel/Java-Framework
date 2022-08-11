package nl.iobyte.framework.data.model;

import nl.iobyte.framework.data.model.annotations.ModelData;
import nl.iobyte.framework.data.model.annotations.ModelKey;
import nl.iobyte.framework.data.model.annotations.ModelSource;
import nl.iobyte.framework.data.model.annotations.ModelSourceStrategy;
import nl.iobyte.framework.data.model.interfaces.IModel;
import nl.iobyte.framework.data.model.interfaces.annotation.IModelAnnotationFieldHandler;
import nl.iobyte.framework.data.model.interfaces.annotation.IModelAnnotationHandler;
import nl.iobyte.framework.data.model.interfaces.source.IModelSource;
import nl.iobyte.framework.data.model.interfaces.source.IModelSourceStrategy;
import nl.iobyte.framework.data.model.objects.ModelField;
import nl.iobyte.framework.data.model.objects.ModelWrapper;
import nl.iobyte.framework.data.model.objects.impl.cache.annotations.Cache;
import nl.iobyte.framework.data.model.objects.impl.cache.objects.CacheAnnotationHandler;
import nl.iobyte.framework.data.model.objects.impl.database.annotations.Column;
import nl.iobyte.framework.data.model.objects.impl.database.annotations.Database;
import nl.iobyte.framework.data.model.objects.impl.database.objects.ColumnAnnotationFieldHandler;
import nl.iobyte.framework.data.model.objects.impl.database.objects.DatabaseAnnotationHandler;
import nl.iobyte.framework.data.model.objects.impl.snowflake.annotations.Snowflake;
import nl.iobyte.framework.data.model.objects.impl.snowflake.objects.SnowflakeAnnotationFieldHandler;
import nl.iobyte.framework.data.model.objects.impl.strategies.annotations.CacheDatabaseStrategy;
import nl.iobyte.framework.data.model.objects.impl.strategies.annotations.CacheStrategy;
import nl.iobyte.framework.data.model.objects.impl.strategies.annotations.DatabaseStrategy;
import nl.iobyte.framework.data.model.objects.impl.strategies.objects.handlers.CacheDatabaseStrategyAnnotationHandler;
import nl.iobyte.framework.data.model.objects.impl.strategies.objects.handlers.CacheStrategyAnnotationHandler;
import nl.iobyte.framework.data.model.objects.impl.strategies.objects.handlers.DatabaseStrategyAnnotationHandler;
import nl.iobyte.framework.generic.reflections.objects.ReflectedType;
import nl.iobyte.framework.generic.service.interfaces.Service;
import nl.iobyte.framework.structures.omap.ObjectMap;
import nl.iobyte.framework.structures.reflected.ReflectedMap;
import nl.iobyte.framework.structures.suppliers.IMapSupplier;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.*;

public class ModelService extends ObjectMap<Class<? extends IModel>, ModelWrapper<?>> implements Service {

    private final Map<Class<? extends Annotation>, IModelAnnotationHandler<?>> annotationHandlers;
    private final Map<Class<? extends Annotation>, IModelAnnotationFieldHandler<?>> annotationFieldHandlers;

    public ModelService() {
        this(ReflectedMap.getMapSupplier(HashMap.class));
    }

    public ModelService(IMapSupplier supplier) {
        annotationHandlers = supplier.get();
        annotationFieldHandlers = supplier.get();

        //Register cache annotations
        on(Cache.class, new CacheAnnotationHandler());

        //Register database annotations
        on(Database.class, new DatabaseAnnotationHandler());
        on(Column.class, new ColumnAnnotationFieldHandler());

        //Register snowflake annotations
        on(Snowflake.class, new SnowflakeAnnotationFieldHandler());

        //Register source strategy annotations
        on(CacheDatabaseStrategy.class, new CacheDatabaseStrategyAnnotationHandler());
        on(CacheStrategy.class, new CacheStrategyAnnotationHandler());
        on(DatabaseStrategy.class, new DatabaseStrategyAnnotationHandler());
    }

    /**
     * Get model wrapper of type
     *
     * @param type of model
     * @param <T>  type of model
     * @return model wrapper instance
     */
    public <T extends IModel> ModelWrapper<T> getWrapper(Class<T> type) {
        //noinspection unchecked
        return (ModelWrapper<T>) get(type);
    }

    /**
     * Get optional model wrapper of type
     *
     * @param type of model
     * @param <T>  type of model
     * @return optional of model wrapper instance
     */
    public <T extends IModel> Optional<ModelWrapper<T>> getOptionalWrapper(Class<T> type) {
        return Optional.ofNullable(getWrapper(type));
    }

    /**
     * Get source strategy from model
     *
     * @param model  type
     * @param source type
     * @param <T>    type of source strategy
     * @return source strategy instance
     */
    public <T extends IModelSource> T getSource(Class<? extends IModel> model, Class<T> source) {
        return getOptionalWrapper(model)
                .map(wrapper -> wrapper.getSource(source))
                .orElse(null);
    }

    /**
     * Get first source strategy from model
     *
     * @param model type
     * @param <T>   type of model
     * @return source strategy instance
     */
    public <T extends IModel> IModelSourceStrategy<T> getSourceStrategy(Class<T> model) {
        return getOptionalWrapper(model)
                .map(ModelWrapper::getSourceStrategies)
                .flatMap(sourceStrategies -> sourceStrategies.stream().findFirst())
                .orElse(null);
    }

    /**
     * Get source strategy from model
     *
     * @param model          type
     * @param sourceStrategy type
     * @param <T>            type of source strategy
     * @return source strategy instance
     */
    public <T extends IModel, R extends IModelSourceStrategy<T>> R getSourceStrategy(
            Class<T> model,
            Class<R> sourceStrategy
    ) {
        return getOptionalWrapper(model)
                .map(wrapper -> wrapper.getSourceStrategy(sourceStrategy))
                .orElse(null);
    }

    /**
     * Add handler for annotation
     *
     * @param type              of annotation
     * @param annotationHandler handler
     * @param <T>               type of annotation
     */
    public <T extends Annotation> void on(Class<T> type, IModelAnnotationHandler<T> annotationHandler) {
        annotationHandlers.put(type, annotationHandler);
    }

    /**
     * Get annotation handler by type
     *
     * @param type of annotation
     * @param <T>  annotation type
     * @return model annotation handler instance
     */
    public <T extends Annotation> IModelAnnotationHandler<T> getAnnotationHandler(Class<T> type) {
        //noinspection unchecked
        return (IModelAnnotationHandler<T>) annotationHandlers.get(type);
    }

    /**
     * Add handler for annotation field
     *
     * @param type                   of annotation
     * @param annotationFieldHandler handler
     * @param <T>                    type of annotation
     */
    public <T extends Annotation> void on(Class<T> type, IModelAnnotationFieldHandler<T> annotationFieldHandler) {
        annotationFieldHandlers.put(type, annotationFieldHandler);
    }

    /**
     * Get annotation field handler by type
     *
     * @param type of annotation
     * @param <T>  annotation type
     * @return model annotation field handler instance
     */
    public <T extends Annotation> IModelAnnotationFieldHandler<T> getAnnotationFieldHandler(Class<T> type) {
        //noinspection unchecked
        return (IModelAnnotationFieldHandler<T>) annotationFieldHandlers.get(type);
    }

    /**
     * Parse model of type
     *
     * @param type reflected type instance
     * @param <T>  type of model
     * @return model wrapper
     */
    public <T extends IModel> ModelWrapper<T> register(Class<T> type) {
        if(containsKey(type))
            //noinspection unchecked
            return (ModelWrapper<T>) get(type);

        ReflectedType<T> reflectedType = ReflectedType.of(type);
        ModelWrapper<T> wrapper = new ModelWrapper<>(reflectedType);

        //Handle source annotations
        Arrays.stream(reflectedType.getAnnotations())
              .filter(annotation -> hasAnnotationOfType(
                      annotation.annotationType(),
                      ModelSource.class
              )).forEach(annotation -> {
                  IModelAnnotationHandler<?> handler = getAnnotationHandler(annotation.annotationType());
                  if(handler == null)
                      return;

                  handler.handleRaw(wrapper, annotation);
              });

        //Handle source strategy annotations
        Arrays.stream(reflectedType.getAnnotations())
              .filter(annotation -> hasAnnotationOfType(
                      annotation.annotationType(),
                      ModelSourceStrategy.class
              )).forEach(annotation -> {
                  IModelAnnotationHandler<?> handler = getAnnotationHandler(annotation.annotationType());
                  if(handler == null)
                      return;

                  handler.handleRaw(wrapper, annotation);
              });

        //Handle model fields
        reflectedType.getFields()
                     .stream()
                     .filter(field -> {
                         for(Annotation annotation : field.getAnnotations())
                             if(hasAnnotationOfType(annotation.annotationType(), ModelData.class))
                                 return true;

                         return false;
                     })
                     .forEach(field -> {
                         ModelField modelField = wrapper.addField(field);
                         for(Annotation annotation : field.getAnnotations()) {
                             if(hasAnnotationOfType(annotation.annotationType(), ModelKey.class))
                                 wrapper.setKeyField(field);

                             if(!hasAnnotationOfType(annotation.annotationType(), ModelData.class))
                                 continue;

                             IModelAnnotationFieldHandler<?> handler = getAnnotationFieldHandler(annotation.annotationType());
                             if(handler == null)
                                 continue;

                             handler.handleRaw(wrapper, modelField, annotation);
                         }
                     });

        put(wrapper);
        return wrapper;
    }

    /**
     * Get model by key
     */
    public <T extends IModel> T get(Class<T> model, Object key) throws Exception {
        IModelSourceStrategy<T> sourceStrategy = getSourceStrategy(model);
        if(sourceStrategy == null)
            throw new IllegalArgumentException(
                    "no source strategy found for model " +
                            model.getSimpleName() +
                            " while trying to get model"
            );

        return sourceStrategy.get(key);
    }

    /**
     * Save model
     */
    public <T extends IModel> void save(T model) throws Exception {
        //noinspection unchecked
        IModelSourceStrategy<T> sourceStrategy = getSourceStrategy((Class<T>) model.getClass());
        if(sourceStrategy == null)
            throw new IllegalArgumentException(
                    "no source strategy found for model " +
                            model.getClass().getSimpleName() +
                            " while trying to save model"
            );

        sourceStrategy.save(model);
    }

    /**
     * Delete model by key
     *
     * @param model type
     * @param key   of model
     * @throws Exception thrown while deleting model
     */
    public <T extends IModel> void delete(Class<T> model, Object key) throws Exception {
        IModelSourceStrategy<T> sourceStrategy = getSourceStrategy(model);
        if(sourceStrategy == null)
            throw new IllegalArgumentException(
                    "no source strategy found for model " +
                            model.getSimpleName() +
                            " while trying to delete model"
            );

        sourceStrategy.delete(key);
    }

    /**
     * Check if annotation has annotation
     *
     * @param type           of annotation
     * @param annotationType annotation type to check for
     * @return whether annotation has annotation
     */
    private static boolean hasAnnotationOfType(
            Class<? extends Annotation> type,
            Class<? extends Annotation> annotationType
    ) {
        if(type.equals(annotationType))
            return true;

        for(Annotation annotation : type.getAnnotations()) {
            if(ignoredAnnotations.contains(annotation.annotationType()))
                continue;

            if(hasAnnotationOfType(annotation.annotationType(), annotationType))
                return true;
        }

        return false;
    }

    /**
     * List of annotations to ignore in hasAnnotationOfType
     */
    private static final Set<Class<? extends Annotation>> ignoredAnnotations = Set.of(
            Documented.class,
            Retention.class,
            Target.class
    );

}
