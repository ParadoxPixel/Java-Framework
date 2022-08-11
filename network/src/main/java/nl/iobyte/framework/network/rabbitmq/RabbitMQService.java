package nl.iobyte.framework.network.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import nl.iobyte.framework.generic.config.ConfigService;
import nl.iobyte.framework.generic.config.interfaces.IConfig;
import nl.iobyte.framework.generic.exceptional.ExceptionalFunction;
import nl.iobyte.framework.generic.exceptional.ExceptionalFuture;
import nl.iobyte.framework.generic.invoker.enums.TaskType;
import nl.iobyte.framework.generic.serializer.SerializerService;
import nl.iobyte.framework.generic.serializer.objects.JsonSerializer;
import nl.iobyte.framework.generic.service.annotations.Inject;
import nl.iobyte.framework.generic.service.interfaces.Service;
import nl.iobyte.framework.network.message.MessageService;
import nl.iobyte.framework.network.rabbitmq.objects.pool.ChannelAllocator;
import nl.iobyte.framework.network.rabbitmq.objects.pool.PoolableChannel;
import nl.iobyte.framework.network.rabbitmq.objects.receiver.ReceiverChannel;
import stormpot.Pool;
import stormpot.Timeout;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class RabbitMQService implements Service {

    private ReceiverChannel receiverChannel = null;
    private Pool<PoolableChannel> pool = null;

    @Inject
    private ConfigService configService;

    @Inject
    private SerializerService serializerService;

    @Inject
    private MessageService messageService;

    /**
     * Load RabbitMQ from config
     */
    public void start() {
        IConfig config = configService.get("rabbitmq");
        if(config == null)
            throw new IllegalStateException("unable to find configuration with id \"rabbitmq\"");

        ConnectionFactory factory = new ConnectionFactory();
        factory.setAutomaticRecoveryEnabled(true);
        factory.setHost(config.getString("host"));
        factory.setPort(config.getInteger("port"));
        factory.setUsername(config.getString("username"));
        factory.setPassword(config.getString("password"));

        receiverChannel = new ReceiverChannel(
                config.getString("queue"),
                factory,
                serializerService.get(JsonSerializer.class),
                messageService
        );

        ExceptionalFuture.of(() -> {
            //Setup service channel
            receiverChannel.initialize().join();

            this.pool = Pool.from(new ChannelAllocator(factory))
                            .setSize(config.getInteger("poolSize"))
                            .setBackgroundExpirationEnabled(true)
                            .build();

            return (Void) null;
        }).schedule(TaskType.EXTERNAL);
    }

    /**
     * Get service channel
     *
     * @return ServiceChannel
     */
    public ReceiverChannel getServiceChannel() {
        return receiverChannel;
    }

    /**
     * Call a function on a channel in pool with a timeout
     *
     * @param f       ExceptionalFunction<Channel,T>
     * @param timeout Long
     * @param unit    TimeUnit
     * @param <T>     T
     * @return CompletableFuture<T>
     */
    public <T> CompletableFuture<T> call(ExceptionalFunction<Channel, T> f, long timeout, TimeUnit unit) {
        return ExceptionalFuture.of(
                () -> callSync(f, timeout, unit)
        ).schedule(TaskType.EXTERNAL);
    }

    /**
     * Call a function on a channel in a pool with a timeout in sync
     *
     * @param f       ExceptionalFunction<Channel,T>
     * @param timeout Long
     * @param unit    TimeUnit
     * @param <T>     T
     * @return T
     * @throws Exception exception
     */
    public <T> T callSync(ExceptionalFunction<Channel, T> f, long timeout, TimeUnit unit) throws Exception {
        if(pool == null)
            return null;

        try(PoolableChannel channel = pool.claim(new Timeout(timeout, unit))) {
            //Local is needed due to concurrency issues
            @SuppressWarnings("UnnecessaryLocalVariable")
            T value = f.apply(channel.getChannel());

            /*channel.basicPublish(
                exchange,
                target_queue,
                null,
                body
            );*/

            return value;
        }
    }

    @Override
    public void stop() {
        try {
            ReceiverChannel receiverChannel = this.receiverChannel;
            this.receiverChannel = null;
            if(receiverChannel != null)
                receiverChannel.close();

            Pool<PoolableChannel> pool = this.pool;
            this.pool = null;
            if(pool != null) {
                pool.shutdown().await(new Timeout(
                        Long.MAX_VALUE,
                        TimeUnit.NANOSECONDS
                ));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
