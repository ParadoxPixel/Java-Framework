package nl.iobyte.framework.network.message;

import nl.iobyte.framework.generic.exceptional.ExceptionalFuture;
import nl.iobyte.framework.generic.invoker.enums.TaskType;
import nl.iobyte.framework.generic.serializer.SerializerService;
import nl.iobyte.framework.generic.serializer.objects.JsonSerializer;
import nl.iobyte.framework.generic.service.annotations.Inject;
import nl.iobyte.framework.generic.service.interfaces.Service;
import nl.iobyte.framework.network.message.adapter.MessagePayloadAdapter;
import nl.iobyte.framework.network.message.interfaces.IMessageHandler;
import nl.iobyte.framework.network.message.objects.Message;
import nl.iobyte.framework.network.message.objects.MessagePayload;
import nl.iobyte.framework.structures.pmap.PairMap;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class MessageService implements Service {

    @SuppressWarnings("rawtypes")
    private final PairMap<String, Class> classMapping = new PairMap<>(String.class, Class.class);
    private final Map<Class<?>, List<IMessageHandler>> handlers = new ConcurrentHashMap<>();

    @Inject
    private SerializerService serializerService;

    @Override
    public void start() {
        serializerService.get(JsonSerializer.class)
                         .registerTypeAdapter(
                                 MessagePayload.class,
                                 new MessagePayloadAdapter(this)
                         );
    }

    /**
     * Register mapping
     *
     * @param name  String
     * @param clazz Class<?>
     * @return MessageService
     */
    public MessageService map(String name, Class<?> clazz) {
        classMapping.set(name, clazz);
        return this;
    }

    /**
     * Get mapping for class
     *
     * @param clazz Class<?>
     * @return String
     */
    public String map(Class<?> clazz) {
        return classMapping.getRight(clazz);
    }

    /**
     * Get mapping for name
     *
     * @param name String
     * @return Class<?>
     */
    public Class<?> map(String name) {
        return classMapping.getLeft(name);
    }

    /**
     * Register handler for type
     *
     * @param type    Class<? extends MessagePayload>
     * @param handler IMessageHandler
     * @return MessageService
     */
    public MessageService on(Class<? extends MessagePayload> type, IMessageHandler handler) {
        handlers.computeIfAbsent(
                type,
                key -> new CopyOnWriteArrayList<>()
        ).add(handler);
        return this;
    }

    /**
     * Handle message
     *
     * @param msg Message
     * @return CompletableFuture<Void>
     */
    public CompletableFuture<Void> handle(Message msg) {
        return ExceptionalFuture.of(() -> {
            handleSync(msg);
            return (Void) null;
        }).schedule(TaskType.INTERNAL);
    }

    /**
     * Handle message sync
     *
     * @param msg Message
     * @throws Exception exception
     */
    public void handleSync(Message msg) throws Exception {
        if(!handlers.containsKey(msg.getPayload().getClass()))
            throw new Exception("no message handler found for type: " + msg.getPayload().getClass().getSimpleName());

        for(IMessageHandler handler : handlers.get(msg.getPayload().getClass()))
            handler.handle(msg);
    }

}
