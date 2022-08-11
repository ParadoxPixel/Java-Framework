package nl.iobyte.framework.rest.validator.objects.impl.range.annotations;

import nl.iobyte.framework.rest.validator.annotations.ValidationRule;
import nl.iobyte.framework.rest.validator.objects.impl.range.enums.RangeType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@ValidationRule
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IntRangeRule {

    RangeType type() default RangeType.RANGE;

    int lower() default 0;

    int upper() default 0;

}
