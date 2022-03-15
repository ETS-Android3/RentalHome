package com.example.yashui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Fade;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity implements TaskCompleted  {
    private FloatingActionButton selectProfile;
    private ImageView profilePic;
    private String phoneNumber;
    private CircularProgressIndicator circularProgressIndicator;
    private UploadDataIntoDatabase uploadDataIntoDatabase;
    private static final int CAMERA_REQUEST = 100;
    private static final int STORAGE_REQUEST = 200;
    private String[] cameraPermission;
    private String[] storagePermission;
    private MaterialToolbar registerToolBar;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initiateView();
        String imageUrl = getIntent().getExtras().getString("IMAGE_URL");
        if (imageUrl !=null){
            setDp(imageUrl);
        }
        phoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        registerToolBar = findViewById(R.id.profile_toolbar);
        setSupportActionBar(registerToolBar);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Fade fade = new Fade();
        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);

    }
    private void initiateView(){
        selectProfile = findViewById(R.id.select_image);
        profilePic = findViewById(R.id.register_profile_image);
        // allowing permissions of gallery and camera
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        circularProgressIndicator = findViewById(R.id.profile_progressIndicator);
    }
    @Override
    public void onResume(){
        super.onResume();
        selectProfile.show();
        selectProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkInternet()) {
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                        requestStoragePermission();
                    } else {
                        pickFromGallery();
                    }
                }
            }
        });
    }
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        selectProfile.hide();
        if (item.getItemId() == android.R.id.home) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finishAfterTransition();
                }
            }, 100);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        selectProfile.hide();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finishAfterTransition();
            }
        },100);

    }

    // checking storage permissions
//    private Boolean checkStoragePermission() {
//        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
//        return result;
//    }

    // Requesting  gallery permission
    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(ProfileActivity.this,storagePermission, STORAGE_REQUEST);
    }

    // checking camera permissions
    private Boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    // Requesting camera permission
    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(ProfileActivity.this,cameraPermission, CAMERA_REQUEST);
    }

    // Requesting camera and gallery
    // permission if not given
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST: {
                if (grantResults.length > 0) {
                    boolean camera_accepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (camera_accepted && writeStorageAccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(this, "Please Enable Camera and Storage Permissions", Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST: {
                if (grantResults.length > 0) {
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(this, "Please Enable Storage Permissions", Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
        }
    }
    // Here we will pick image from gallery or camera
    private void pickFromGallery() {
        CropImage.activity().start(ProfileActivity.this);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        uploadDataIntoDatabase = new UploadDataIntoDatabase(this);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                setDp(resultUri);
                if(resultUri!=null) {
                    StorageReference ref = FirebaseStorage.getInstance().getReference().child("rentalHome/userDP/" + phoneNumber);
                    uploadDataIntoDatabase.setImageUri(resultUri);
                    uploadDataIntoDatabase.uploadTask(ref);
                }
            }
        }
    }


    private void setDp(Object object){
        Log.d("CHECK","Inside setDp Method");
        Glide.with(this)
                .load(object)
                .circleCrop()
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        circularProgressIndicator.hide();
                        return false;
                    }
                })
                .into(profilePic);
    }
    @Override
    public void onTaskCompleted() {
        if(uploadDataIntoDatabase.isUploaded()){
            FirebaseDatabase.getInstance().getReference().child("user_data").child(phoneNumber).child("profileImage").setValue(uploadDataIntoDatabase.getImageDownloadUrl());
        }
    }
    private boolean checkInternet(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if(activeNetwork==null){
            Snackbar snackbar = Snackbar.make(selectProfile, "No internet Connection", Snackbar.LENGTH_LONG)
                    .setAnchorView(selectProfile)
                    .setTextColor(Color.WHITE)
                    .setBackgroundTint(Color.DKGRAY)
                    .setActionTextColor(Color.WHITE);
            snackbar.show();
            return false;
        }
        return true;
    }
}
