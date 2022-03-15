package com.example.yashui;

import android.net.Uri;

import java.util.HashMap;

public class Home {
    private String  address;
    private String country;
    private String state;
    private String city;
    private String typeOfHouse;
    private HashMap<String, String> images;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String description;
    private  float rent ,length,width;

    public Home(){

    }

    public HashMap<String, String> getImages() {
        return images;
    }

    public void setImages(HashMap<String, String> images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "Home{" +
                "address='" + address + '\'' +
                ", country='" + country + '\'' +
                ", state='" + state + '\'' +
                ", city='" + city + '\'' +
                ", images=" + images +
                ", description='" + description + '\'' +
                ", rent=" + rent +
                ", length=" + length +
                ", width=" + width +
                ", typeOfHouse=" + typeOfHouse +
                ", everyone=" + everyone +
                ", married=" + married +
                ", students=" + students +
                ", worker=" + worker +
                ", bachelors=" + bachelors +
                ", kitchen=" + kitchen +
                ", electricity=" + electricity +
                ", waterSupply=" + waterSupply +
                ", sanitary=" + sanitary +
                ", parking=" + parking +
                ", garden=" + garden +
                ", balcony=" + balcony +
                '}';
    }
    private boolean everyone,married,students,worker,bachelors,kitchen,electricity,waterSupply,sanitary,parking,garden,balcony;

    public String getAddress() {
        return address;
    }

    public String getCountry() {
        return country;
    }

    public String getState() {
        return state;
    }

    public String getCity() {
        return city;
    }

    public float getRent() {
        return rent;
    }

    public float getLength() {
        return length;
    }

    public float getWidth() {
        return width;
    }
    public String getTypeOfHouse() {
        return typeOfHouse;
    }

    public boolean isEveryone() {
        return everyone;
    }

    public boolean isMarried() {
        return married;
    }

    public boolean isStudents() {
        return students;
    }

    public boolean isWorker() {
        return worker;
    }

    public boolean isBachelors() {
        return bachelors;
    }

    public boolean isKitchen() {
        return kitchen;
    }

    public boolean isElectricity() {
        return electricity;
    }

    public boolean isWaterSupply() {
        return waterSupply;
    }

    public boolean isSanitary() {
        return sanitary;
    }

    public boolean isParking() {
        return parking;
    }

    public boolean isGarden() {
        return garden;
    }

    public boolean isBalcony() {
        return balcony;
    }

    public Home(String address, String country, String state, String city, float rent, float length, float width,
                String typeOfHouse, boolean everyone, boolean married, boolean students,
                boolean worker, boolean bachelors, boolean kitchen, boolean electricity, boolean waterSupply,
                boolean sanitary, boolean parking, boolean garden, boolean balcony,String description) {
        this.address = address;
        this.country = country;
        this.state = state;
        this.city = city;
        this.rent = rent;
        this.length = length;
        this.width = width;
        this.typeOfHouse = typeOfHouse;
        this.everyone = everyone;
        this.married = married;
        this.students = students;
        this.worker = worker;
        this.bachelors = bachelors;
        this.kitchen = kitchen;
        this.electricity = electricity;
        this.waterSupply = waterSupply;
        this.sanitary = sanitary;
        this.parking = parking;
        this.garden = garden;
        this.balcony = balcony;
        this.description = description;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setRent(float rent) {
        this.rent = rent;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setTypeOfHouse(String typeOfHouse) {
        this.typeOfHouse = typeOfHouse;
    }

    public void setEveryone(boolean everyone) {
        this.everyone = everyone;
    }

    public void setMarried(boolean married) {
        this.married = married;
    }

    public void setStudents(boolean students) {
        this.students = students;
    }

    public void setWorker(boolean worker) {
        this.worker = worker;
    }

    public void setBachelors(boolean bachelors) {
        this.bachelors = bachelors;
    }

    public void setKitchen(boolean kitchen) {
        this.kitchen = kitchen;
    }

    public void setElectricity(boolean electricity) {
        this.electricity = electricity;
    }

    public void setWaterSupply(boolean waterSupply) {
        this.waterSupply = waterSupply;
    }

    public void setSanitary(boolean sanitary) {
        this.sanitary = sanitary;
    }

    public void setParking(boolean parking) {
        this.parking = parking;
    }

    public void setGarden(boolean garden) {
        this.garden = garden;
    }

    public void setBalcony(boolean balcony) {
        this.balcony = balcony;
    }
}

