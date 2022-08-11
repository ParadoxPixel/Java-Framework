package nl.iobyte.framework.network.rabbitmq.objects.receiver;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import nl.iobyte.framework.generic.exceptional.ExceptionalFuture;
import nl.iobyte.framework.generic.invoker.enums.TaskType;
import nl.iobyte.framework.generic.serializer.objects.JsonSerializer;
import nl.iobyte.framework.network.message.MessageService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class ReceiverChannel implements AutoCloseable {

    private final String queue;
    private final ConnectionFactory factory;
    private final JsonSerializer jsonSerializer;
    private final MessageService messageService;

    private final AtomicReference<Connection> connection = new AtomicReference<>(null);
    private final AtomicReference<Channel> channel = new AtomicReference<>(null);
    private final AtomicReference<String> consumerId = new AtomicReference<>(null);

    public ReceiverChannel(String queue,
                           ConnectionFactory factory,
                           JsonSerializer jsonSerializer,
                           MessageService messageService) {
        this.queue = queue;
        this.factory = factory;

        this.jsonSerializer = jsonSerializer;
        this.messageService = messageService;
    }

    /**
     * Initialize service channel
     *
     * @return CompletableFuture<Void>
     */
    public CompletableFuture<Void> initialize() {
        return ExceptionalFuture.of(() -> {
            //Connection
            Connection connection = this.connection.get();
            if(connection == null) {
                connection = factory.newConnection();
                this.connection.set(connection);
            }

            //Channel
            Channel channel = this.channel.get();
            if(channel == null) {
                channel = connection.createChannel();
                this.channel.set(channel);
            }

            if(consumerId.get() != null)
                throw new IllegalStateException("channel already initialized");

            //Create queue
            channel.queueDeclare(
                    queue,
                    false,
                    false,
                    true,
                    null
            );

            //Declare consumer
            String id = channel.basicConsume(
                    queue,
                    false,
                    new ChannelDeliveryCallback(jsonSerializer, messageService),
                    consumerTag -> {}
            );
            consumerId.set(id);

            return (Void) null;
        }).schedule(TaskType.EXTERNAL);
    }

    /**
     * Bind to exchange with topic
     *
     * @param exchange String
     * @param topic    String
     * @return CompletableFuture<AMQP.Queue.BindOk>
     */
    public CompletableFuture<AMQP.Queue.BindOk> bind(String exchange, String topic) {
        return ExceptionalFuture.of(
                () -> channel.get().queueBind(
                        queue,
                        exchange,
                        topic
                )
        ).schedule(TaskType.EXTERNAL);
    }

    /**
     * Unbind from exchange with topic
     *
     * @param exchange String
     * @param topic    String
     * @return CompletableFuture<AMQP.Queue.UnbindOk>
     */
    public CompletableFuture<AMQP.Queue.UnbindOk> unbind(String exchange, String topic) {
        return ExceptionalFuture.of(
                () -> channel.get().queueUnbind(
                        queue,
                        exchange,
                        topic
                )
        ).schedule(TaskType.EXTERNAL);
    }

    @Override
    public void close() throws Exception {
        Connection connection = this.connection.getAndSet(null);
        Channel channel = this.channel.getAndSet(null);

        if(channel != null) {
            String id = consumerId.getAndSet(null);
            if(id != null && !id.isEmpty())
                channel.basicCancel(id);

            channel.close();
        }

        if(connection != null)
            connection.close();
    }

}
