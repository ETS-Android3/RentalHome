package com.example.yashui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private EditText name , email;
    private ProgressBar progressBar;
    private Button next;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        next = findViewById(R.id.next);
        progressBar = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        phoneNumber = mCurrentUser.getPhoneNumber();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("user_data").child(phoneNumber);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);
                String sEmail = email.getText().toString();
                String sName = name.getText().toString();
                User user = new User(sName,sEmail);
                mDatabase.setValue(user);
                Intent i = new Intent(RegisterActivity.this , MainActivity2.class);
                startActivity(i);
                finish();

            }
        });

    }
}