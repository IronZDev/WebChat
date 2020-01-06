package com.mstokfisz.chatserver;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

@ServerEndpoint(value = "/chat", encoders = MessageEncoder.class, decoders = MessageDecoder.class)
public class ChatServer {
    private static ArrayList<User> userList;
    private static ArrayList<Message> messagesList;

    public ChatServer() {

        if (ChatServer.messagesList == null) {
            System.out.println("dupa");
            ChatServer.messagesList = new ArrayList<>();
        }
        if (ChatServer.userList == null) {
            ChatServer.userList = new ArrayList<>();
        }
    }

    /**
     * @OnOpen allows us to intercept the creation of a new session.
     * The session class allows us to send data to the user.
     * In the method onOpen, we'll let the user know that the handshake was
     * successful.
     */
    @OnOpen
    public void onOpen(Session session){
        System.out.println(session.getId() + " has opened a connection");
    }

    /**
     * When a user sends a message to the server, this method will intercept the message
     * and allow us to react to it. For now the message is read as a String.
     */
    @OnMessage
    public void onMessage(Message message, Session session) throws IOException, EncodeException {
        String userName = message.getUser();
        boolean userNameExists = false;
        for (User user : ChatServer.userList) {
            if (user.getName().equals(userName)) {
                userNameExists = true;
                if (message.getMessageType() == MessageType.CONNECT) { // Find a new name
                    if (user.getSession() != null) { // If another user is connected with that nick
                        int counter = 0;
                        boolean stillExists = true;
                        while (stillExists) {
                            stillExists = false;
                            for (User usr : ChatServer.userList) {
                                if (usr.getName().equals(userName+counter)) {
                                    stillExists = true;
                                    counter++;
                                }
                            }
                        }
                        message.setMessageContent(userName+counter); // Propose a new not taken username
                        session.getBasicRemote().sendObject(message);
                        return;
                    } else {
                        user.setSession(session);
                        for (Message oldMessage : ChatServer.messagesList) {
                            user.getSession().getBasicRemote().sendObject(oldMessage);
                        }
                        sendUserUpdate();
                    }
                } else if (message.getMessageType() == MessageType.WRITING) {
                    user.setWriting();
                    sendUserUpdate();
                } else if (message.getMessageType() == MessageType.STOP_WRITING || message.getMessageType() == MessageType.MESSAGE) {
                    user.setNonWriting();
                    sendUserUpdate();
                }
            }
        }
        if (!userNameExists) {
            User newUser = new User(userName, session);
            ChatServer.userList.add(newUser);
            for (Message oldMessage : ChatServer.messagesList) {
                newUser.getSession().getBasicRemote().sendObject(oldMessage);
            }
            sendUserUpdate();
        }
        if (message.getMessageContent() != null) {
            ChatServer.messagesList.add(message);
        }
        try {
            if (message.getMessageType() == MessageType.CONNECT || message.getMessageType() == MessageType.MESSAGE) sendMessageToAll(message); // Broadcast message
        } catch (IOException | EncodeException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * The user closes the connection.
     *
     * Note: you can't send messages to the client from this method
     */
    @OnClose
    public void onClose(Session session){
        for (User user : ChatServer.userList) {
            if (user.getSession().getId().equals(session.getId())) {
                user.removeSession();
                user.setNonWriting();
                try {
                    sendUserUpdate();
                    sendMessageToAll(new Message(new Date(), user.getName(), "", MessageType.DISCONNECT));
                } catch (IOException | EncodeException ex) {
                    ex.printStackTrace();
                }
                return;
            }
        }
        System.out.println("Session " +session.getId()+" has ended");
    }

    private void sendUserUpdate() throws IOException, EncodeException {
        for (User user: ChatServer.userList) {
            if (user.getSession() != null) {
                Message message = new Message(new Date(), "", "", MessageType.USER_UPDATE); // Send empty message to clear list
                user.getSession().getBasicRemote().sendObject(message);
                for (User userOther: ChatServer.userList) {
                    message.setUser(userOther.getName());
                    if (userOther.getSession() != null) {
                        message.setMessageContent(userOther.getIsWriting() ? "WRITING" : "ONLINE");
                    } else {
                        message.setMessageContent("OFFLINE");
                    }
                    user.getSession().getBasicRemote().sendObject(message);
                }
            }
        }
    }

    private void sendMessageToAll(Message message) throws IOException, EncodeException {
        for (User user: ChatServer.userList) {
            if (user.getSession() != null) {
                user.getSession().getBasicRemote().sendObject(message);
            }
        }
    }
}
