package nl.iobyte.framework.data.model.objects.impl.database.objects;

import nl.iobyte.framework.FW;
import nl.iobyte.framework.data.database.DatabaseService;
import nl.iobyte.framework.data.database.interfaces.IDatabase;
import nl.iobyte.framework.data.model.interfaces.IModel;
import nl.iobyte.framework.data.model.interfaces.annotation.IModelAnnotationHandler;
import nl.iobyte.framework.data.model.objects.ModelWrapper;
import nl.iobyte.framework.data.model.objects.impl.database.annotations.Database;
import nl.iobyte.framework.data.model.objects.impl.database.objects.source.DatabaseModelSource;

public class DatabaseAnnotationHandler implements IModelAnnotationHandler<Database> {

    @Override
    public <T extends IModel> void handle(ModelWrapper<T> wrapper, Database annotation) {
        IDatabase database = FW.service(DatabaseService.class).get(annotation.id());
        if(database == null)
            throw new IllegalArgumentException("database with identity \"" + annotation.id() + "\" not found");

        String name = annotation.name();
        if(name == null || name.isEmpty() || name.isBlank())
            name = wrapper.getType().getSnakeCase();

        DatabaseModelSource<T> source = new DatabaseModelSource<>(
                name,
                wrapper,
                database
        );
        wrapper.addSource(source);
    }

}
