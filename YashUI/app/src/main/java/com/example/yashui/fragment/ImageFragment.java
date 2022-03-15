package com.example.yashui.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.SharedElementCallback;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.yashui.DatabaseHandling;
import com.example.yashui.HomeData;
import com.example.yashui.R;
import com.example.yashui.User;
import com.example.yashui.adapter.ImageAdapter;
import com.example.yashui.ui.home.HomeFragment;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageFragment extends Fragment {
    private CircularProgressIndicator circularProgressIndicator;
    private PhotoView photoView;
    private View view;
    private TextView nameTV,emailTV,phoneTV;
    private RequestManager requestManager;
    private final HomeData homeData;
    private Button homeDetails,show_All_Ads;
    private ImageView imageView,callImage;
    private HashMap<String,String> homeIds;
    public ImageFragment(HomeData homeData)
    {
        this.homeData = homeData;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_image, container, false);
        requestManager = Glide.with(this);
        initiateView();
        // Just like we do when binding views at the grid, we set the transition name to be the string
        // value of the image res.
        view.findViewById(R.id.image).setTransitionName(String.valueOf(HomeFragment.currentPosition));
        prepareSharedElementTransition();
        // Load the image with Glide to prevent OOM error when the image drawables are very large.
        return view;
    }
    private void initiateView() {
        emailTV = view.findViewById(R.id.emailTV);
        phoneTV = view.findViewById(R.id.phoneTV);
        nameTV = view.findViewById(R.id.nameTV);
        homeDetails = view.findViewById(R.id.home_details);
        photoView = view.findViewById(R.id.image);
        circularProgressIndicator = view.findViewById(R.id.image_progressIndicator);
        imageView = view.findViewById(R.id.user_image);
        show_All_Ads = view.findViewById(R.id.show_all_ads);
        callImage = view.findViewById(R.id.callImage);
    }

    @Override
    public void onStart() {
        super.onStart();
        setImage(homeData.getImageUrl());
        showHomeImages();
        if (homeData.getPhone() != null) {
            DatabaseReference temporaryDatabase = FirebaseDatabase.getInstance().getReference().child("user_data").child(homeData.getPhone());
            temporaryDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        User user = snapshot.getValue(User.class);
                        System.out.println(snapshot.getValue());
                        if (user != null) {
                            if (user.getProfileImage() != null) {
                                circularProgressIndicator.setVisibility(View.VISIBLE);
                                setUserProfile(user.getProfileImage());
                            }
                            homeIds = user.getHomeIds();
                            nameTV.setText(user.getName());
                            emailTV.setText(user.getEmail());
                            phoneTV.setText(homeData.getPhone());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
    @Override
    public void onResume() {
        super.onResume();
            show_All_Ads.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        getParentFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                                .setReorderingAllowed(true)
                                .replace(R.id.fragment_container, new Show_All_Ads(homeIds), Show_All_Ads.class.getSimpleName())
                                .addToBackStack(null)
                                .commit();
                }
            });
            homeDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getParentFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_down,R.anim.exit_to_up,R.anim.enter_from_left,R.anim.exit_to_right)
                            .setReorderingAllowed(true)
                            .replace(R.id.fragment_container,new HomeDetails(homeData),HomeDetails.class.getSimpleName())
                            .addToBackStack(null)
                            .commit();
                }
            });
            callImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int permissionCheck = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE);
                    if(permissionCheck!= PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.CALL_PHONE},123);
                    }
                    else {
                        startActivity(new Intent(Intent.ACTION_CALL).setData(Uri.parse("tel:"+homeData.getPhone())));
                    }
                }
            });

    }

    public void setImage(String url){
        requestManager
                .load(url)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable>
                            target, boolean isFirstResource) {
                        // The postponeEnterTransition is called on the parent ImagePagerFragment, so the
                        // startPostponedEnterTransition() should also be called on it to get the transition
                        // going in case of a failure.
//                        getParentFragment().startPostponedEnterTransition();
                        prepareSharedElementTransition();
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                        startPostponedEnterTransition();
                        circularProgressIndicator.hide();
                        prepareSharedElementTransition();
                        return false;
                    }
                })
                .into(photoView);
    }

    private void prepareSharedElementTransition() {
        Transition transition =
                TransitionInflater.from(getContext())
                        .inflateTransition(R.transition.image_shared_element_transition);
        setSharedElementEnterTransition(transition);

        // A similar mapping is set at the JhootiGridFragment with a setExitSharedElementCallback.
        setEnterSharedElementCallback(
                new SharedElementCallback() {
                    @Override
                    public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                        // Locate the image view at the primary fragment (the JhootiImageFragment that is currently
                        // visible). To locate the fragment, call instantiateItem with the selection position.
                        // At this stage, the method will simply return the fragment at the position and will
                        // not create a new one.

                        Fragment currentFragment = ImageFragment.this;
                        View view = currentFragment.getView();
                        if (view == null) {
                            return;
                        }

                        // Map the first shared element name to the child ImageView.
                        sharedElements.put(names.get(0), view.findViewById(R.id.image));
                    }
                });
    }
    private void showHomeImages() {
        RecyclerView recyclerView = view.findViewById(R.id.customer_recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        LinearLayoutManager horizontalLayout = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayout);
        ImageAdapter imageAdapter = new ImageAdapter(homeData.getImagesClass(), ImageFragment.this);
        recyclerView.setAdapter(imageAdapter);
    }
    private void setUserProfile(Object o){
        Glide.with(this)
                .load(o)
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
                .into(imageView);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 123) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getActivity().startActivity(new Intent(Intent.ACTION_CALL).setData(Uri.parse("tel:" + new DatabaseHandling().getPhoneNumber())));
            }
        }
    }


}
