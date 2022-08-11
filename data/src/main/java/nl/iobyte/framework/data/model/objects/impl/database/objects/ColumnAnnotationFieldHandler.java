package nl.iobyte.framework.data.model.objects.impl.database.objects;

import nl.iobyte.framework.data.model.interfaces.annotation.IModelAnnotationFieldHandler;
import nl.iobyte.framework.data.model.objects.ModelField;
import nl.iobyte.framework.data.model.objects.ModelWrapper;
import nl.iobyte.framework.data.model.objects.impl.database.annotations.Column;
import nl.iobyte.framework.data.model.objects.impl.database.interfaces.ITransformer;
import nl.iobyte.framework.data.model.objects.impl.database.objects.source.DatabaseModelSource;
import nl.iobyte.framework.data.model.objects.impl.database.objects.transformers.LongTimestampTransformer;
import nl.iobyte.framework.generic.reflections.TypeConverter;
import nl.iobyte.framework.structures.bimap.BiMap;

import java.lang.constant.Constable;
import java.sql.Timestamp;

public class ColumnAnnotationFieldHandler implements IModelAnnotationFieldHandler<Column> {

    //Database type transformers
    private static final BiMap<Class<?>, Class<?>, ITransformer> transformer = new BiMap<>() {{
        putPair(Long.class, Timestamp.class, new LongTimestampTransformer());
    }};

    @Override
    public void handle(ModelWrapper<?> wrapper, ModelField field, Column annotation) {
        if(!annotation.name().isEmpty() && !annotation.name().isBlank())
            wrapper.getSource(
                    DatabaseModelSource.class
            ).addColumnMapping(field.getId(), annotation.name());

        if(annotation.targetType().isAssignableFrom(field.getField().getRawType()))
            return;

        if(Constable.class.equals(annotation.targetType()))
            return;

        //Add load transformers
        wrapper.onLoad(
                field.getField(),
                DatabaseModelSource.class,
                obj -> {
                    if(obj == null)
                        return null;

                    ITransformer itf = transformer.getPair(
                            TypeConverter.normalise(field.getField().getRawType()),
                            annotation.targetType()
                    );
                    if(itf == null)
                        return TypeConverter.normalise(obj, field.getField().getRawType());

                    return itf.convertRight(obj);
                }
        );

        //Add save transformers
        wrapper.onSave(
                field.getField(),
                DatabaseModelSource.class,
                obj -> {
                    if(obj == null)
                        return null;

                    ITransformer itf = transformer.getPair(
                            TypeConverter.normalise(field.getField().getRawType()),
                            annotation.targetType()
                    );
                    if(itf == null)
                        return TypeConverter.normalise(obj, annotation.targetType());

                    return itf.convertLeft(obj);
                }
        );
    }

}
