package com.thomas.firebasechatapp.Models;

public class User {
    String username, id, imageUrl, status;

    public User() {
    }

    public User(String username, String id, String imageUrl, String status) {
        this.username = username;
        this.id = id;
        this.imageUrl = imageUrl;
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
