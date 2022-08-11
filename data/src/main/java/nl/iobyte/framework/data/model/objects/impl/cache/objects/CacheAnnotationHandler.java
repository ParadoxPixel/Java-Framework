package nl.iobyte.framework.data.model.objects.impl.cache.objects;

import nl.iobyte.framework.FW;
import nl.iobyte.framework.data.cache.CacheService;
import nl.iobyte.framework.data.cache.interfaces.ICache;
import nl.iobyte.framework.data.model.interfaces.IModel;
import nl.iobyte.framework.data.model.interfaces.annotation.IModelAnnotationHandler;
import nl.iobyte.framework.data.model.objects.ModelWrapper;
import nl.iobyte.framework.data.model.objects.impl.cache.annotations.Cache;

public class CacheAnnotationHandler implements IModelAnnotationHandler<Cache> {

    @Override
    public <T extends IModel> void handle(ModelWrapper<T> wrapper, Cache annotation) {
        ICache cache = FW.service(CacheService.class).get(annotation.id());
        if(cache == null)
            throw new IllegalArgumentException("cache with identity \"" + annotation.id() + "\" not found");

        String name = annotation.name();
        if(name == null || name.isEmpty() || name.isBlank())
            name = wrapper.getType().getSnakeCase();

        CacheModelSource<T> source = new CacheModelSource<>(
                name,
                wrapper,
                cache,
                annotation.timeout(),
                annotation.unit()
        );
        wrapper.addSource(source);
    }

}
