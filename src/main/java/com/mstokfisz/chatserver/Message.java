package com.mstokfisz.chatserver;

import java.util.Date;

public class Message {
    private Date date;
    private String user;
    private String messageContent;
    private MessageType messageType;

    public Message(Date date, String user, String content, MessageType messageType) {
        this.date = date;
        this.user = user;
        this.messageContent = content;
        this.messageType = messageType;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }
}
