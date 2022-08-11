package nl.iobyte.framework.network.message.objects;

public class Message {

    private final String sender;
    private final MessagePayload payload;

    public Message(String sender, MessagePayload payload) {
        this.sender = sender;
        this.payload = payload;
    }

    public String getSender() {
        return sender;
    }

    public MessagePayload getPayload() {
        return payload;
    }

    public <T extends MessagePayload> T getPayload(Class<T> type) {
        return type.cast(payload);
    }

}
