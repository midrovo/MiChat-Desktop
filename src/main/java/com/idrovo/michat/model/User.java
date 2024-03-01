package com.idrovo.michat.model;

public class User {
    private int id;
    private String name;
    private String lastaname;
    private String image;
    private String username;
    private String password;
    private boolean estado;

    public User(String name, String lastaname, String image, String username, String password) {
        this.name = name;
        this.lastaname = lastaname;
        this.image = image;
        this.username = username;
        this.password = password;
    }

    public User(int id, String image, String username, boolean estado) {
        this.id = id;
        this.image = image;
        this.username = username;
        this.estado = estado;
    }

    public User(String image, String username, boolean estado) {
        this.image = image;
        this.username = username;
        this.estado = estado;
    }

    public User(int id, String image, String username) {
        this.id = id;
        this.image = image;
        this.username = username;
    }

    public User(int id, String username) {
        this.id = id;
        this.username = username;
    }

    public User() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastaname() {
        return lastaname;
    }

    public void setLastaname(String lastaname) {
        this.lastaname = lastaname;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "{\"id\":" + id + ", \"userName\": \"" + username + "\"}";
    }
}
