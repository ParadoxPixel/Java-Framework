package nl.iobyte.framework.data.model.objects.impl.snowflake.objects;

import nl.iobyte.framework.FW;
import nl.iobyte.framework.data.model.interfaces.annotation.IModelAnnotationFieldHandler;
import nl.iobyte.framework.data.model.objects.ModelField;
import nl.iobyte.framework.data.model.objects.ModelWrapper;
import nl.iobyte.framework.data.model.objects.impl.snowflake.annotations.Snowflake;
import nl.iobyte.framework.data.snowflake.SnowflakeService;

public class SnowflakeAnnotationFieldHandler implements IModelAnnotationFieldHandler<Snowflake> {

    @Override
    public void handle(ModelWrapper<?> wrapper, ModelField field, Snowflake annotation) {
        //Verify field type
        if(!field.getField().isAssignable(long.class))
            return;

        wrapper.onCreate(field.getField(), () -> FW.service(SnowflakeService.class).nextId());
    }

}
