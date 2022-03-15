package com.example.yashui;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;

@IgnoreExtraProperties
public class HomeData {
    private String phone;
    private String imageUrl;
    private Home home;
    private ArrayList<String> imagesClass;
    public ArrayList<String> getImagesClass() {
        return imagesClass;
    }

    public void setImagesClass(ArrayList<String> imagesClass) {
        this.imagesClass = imagesClass;
    }

    public HomeData() {
    }

    public HomeData(Home home, String imageUrl, ArrayList<String> imagesClass, String phone){
        this.home = home;
        this.imageUrl = imageUrl;
        this.imagesClass = imagesClass;
        this.phone = phone;
    }
    public HomeData(String phone, String imageUrl, Home home) {
        this.phone = phone;
        this.imageUrl = imageUrl;
        this.home = home;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public Home getHome() {
        return home;
    }
    public void setHome(Home home) {
        this.home = home;
    }
}