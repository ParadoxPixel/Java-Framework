package nl.iobyte.framework.data.model.interfaces.annotation;

import nl.iobyte.framework.data.model.objects.ModelField;
import nl.iobyte.framework.data.model.objects.ModelWrapper;

import java.lang.annotation.Annotation;

public interface IModelAnnotationFieldHandler<T extends Annotation> {

    /**
     * Handle annotation for model
     *
     * @param wrapper    model wrapper instance
     * @param field      reflected field instance
     * @param annotation instance of annotation
     */
    void handle(ModelWrapper<?> wrapper, ModelField field, T annotation);

    /**
     * Type cast annotation
     *
     * @param wrapper    model wrapper instance
     * @param field      reflected field instance
     * @param annotation raw instance of annotation
     */
    default void handleRaw(ModelWrapper<?> wrapper, ModelField field, Annotation annotation) {
        T obj;
        try {
            //noinspection unchecked
            obj = (T) annotation;
        } catch(Exception e) {
            throw new RuntimeException(e);
        }

        handle(wrapper, field, obj);
    }

}
