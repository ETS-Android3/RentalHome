package com.example.yashui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private EditText phone,otp;
    private DatabaseReference mDatabase;
    private TextInputLayout textInputLayout;
    private Button genOtp;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks;
    private TextView smsSent, changePhone, resendOtp ;
    private boolean isPhoneEntered = false;
    String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();


        phone = findViewById(R.id.phone);
        genOtp = findViewById(R.id.genOtp);
        progressBar = findViewById(R.id.progressBar);
        otp = findViewById(R.id.otp);
        textInputLayout = findViewById(R.id.textInputLayout2);
        smsSent = findViewById(R.id.otpSent);
        changePhone = findViewById(R.id.changePhone);
        resendOtp = findViewById(R.id.resend);

        genOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isPhoneEntered){
                    //Checking Code
                    progressBar.setVisibility(View.VISIBLE);
                    String otpString = otp.getText().toString();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otpString);
                    signInWithPhoneAuthCredential(credential);

                }
                else {
                    String phone_number = "+91" + phone.getText().toString();
                    Log.d("phone", phone_number);
                    progressBar.setVisibility(View.VISIBLE);
                    genOtp.setEnabled(false);

                    PhoneAuthOptions options =
                            PhoneAuthOptions.newBuilder(mAuth)
                                    .setPhoneNumber(phone_number)       // Phone number to verify
                                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                    .setActivity(LoginActivity.this)                 // Activity (for callback binding)
                                    .setCallbacks(mCallBacks)          // OnVerificationStateChangedCallbacks
                                    .build();
                    PhoneAuthProvider.verifyPhoneNumber(options);
                }

            }
        });

        mCallBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                progressBar.setVisibility(View.INVISIBLE);
                textInputLayout.setVisibility(View.VISIBLE);
                genOtp.setEnabled(true);
                genOtp.setText("SUBMIT");
                phone.setEnabled(false);
                isPhoneEntered = true;
                smsSent.setVisibility(View.VISIBLE);
                changePhone.setVisibility(View.VISIBLE);
                resendOtp.setVisibility(View.VISIBLE);


                verificationId = s;

//                Intent i = new Intent(LoginActivity.this,OtpActivity.class);
//                startActivity(i);

            }
        };

        changePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone.setEnabled(true);
                phone.setText("");
                textInputLayout.setVisibility(View.GONE);
                otp.setText("");
                smsSent.setVisibility(View.GONE);
                changePhone.setVisibility(View.GONE);
                resendOtp.setVisibility(View.GONE);
                genOtp.setText("NEXT");
                isPhoneEntered = false;
            }
        });




    }



    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            // Update UI


                            String phoneNumber = "+91"+phone.getText().toString();
                            mDatabase = FirebaseDatabase.getInstance().getReference().child("user_data").child(phoneNumber);

                            ValueEventListener postListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    // Get Post object and use the values to update the UI
                                    User user = dataSnapshot.getValue(User.class);

                                    Intent i;
                                    if(user == null){
                                        i = new Intent(LoginActivity.this, RegisterActivity.class);
                                    }
                                    else{
                                        i = new Intent(LoginActivity.this, MainActivity2.class);
                                    }
                                    startActivity(i);
                                    finish();
                                    // ..
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // Getting Post failed, log a message
                                    Log.w("fuck", "loadPost:onCancelled", databaseError.toException());
                                }
                            };
                            mDatabase.addValueEventListener(postListener);

                        } else {
                            // Sign in failed, display a message and update the UI
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                smsSent.setTextColor(getResources().getColor(R.color.design_default_color_error));
                                smsSent.setText("Abe Bhosdk Galat Daal diya tune, Ab gand mara..");
                                //smsSent.setTextColor(getResources().getColor(R.color.teal_200));
                                progressBar.setVisibility(View.INVISIBLE);

                            }
                        }
                    }
                });

    }




}