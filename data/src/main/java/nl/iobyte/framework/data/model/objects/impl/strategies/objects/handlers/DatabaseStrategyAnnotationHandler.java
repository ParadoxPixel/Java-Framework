package nl.iobyte.framework.data.model.objects.impl.strategies.objects.handlers;

import nl.iobyte.framework.data.model.interfaces.IModel;
import nl.iobyte.framework.data.model.interfaces.annotation.IModelAnnotationHandler;
import nl.iobyte.framework.data.model.objects.ModelWrapper;
import nl.iobyte.framework.data.model.objects.impl.database.objects.source.DatabaseModelSource;
import nl.iobyte.framework.data.model.objects.impl.strategies.annotations.DatabaseStrategy;
import nl.iobyte.framework.data.model.objects.impl.strategies.objects.DatabaseSourceStrategy;

public class DatabaseStrategyAnnotationHandler implements IModelAnnotationHandler<DatabaseStrategy> {

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IModel> void handle(ModelWrapper<T> wrapper, DatabaseStrategy annotation) {
        DatabaseModelSource<T> cacheSource = (DatabaseModelSource<T>) wrapper.getSource(DatabaseModelSource.class);
        if(cacheSource == null)
            throw new IllegalArgumentException("model of type " + wrapper.getType().getName() + " has no valid cache");

        wrapper.addSourceStrategy(new DatabaseSourceStrategy<>(cacheSource));
    }

}
