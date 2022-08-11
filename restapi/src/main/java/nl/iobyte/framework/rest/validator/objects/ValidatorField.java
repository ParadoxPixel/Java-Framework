package nl.iobyte.framework.rest.validator.objects;

import nl.iobyte.framework.generic.reflections.components.ReflectedField;
import nl.iobyte.framework.rest.validator.exceptions.ValidationRuleFailException;
import nl.iobyte.framework.rest.validator.interfaces.IValidationRule;
import nl.iobyte.framework.structures.omap.interfaces.IObject;
import nl.iobyte.framework.structures.suppliers.IListSupplier;

import java.util.ArrayList;
import java.util.List;

public class ValidatorField<T> implements IObject<String> {

    private final ReflectedField<T> field;
    private final List<IValidationRule<T>> predicates;

    public ValidatorField(ReflectedField<T> field) {
        this(field, ArrayList::new);
    }

    public ValidatorField(ReflectedField<T> field, IListSupplier listSupplier) {
        this.field = field;
        this.predicates = listSupplier.get();
    }

    /**
     * Get name of field as identifier
     *
     * @return field name
     */
    public String getId() {
        return field.getSnakeCase();
    }

    /**
     * Get reflected field of model
     *
     * @return reflected field instance
     */
    public ReflectedField<T> getField() {
        return field;
    }

    /**
     * Add predicate to run on test
     *
     * @param predicate exceptional predicate
     */
    public void onTest(IValidationRule<T> predicate) {
        predicates.add(predicate);
    }

    /**
     * Test object
     *
     * @param obj object to test
     * @return whether predicates passed
     * @throws ValidationRuleFailException thrown when validation rule fails due to returning false or an exception is thrown
     */
    public boolean test(Object instance, T obj) throws ValidationRuleFailException {
        for(IValidationRule<T> predicate : predicates) {
            try {
                if(!predicate.test(instance, obj))
                    throw new ValidationRuleFailException(predicate, obj);

                return true;
            } catch(Exception e) {
                throw new ValidationRuleFailException(predicate, obj, e);
            }
        }

        return true;
    }

    /**
     * Test raw object by typecasting
     *
     * @param obj raw object to test
     * @return whether predicates passed
     * @throws Exception ClassCast, ValidationRuleFail or other exception thrown while testing
     */
    public boolean testRaw(Object instance, Object obj) throws Exception {
        //noinspection unchecked
        return test(instance, (T) obj);
    }

}
