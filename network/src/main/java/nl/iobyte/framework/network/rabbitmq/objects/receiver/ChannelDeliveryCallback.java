package nl.iobyte.framework.network.rabbitmq.objects.receiver;

import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import nl.iobyte.framework.generic.exceptional.ExceptionalFuture;
import nl.iobyte.framework.generic.invoker.enums.TaskType;
import nl.iobyte.framework.generic.serializer.objects.JsonSerializer;
import nl.iobyte.framework.network.message.MessageService;
import nl.iobyte.framework.network.message.objects.Message;

public class ChannelDeliveryCallback implements DeliverCallback {

    private final JsonSerializer jsonSerializer;
    private final MessageService messageService;

    public ChannelDeliveryCallback(JsonSerializer jsonSerializer, MessageService messageService) {
        this.jsonSerializer = jsonSerializer;
        this.messageService = messageService;
    }

    @Override
    public void handle(String s, Delivery delivery) {
        ExceptionalFuture.of(() -> jsonSerializer.from(delivery.getBody(), Message.class))
                         .schedule(TaskType.INTERNAL)
                         .thenAccept(
                                 msg -> messageService.handle(msg)
                                                      .exceptionally(e -> {
                                                          e.printStackTrace();
                                                          return null;
                                                      })
                         ).exceptionally(e -> {
                             e.printStackTrace();
                             return null;
                         });
    }

}
