package com.example.yashui;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DatabaseStorageReference {
    private StorageReference storageReference;
    public DatabaseStorageReference(){
        this.storageReference = FirebaseStorage.getInstance().getReference();
    }
    public DatabaseStorageReference(String location){
        this.storageReference = FirebaseStorage.getInstance().getReference(location);
    }
    public StorageReference getStorageReference(){
        return  storageReference;
    }
    public StorageReference child(String path){
            return storageReference.child(path);
    }
}
