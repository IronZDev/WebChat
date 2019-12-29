package com.mstokfisz.chatserver;

import org.json.*;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import java.util.Date;

public class MessageDecoder implements Decoder.Text<Message> {
    @Override
    public Message decode(String stringMessage) throws DecodeException {
        System.out.println(stringMessage);
        JSONObject obj = new JSONObject(stringMessage);
        return new Message(new Date(), obj.has("user") ? obj.getString("user") : "", obj.has("message") ? obj.getString("message") : "", MessageType.valueOf(obj.getString("type")));
    }

    @Override
    public boolean willDecode(String s) {
        return true;
    }

    @Override
    public void init(EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy() {

    }
}
