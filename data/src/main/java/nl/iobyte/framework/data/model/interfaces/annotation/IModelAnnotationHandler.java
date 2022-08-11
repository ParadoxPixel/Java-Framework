package nl.iobyte.framework.data.model.interfaces.annotation;

import nl.iobyte.framework.data.model.interfaces.IModel;
import nl.iobyte.framework.data.model.objects.ModelWrapper;

import java.lang.annotation.Annotation;

public interface IModelAnnotationHandler<T extends Annotation> {

    /**
     * Handle annotation for model
     *
     * @param wrapper    model wrapper instance
     * @param annotation instance of annotation
     */
    <R extends IModel> void handle(ModelWrapper<R> wrapper, T annotation);

    /**
     * Type cast annotation
     *
     * @param wrapper    model wrapper instance
     * @param annotation raw instance of annotation
     */
    default void handleRaw(ModelWrapper<?> wrapper, Annotation annotation) {
        T obj;
        try {
            //noinspection unchecked
            obj = (T) annotation;
        } catch(Exception e) {
            throw new RuntimeException(e);
        }

        handle(wrapper, obj);
    }

}
