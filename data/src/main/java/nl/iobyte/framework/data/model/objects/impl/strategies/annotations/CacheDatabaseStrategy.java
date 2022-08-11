package nl.iobyte.framework.data.model.objects.impl.strategies.annotations;

import nl.iobyte.framework.data.model.annotations.ModelSourceStrategy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@ModelSourceStrategy
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheDatabaseStrategy {

    int chance() default 0;

}
