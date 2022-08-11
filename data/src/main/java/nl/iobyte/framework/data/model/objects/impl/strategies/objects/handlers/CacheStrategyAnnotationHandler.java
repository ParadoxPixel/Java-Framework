package nl.iobyte.framework.data.model.objects.impl.strategies.objects.handlers;

import nl.iobyte.framework.data.model.interfaces.IModel;
import nl.iobyte.framework.data.model.interfaces.annotation.IModelAnnotationHandler;
import nl.iobyte.framework.data.model.objects.ModelWrapper;
import nl.iobyte.framework.data.model.objects.impl.cache.objects.CacheModelSource;
import nl.iobyte.framework.data.model.objects.impl.strategies.annotations.CacheStrategy;
import nl.iobyte.framework.data.model.objects.impl.strategies.objects.CacheSourceStrategy;

public class CacheStrategyAnnotationHandler implements IModelAnnotationHandler<CacheStrategy> {

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IModel> void handle(ModelWrapper<T> wrapper, CacheStrategy annotation) {
        CacheModelSource<T> cacheSource = (CacheModelSource<T>) wrapper.getSource(CacheModelSource.class);
        if(cacheSource == null)
            throw new IllegalArgumentException("model of type " + wrapper.getType().getName() + " has no valid cache");

        wrapper.addSourceStrategy(new CacheSourceStrategy<>(cacheSource));
    }

}
