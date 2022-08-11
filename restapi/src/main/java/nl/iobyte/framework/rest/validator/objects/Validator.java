package nl.iobyte.framework.rest.validator.objects;

import nl.iobyte.framework.generic.reflections.components.ReflectedField;
import nl.iobyte.framework.generic.reflections.objects.ReflectedType;
import nl.iobyte.framework.rest.validator.exceptions.ValidationRuleFailException;
import nl.iobyte.framework.rest.validator.exceptions.ValidatorFailException;
import nl.iobyte.framework.structures.omap.ObjectMap;
import nl.iobyte.framework.structures.omap.interfaces.IObject;
import nl.iobyte.framework.structures.reflected.ReflectedMap;
import nl.iobyte.framework.structures.suppliers.IListSupplier;
import nl.iobyte.framework.structures.suppliers.IMapSupplier;

import java.util.ArrayList;
import java.util.HashMap;

public class Validator<T> extends ObjectMap<String, ValidatorField<?>> implements IObject<Class<?>> {

    private final ReflectedType<T> type;
    private final IListSupplier listSupplier;

    public Validator(ReflectedType<T> type) {
        this(type, ReflectedMap.getMapSupplier(HashMap.class), ArrayList::new);
    }

    public Validator(ReflectedType<T> type, IMapSupplier mapSupplier, IListSupplier listSupplier) {
        super(mapSupplier);
        this.type = type;
        this.listSupplier = listSupplier;
    }

    @Override
    public Class<?> getId() {
        return type.getType();
    }

    /**
     * Get reflected type of model
     *
     * @return reflected type instance
     */
    public ReflectedType<T> getType() {
        return type;
    }

    /**
     * Add field to validator
     *
     * @param field reflected field instance
     */
    public <R> ValidatorField<R> addField(ReflectedField<R> field) {
        //noinspection unchecked
        return (ValidatorField<R>) computeIfAbsent(
                field.getSnakeCase(),
                key -> new ValidatorField<>(
                        field,
                        listSupplier
                )
        );
    }

    /**
     * Test object
     *
     * @param obj to test
     * @return whether object passes tests
     */
    public boolean test(T obj) throws Exception {
        for(ValidatorField<?> field : values()) {
            try {
                Object value = field.getField().getRawValue(obj);
                field.testRaw(obj, value);
            } catch(ValidationRuleFailException e) {
                throw new ValidatorFailException(obj, e);
            }
        }

        return true;
    }

}
