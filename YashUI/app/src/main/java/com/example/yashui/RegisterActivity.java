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
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
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

public class RegisterActivity extends AppCompatActivity implements TaskCompleted {
    private EditText name , email;
    private ProgressBar progressBar;
    private Button next;
    private TextInputLayout textInputLayout,textInputLayout2;
    private ImageView profilePic;
    private DatabaseReference mYDatabase;
    private static final int CAMERA_REQUEST = 100;
    private static final int STORAGE_REQUEST = 200;
    private String[] cameraPermission;
    private String[] storagePermission;
    private UploadDataIntoDatabase uploadDataIntoDatabase;
    private String phoneNumber,imageUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        profilePic = findViewById(R.id.profile_dp);
        textInputLayout = findViewById(R.id.textInputLayout);
        textInputLayout2  = findViewById(R.id.textInputLayout2);
        next = findViewById(R.id.next);
        imageUrl = null;
        progressBar = findViewById(R.id.progressBar);
        // allowing permissions of gallery and camera
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        DatabaseHandling handling = new DatabaseHandling();
        mYDatabase =  FirebaseDatabase.getInstance().getReference().child("user_data").child(handling.getPhoneNumber());
        phoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        Fade fade = new Fade();
        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);

    }
    private boolean checkFields(String email,String name){
        if(email.isEmpty()||name.isEmpty())
            return false;
        else {
            if(isEmailNameValid(email,name))
                return true;
            else
                return false;
        }
    }
    private boolean isEmailNameValid(String email,String name){
        boolean emailBool = true;
        boolean nameBool = true;
        if(!email.matches("^[a-zA-Z0-9+.-_]+@[a-zA-Z0-9+._-]+$")){
            textInputLayout2.setErrorEnabled(true);
            textInputLayout2.setBoxStrokeColor(Color.RED);
            textInputLayout2.setError("Entered email is not valid");
            emailBool = false;
        }
        if (!name.matches("^[a-zA-Z]{4,}(?: [a-zA-Z]+){0,2}$")){
            textInputLayout.setErrorEnabled(true);
            textInputLayout.setBoxStrokeColor(Color.RED);
            textInputLayout.setError("Entered name is not valid");
            nameBool = false;
        }
            return (emailBool&&nameBool);
    }
    private void setDp(Object object){
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
                        return false;
                    }
                })
                .into(profilePic);
    }

    @Override
    public void onResume() {
        super.onResume();
        profilePic.setOnClickListener(new View.OnClickListener() {
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


        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() >= 1) {
                    textInputLayout2.setErrorEnabled(false);
                    textInputLayout2.setBoxStrokeColor(Color.rgb(36, 169, 225));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() >= 1) {
                    textInputLayout.setErrorEnabled(false);
                    textInputLayout.setBoxStrokeColor(Color.rgb(36, 169, 225));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sEmail = email.getText().toString();
                String sName = name.getText().toString();
                if(checkFields(sEmail,sName)) {
                    User user = new User(sEmail, sName);
                    if(imageUrl!=null)
                        user.setProfileImage(imageUrl);
                    if (checkInternet()) {
                        mYDatabase.setValue(user);
                        progressBar.setVisibility(View.VISIBLE);
                        Intent i = new Intent(RegisterActivity.this, MainActivity2.class);
                        startActivity(i);
                        finish();
                    }
                }
                else{
                    if(sEmail.isEmpty())
                    {
                        textInputLayout2.setErrorEnabled(true);
                        textInputLayout2.setBoxStrokeColor(Color.RED);
                        textInputLayout2.setError("Please Enter Email");
                    }
                    if(sName.isEmpty()){
                        textInputLayout.setErrorEnabled(true);
                        textInputLayout.setBoxStrokeColor(Color.RED);
                        textInputLayout.setError("Please Specify Valid Name");
                    }
                }
            }
        });
    }
    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(RegisterActivity.this,storagePermission, STORAGE_REQUEST);
    }

    // checking camera permissions
    private Boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    // Requesting camera permission
    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(RegisterActivity.this,cameraPermission, CAMERA_REQUEST);
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
        CropImage.activity().start(RegisterActivity.this);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        uploadDataIntoDatabase = new UploadDataIntoDatabase(this);
        Log.d("CHECK","Inside onActivityResult");
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
    @Override
    public void onTaskCompleted() {
        if(uploadDataIntoDatabase.isUploaded()){
            imageUrl = uploadDataIntoDatabase.getImageDownloadUrl();
        }
    }
    private boolean checkInternet(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if(activeNetwork==null){
            Snackbar snackbar = Snackbar.make(next, "No internet Connection", Snackbar.LENGTH_LONG)
                    .setAnchorView(next)
                    .setTextColor(Color.WHITE)
                    .setBackgroundTint(Color.DKGRAY)
                    .setActionTextColor(Color.WHITE);
            snackbar.show();
            return false;
        }
        return true;
    }
}