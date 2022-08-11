package nl.iobyte.framework.data.model.objects.impl.snowflake.annotations;

import nl.iobyte.framework.data.model.annotations.ModelKey;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@ModelKey
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Snowflake {
}
