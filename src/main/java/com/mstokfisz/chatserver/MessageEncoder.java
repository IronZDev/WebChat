package com.mstokfisz.chatserver;

import javax.json.Json;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class MessageEncoder implements Encoder.Text<Message>{

    @Override
    public String encode(Message messageObj) throws EncodeException {
        System.out.println(messageObj.getUser()+" "+messageObj.getMessageType()+" "+messageObj.getMessageContent());
        return Json.createObjectBuilder()
                .add("message", messageObj.getMessageContent() != null ? messageObj.getMessageContent() : "")
                .add("user", messageObj.getUser() != null ? messageObj.getUser() : "")
                .add("type", messageObj.getMessageType().name())
                .add("date", messageObj.getDate().toString()).build()
                .toString();
    }

    @Override
    public void init(EndpointConfig config) {

    }

    @Override
    public void destroy() {

    }
}
