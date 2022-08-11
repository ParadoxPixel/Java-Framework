package nl.iobyte.framework.data.model.objects.impl.cache.annotations;

import nl.iobyte.framework.data.model.annotations.ModelSource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@ModelSource
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cache {

    String id();

    String name() default "";

    int timeout() default 0;

    TimeUnit unit() default TimeUnit.MINUTES;

}
