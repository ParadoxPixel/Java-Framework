package nl.iobyte.framework.network.rabbitmq.objects.pool;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import stormpot.Allocator;
import stormpot.Slot;

public class ChannelAllocator implements Allocator<PoolableChannel> {

    private final ConnectionFactory factory;

    public ChannelAllocator(ConnectionFactory factory) {
        this.factory = factory;
    }

    @Override
    public PoolableChannel allocate(Slot slot) throws Exception {
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        return new PoolableChannel(slot, connection, channel);
    }

    @Override
    public void deallocate(PoolableChannel poolableChannel) throws Exception {
        poolableChannel.shutdown();
    }

}
