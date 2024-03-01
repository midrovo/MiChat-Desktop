package com.idrovo.michat.model;

import java.time.LocalDateTime;

public class Message {
    private int id;
    private User user1;
    private User user2;
    private LocalDateTime localDateTime;
    private String message;

    public Message(int id, User user1, User user2, LocalDateTime localDateTime, String message) {
        this.id = id;
        this.user1 = user1;
        this.user2 = user2;
        this.localDateTime = localDateTime;
        this.message = message;
    }

    public Message(User user1, User user2, LocalDateTime localDateTime, String message) {
        this.user1 = user1;
        this.user2 = user2;
        this.localDateTime = localDateTime;
        this.message = message;
    }

    public Message() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser1() {
        return user1;
    }

    public void setUser1(User user1) {
        this.user1 = user1;
    }

    public User getUser2() {
        return user2;
    }

    public void setUser2(User user2) {
        this.user2 = user2;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "{\"from\":" + user1 + ", \"to\":" + user2 + ", \"date\": \"" + localDateTime + "\", \"content\" :\"" + message + "\"}";
    }
}
