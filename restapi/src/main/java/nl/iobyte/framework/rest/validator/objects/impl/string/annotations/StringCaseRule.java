package nl.iobyte.framework.rest.validator.objects.impl.string.annotations;

import nl.iobyte.framework.rest.validator.annotations.ValidationRule;
import nl.iobyte.framework.rest.validator.objects.impl.string.enums.StringCase;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@ValidationRule
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface StringCaseRule {

    StringCase type();

    /**
     * Whether to check if value is in the specified case or transform it to said case
     */
    boolean transform() default true;

}
