package nl.iobyte.framework.data.model.objects.impl.database.annotations;

import nl.iobyte.framework.data.model.annotations.ModelSource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@ModelSource
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Database {

    String id();

    String name() default "";

}
