package nl.iobyte.framework.network.message.adapter;

import com.google.gson.*;
import nl.iobyte.framework.network.message.MessageService;
import nl.iobyte.framework.network.message.objects.MessagePayload;

import java.lang.reflect.Type;

public class MessagePayloadAdapter implements JsonSerializer<MessagePayload>, JsonDeserializer<MessagePayload> {

    private final MessageService service;

    public MessagePayloadAdapter(MessageService service) {
        this.service = service;
    }

    @Override
    public JsonElement serialize(MessagePayload src, Type type, JsonSerializationContext ctx) {
        //Get name of payload from type
        String name = service.map(src.getClass());

        //Create object
        JsonObject result = new JsonObject();
        result.add("type", new JsonPrimitive(name));
        result.add("data", ctx.serialize(src, src.getClass()));

        return result;
    }

    @Override
    public MessagePayload deserialize(JsonElement element, Type type, JsonDeserializationContext ctx) throws
            JsonParseException {
        JsonObject object = element.getAsJsonObject();

        //Get type of payload
        Class<?> clazz = service.map(object.get("type").getAsString());
        if(clazz == null)
            throw new JsonParseException("unknown payload type of: " + object.get("type").getAsString());

        //Deserialize type
        JsonElement payload = object.get("data");
        return ctx.deserialize(payload, clazz);
    }

}
