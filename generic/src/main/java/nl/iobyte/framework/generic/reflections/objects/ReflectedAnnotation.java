package nl.iobyte.framework.generic.reflections.objects;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Optional;

public class ReflectedAnnotation extends AbstractReflected {

    private final AnnotatedElement type;

    public ReflectedAnnotation(AnnotatedElement type) {
        this.type = type;
    }

    @Override
    public String getName() {
        return type.getClass().getSimpleName();
    }

    /**
     * Check if an annotation of type is present
     *
     * @param annotation type of annotation
     * @return if annotation is present
     */
    public boolean hasAnnotation(Class<? extends Annotation> annotation) {
        return type.isAnnotationPresent(annotation);
    }

    /**
     * Get annotation of type
     *
     * @param annotation type of annotation
     * @param <T>        type of annotation
     * @return annotation instance
     */
    public <T extends Annotation> T getAnnotation(Class<T> annotation) {
        return type.getAnnotation(annotation);
    }

    public Annotation[] getAnnotations() {
        return type.getAnnotations();
    }

    /**
     * Get optional annotation of type
     *
     * @param annotation type of annotation
     * @param <T>        type of annotation
     * @return optional of annotation instance
     */
    public <T extends Annotation> Optional<T> getOptionalAnnotation(Class<T> annotation) {
        return Optional.ofNullable(getAnnotation(annotation));
    }

    /**
     * Get reflected annotation instance from element
     *
     * @param element annotated element instance
     * @return reflected annotation instance
     */
    public static ReflectedAnnotation of(AnnotatedElement element) {
        return new ReflectedAnnotation(element);
    }

}
