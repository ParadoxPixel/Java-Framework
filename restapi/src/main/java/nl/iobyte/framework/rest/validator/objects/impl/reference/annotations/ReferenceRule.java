package nl.iobyte.framework.rest.validator.objects.impl.reference.annotations;

import nl.iobyte.framework.data.model.interfaces.IModel;
import nl.iobyte.framework.rest.validator.annotations.ValidationRule;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@ValidationRule
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ReferenceRule {

    Class<? extends IModel> model();

    boolean required() default true;

}
