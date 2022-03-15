package com.example.yashui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;

public class SplashScreen extends AppCompatActivity {
    ImageView rentalHomeImage;
    ImageView getRentalHomeImage;
    TextView rentalHomeText;
    ProgressBar progressBar;
    ShimmerFrameLayout shimmerFrameLayout;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rentalhome);

        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        rentalHomeImage = findViewById(R.id.home_image);
        getRentalHomeImage = findViewById(R.id.home_image1);
        rentalHomeText = findViewById(R.id.rental_text);
        progressBar = findViewById(R.id.progressBar);
        shimmerFrameLayout = findViewById(R.id.shimmer_view);

        Animation animation = AnimationUtils.loadAnimation(this,R.anim.image_splash_screen);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                progressBar.setVisibility(View.VISIBLE);
                shimmerFrameLayout.startShimmer();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        //rentalHomeImage.startAnimation(animation);

        //rentalHomeText.startAnimation(AnimationUtils.loadAnimation(this,R.anim.text_splash_screen));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(shimmerFrameLayout.isShimmerStarted()) {
                    shimmerFrameLayout.stopShimmer();
                    rentalHomeImage.setVisibility(View.INVISIBLE);
                }
                getRentalHomeImage.setVisibility(View.VISIBLE);
            }
        },1500);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this,MainActivity2.class);
                startActivity(intent);
                finish();
            }
        },3000);
    }
}
