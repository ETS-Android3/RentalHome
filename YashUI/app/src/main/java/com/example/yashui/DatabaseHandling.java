package com.example.yashui;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseHandling {
    private String home_data_UUID;
    private final FirebaseAuth myAuth;
    private final DatabaseReference home_data;
    public DatabaseHandling() {
        this.home_data = FirebaseDatabase.getInstance().getReference().child("home_data");
        this.myAuth = FirebaseAuth.getInstance();
    }
    public String getPhoneNumber(){
        if (myAuth.getCurrentUser()!=null) {
            return myAuth.getCurrentUser().getPhoneNumber();
        }
        return null;
    }
    public DatabaseReference getHome_data(){
        return home_data;
    }
    public String generate_home_data_UUID(){
        home_data_UUID = home_data.push().getKey();
        return home_data_UUID;
    }
    public String get_home_UUID(){
        return  home_data_UUID;
    }

    public FirebaseAuth getMyAuth(){
        return myAuth;
    }
    public DatabaseReference get_home_data_child(String path){
        return home_data.child(path);
    }
    public FirebaseUser getCurrentUser(){
        return myAuth.getCurrentUser();
    }

}
