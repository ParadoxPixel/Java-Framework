package nl.iobyte.framework.data.model.objects.impl.database.annotations;

import nl.iobyte.framework.data.model.annotations.ModelData;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.constant.Constable;

@ModelData
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {

    String name() default "";

    Class<?> targetType() default Constable.class;

}
