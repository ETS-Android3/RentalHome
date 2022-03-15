package com.example.yashui.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.yashui.ProfileActivity;
import com.example.yashui.R;
import com.example.yashui.User;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

public class NewAccountFragment extends Fragment {
    private TextView nameTV , emailTV , phoneTV ;
    private String phoneNumber;
    private ImageView userDP;
    private HashMap<String,String> homeIds;
    private CircularProgressIndicator circularProgressIndicator;
    private View root;
    private Button my_ads,settings,favourites,contact_us;
    private String imageUrl;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.new_account_fragment, container, false);
        initiateView();
        return root;
    }
    private void initiateView(){
        circularProgressIndicator = root.findViewById(R.id.account_progressIndicator);
        nameTV = root.findViewById(R.id.user_name);
        emailTV = root.findViewById(R.id.user_email);
        phoneTV = root.findViewById(R.id.user_phone);
        userDP = root.findViewById(R.id.user_dp);
        my_ads = root.findViewById(R.id.my_ads);
        favourites = root.findViewById(R.id.favourites);
        settings = root.findViewById(R.id.settings);
        contact_us = root.findViewById(R.id.contact_us);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mCurrentUser = mAuth.getCurrentUser();
        if(mCurrentUser != null) {
            phoneNumber = mCurrentUser.getPhoneNumber();
            DatabaseReference temporaryDatabase = FirebaseDatabase.getInstance().getReference().child("user_data").child(phoneNumber);
            temporaryDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            nameTV.setText(user.getName());
                            emailTV.setText(user.getEmail());
                            phoneTV.setText(phoneNumber);
                            homeIds = user.getHomeIds();
                            if (user.getProfileImage() != null) {
                                imageUrl = user.getProfileImage();
                                circularProgressIndicator.setVisibility(View.VISIBLE);
                                setDp(user.getProfileImage());
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        userDP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                intent.putExtra("IMAGE_URL",imageUrl);
                // below method is used to make scene transition
                // and adding fade animation in it.
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(Objects.requireNonNull(requireActivity()), userDP, ViewCompat.getTransitionName(userDP));
                // starting our activity with below method.
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(intent, options.toBundle());
                    }
                },100);
            }
        });
        my_ads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    getParentFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                            .replace(R.id.account_fragment_container, new Show_All_Ads(homeIds), Show_All_Ads.class.getSimpleName())
                            .addToBackStack(null)
                            .commit();
            }
        });
        favourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                        .replace(R.id.account_fragment_container, new Favourites(), Favourites.class.getSimpleName())
                        .addToBackStack(null)
                        .commit();
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                        .replace(R.id.account_fragment_container, new Settings(), Settings.class.getSimpleName())
                        .addToBackStack(null)
                        .commit();
            }
        });
        contact_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                        .replace(R.id.account_fragment_container, new ContactUs(), ContactUs.class.getSimpleName())
                        .addToBackStack(null)
                        .commit();
            }
        });
    }
    private void setDp(Object object) {
        if (isAdded()) {
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
                    .into(userDP);
        }
    }

}
