package com.crisanto.kevin.picstant.models;

public class User {

    int id;
    String email, username, image;

    public User(int id, String email, String username, String image) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getImage() {
        return image;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
