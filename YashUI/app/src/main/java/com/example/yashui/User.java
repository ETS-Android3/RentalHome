package com.example.yashui;

import java.util.HashMap;

public class User {
    String email;
    String name;
    String profileImage;
    HashMap<String,String> HomeIds;

    public User() {
    }

    public User(String email, String name) {
        this.email = email;
        this.name = name;
    }

    public User(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public HashMap<String, String> getHomeIds() {
        return HomeIds;
    }

    public void setHomeIds(HashMap<String, String> homeIds) {
        HomeIds = homeIds;
    }
}
