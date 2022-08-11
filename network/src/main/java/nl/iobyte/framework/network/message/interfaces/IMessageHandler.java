package nl.iobyte.framework.network.message.interfaces;

import nl.iobyte.framework.network.message.objects.Message;

public interface IMessageHandler {

    /**
     * Handle message
     *
     * @param msg Message
     */
    void handle(Message msg) throws Exception;

}
