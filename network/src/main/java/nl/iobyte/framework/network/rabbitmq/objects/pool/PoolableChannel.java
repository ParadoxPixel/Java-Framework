package nl.iobyte.framework.network.rabbitmq.objects.pool;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import stormpot.BasePoolable;
import stormpot.Slot;

public class PoolableChannel extends BasePoolable implements AutoCloseable {

    private final Connection connection;
    private final Channel channel;

    public PoolableChannel(Slot slot, Connection connection, Channel channel) {
        super(slot);
        this.connection = connection;
        this.channel = channel;
    }

    public Channel getChannel() {
        return channel;
    }

    @Override
    public void close() {
        if(channel.isOpen()) {
            release();
        } else {
            expire();
        }
    }

    public void shutdown() throws Exception {
        channel.close();
        connection.close();
    }

}
