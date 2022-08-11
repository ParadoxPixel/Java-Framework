package nl.iobyte.framework.rest.validator.objects.impl.empty.annotations;

import nl.iobyte.framework.rest.validator.annotations.ValidationRule;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@ValidationRule
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NotEmptyRule {

}
