package nl.iobyte.framework.data.model.objects.impl.strategies.objects.handlers;

import nl.iobyte.framework.data.model.interfaces.IModel;
import nl.iobyte.framework.data.model.interfaces.annotation.IModelAnnotationHandler;
import nl.iobyte.framework.data.model.objects.ModelWrapper;
import nl.iobyte.framework.data.model.objects.impl.cache.objects.CacheModelSource;
import nl.iobyte.framework.data.model.objects.impl.database.objects.source.DatabaseModelSource;
import nl.iobyte.framework.data.model.objects.impl.strategies.annotations.CacheDatabaseStrategy;
import nl.iobyte.framework.data.model.objects.impl.strategies.objects.CacheDatabaseSourceStrategy;
import nl.iobyte.framework.data.model.objects.impl.strategies.objects.CacheSourceStrategy;
import nl.iobyte.framework.data.model.objects.impl.strategies.objects.DatabaseSourceStrategy;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

public class CacheDatabaseStrategyAnnotationHandler implements IModelAnnotationHandler<CacheDatabaseStrategy> {

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IModel> void handle(ModelWrapper<T> wrapper, CacheDatabaseStrategy annotation) {
        CacheModelSource<T> cacheSource = (CacheModelSource<T>) wrapper.getSource(CacheModelSource.class);
        DatabaseModelSource<T> databaseSource = (DatabaseModelSource<T>) wrapper.getSource(DatabaseModelSource.class);
        if(cacheSource == null && databaseSource == null)
            throw new IllegalArgumentException(
                    "model of type " +
                            wrapper.getType().getName() +
                            " has neither a valid cache or database"
            );

        Supplier<Boolean> bypass = () -> false;
        if(annotation.chance() > 0)
            bypass = () -> ThreadLocalRandom.current().nextInt(annotation.chance()) == 0;

        if(cacheSource != null && databaseSource != null) {
            wrapper.addSourceStrategy(new CacheDatabaseSourceStrategy<>(
                    cacheSource,
                    databaseSource,
                    wrapper,
                    bypass
            ));
            return;
        }

        if(cacheSource != null) {
            wrapper.addSourceStrategy(new CacheSourceStrategy<>(cacheSource));
            return;
        }

        wrapper.addSourceStrategy(new DatabaseSourceStrategy<>(databaseSource));
    }

}
