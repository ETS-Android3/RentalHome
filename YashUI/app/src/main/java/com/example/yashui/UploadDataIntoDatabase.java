package com.example.yashui;

import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class UploadDataIntoDatabase implements TaskCompleted{
    private Uri imageUri;
    private String downloadUrl;
    private boolean isUploaded = false;
    private Object object;
    public UploadDataIntoDatabase(Object object,Uri imageUri) {
        this.imageUri = imageUri;
    }

    public UploadDataIntoDatabase(Object object) {
        this.object = object;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public String  getImageDownloadUrl() {
        return downloadUrl;
    }

    public void uploadTask(StorageReference storageReference) {
        UploadTask uploadtask = storageReference.putFile(imageUri);
        uploadtask.addOnSuccessListener(taskSnapshot -> Log.d("SUBMIT", "Inside onSuccess")).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("SUBMIT", "Inside onFailure");
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                Log.d("SUBMIT", "Inside onProgress");
            }
        }).continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            // Continue with the task to get the download URL
            return storageReference.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            Log.d("SUBMIT", "Inside addOnCompleteListener");
            if (task.isSuccessful()) {
                Log.d("SUBMIT", "Inside addOnCompleteListener iF STATEMENT "+task.getResult());
                downloadUrl = task.getResult().toString();
                isUploaded = true;
                onTaskCompleted();
                Log.d("SUBMIT", "Inside addOnCompleteListener iF STATEMENT "+downloadUrl);
            } else {
                // Handle failures
                // ...
            }
        });
    }

    @Override
    public void onTaskCompleted() {
        try {
           Method method =  object.getClass().getMethod("onTaskCompleted");
           Log.d("SUBMIT","isnide ontaskCompleted method of UploadDataIntoDatabase");
           method.invoke(object);
        }catch (NoSuchMethodException e){e.getMessage();} catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    public boolean isUploaded(){
        return  isUploaded;
    }
}
