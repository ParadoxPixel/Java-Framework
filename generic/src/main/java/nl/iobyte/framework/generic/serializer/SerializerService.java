package nl.iobyte.framework.generic.serializer;

import nl.iobyte.framework.generic.serializer.interfaces.ISerializer;
import nl.iobyte.framework.generic.serializer.objects.JavaObjectSerializer;
import nl.iobyte.framework.generic.serializer.objects.JsonSerializer;
import nl.iobyte.framework.generic.serializer.objects.YamlSerializer;
import nl.iobyte.framework.generic.service.interfaces.Service;
import nl.iobyte.framework.structures.cmap.ClassMap;

public class SerializerService extends ClassMap<ISerializer> implements Service {

    public SerializerService() {
        register(YamlSerializer.class);
        register(JsonSerializer.class);
        register(JavaObjectSerializer.class);
    }

}
