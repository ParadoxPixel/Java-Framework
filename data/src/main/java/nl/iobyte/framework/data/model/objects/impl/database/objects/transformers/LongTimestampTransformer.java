package nl.iobyte.framework.data.model.objects.impl.database.objects.transformers;

import nl.iobyte.framework.data.model.objects.impl.database.interfaces.ITransformer;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class LongTimestampTransformer implements ITransformer {

    @Override
    public Object convertLeft(Object obj) {
        return Timestamp.from(Instant.ofEpochSecond((Long) obj));
    }

    @Override
    public Object convertRight(Object obj) {
        return TimeUnit.MILLISECONDS.toSeconds(((Timestamp) obj).getTime());
    }

}
