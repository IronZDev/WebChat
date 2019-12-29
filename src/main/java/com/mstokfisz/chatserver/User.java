package com.mstokfisz.chatserver;

import javax.websocket.Session;

public class User {
    private String name;
    private Session session;
    private boolean isWriting = false;

    public User(String name, Session session) {
        this.name = name;
        this.session = session;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public void removeSession() {
        this.session = null;
    }

    public void setWriting() {
        isWriting = true;
    }

    public void setNonWriting() {
        isWriting = false;
    }

    public boolean getIsWriting() {
        return isWriting;
    }
}
