package com.example.yashui.adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
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
import com.example.yashui.fragment.ImageFragment;
import com.example.yashui.ui.home.HomeFragment;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import xyz.hanks.library.bang.SmallBangView;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ImageViewHolder> {

    /**
     * A listener that is attached to all ViewHolders to handle image loading events and clicks.
     */
    private final CircularProgressIndicator circularNext;
    private  ArrayList<HomeData> list;
    private final Fragment fragment;
    private interface ViewHolderListener
    {
        void onLoadCompleted(ImageView view, int adapterPosition,CircularProgressIndicator circularProgressIndicator);
        void onItemClicked(View view, int adapterPosition);
    }
    private final RequestManager requestManager;
    private final ViewHolderListener viewHolderListener;

    /**
     * Constructs a new grid adapter for the given {@link Fragment}.
     */
    public GridAdapter(Fragment fragment, CircularProgressIndicator circularNext) {
        this.fragment = fragment;
        this.requestManager = Glide.with(fragment);
        this.circularNext = circularNext;
        this.viewHolderListener = new ViewHolderListenerImpl(fragment);
    }
    public void setList(ArrayList<HomeData> list){
        this.list = list;
    }
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_card, parent, false);
        return new ImageViewHolder(view, requestManager, viewHolderListener);
    }
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        holder.rentText.setText(String.valueOf((int) list.get(position).getHome().getRent()));
        holder.addressText.setText(list.get(position).getHome().getAddress());
        holder.descriptionText.setText(list.get(position).getHome().getDescription());
        holder.onBind();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    /**
     * Default {@link ViewHolderListener} implementation.
     */
    private  class ViewHolderListenerImpl implements ViewHolderListener {

        private final Fragment fragment;
        private final AtomicBoolean enterTransitionStarted;

        ViewHolderListenerImpl(Fragment fragment) {
            this.fragment = fragment;
            this.enterTransitionStarted = new AtomicBoolean();
        }
        @Override
        public void onLoadCompleted(ImageView view, int position,CircularProgressIndicator circularProgressIndicator) {
            circularProgressIndicator.hide();
            // Call startPostponedEnterTransition only when the 'selected' image loading is completed.
            if (HomeFragment.currentPosition != position) {
                return;
            }
            if (enterTransitionStarted.getAndSet(true)) {
                return;
            }
            fragment.startPostponedEnterTransition();
        }

        /**
         * Handles a view click by setting the current position to the given {@code position} and
         * starting a {@link HomeFragment} which displays the image at the position.
         *
         * @param view the clicked {@link ImageView} (the shared element view will be re-mapped at the
         * JhootiGridFragment's SharedElementCallback)
         * @param position the selected view position
         */
        @Override
        public void onItemClicked(View view, int position) {
            // Update the position.

            HomeFragment.currentPosition = position;
            // Exclude the clicked card from the exit transition (e.g. the card will disappear immediately
            // instead of fading out with the rest to prevent an overlapping animation of fade and move).
            ((TransitionSet) fragment.getExitTransition()).excludeTarget(view, true);
            ImageView transitioningView = view.findViewById(R.id.card_image);
            fragment.getParentFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true) // Optimize for shared element transition
                    .addSharedElement(transitioningView, transitioningView.getTransitionName())
                    .replace(R.id.fragment_container, new ImageFragment(list.get(position))
                            , ImageFragment.class.getSimpleName())
                    .addToBackStack(null)
                    .commit();
        }
    }

    /**
     * ViewHolder for the grid's images.
     */
    class ImageViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {

        private final ImageView image,overFlow;
        private final SmallBangView heartImage;
        private final TextView rentText,descriptionText,addressText;
        private final CircularProgressIndicator circularProgressIndicator;
        private final RequestManager requestManager;
        private final ViewHolderListener viewHolderListener;

        ImageViewHolder(View itemView, RequestManager requestManager,
                        ViewHolderListener viewHolderListener) {
            super(itemView);
            this.heartImage = itemView.findViewById(R.id.imageViewAnimation);
            this.overFlow = itemView.findViewById(R.id.card_overflow);
            this.rentText = itemView.findViewById(R.id.rent_text);
            this.descriptionText = itemView.findViewById(R.id.description_box);
            this.addressText = itemView.findViewById(R.id.location_text);
            this.image = itemView.findViewById(R.id.card_image);
            this.circularProgressIndicator = itemView.findViewById(R.id.next_circularbar);
            this.requestManager = requestManager;
            this.viewHolderListener = viewHolderListener;
            overFlow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(fragment.getContext(),overFlow);
                    popupMenu.getMenuInflater().inflate(R.menu.main_activity3,popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch(item.getItemId()){
                                case R.id.call: {
                                     int permissionCheck = ContextCompat.checkSelfPermission(fragment.getContext(), Manifest.permission.CALL_PHONE);
                                    if(permissionCheck!= PackageManager.PERMISSION_GRANTED){
                                        ActivityCompat.requestPermissions(fragment.getActivity(),new String[]{Manifest.permission.CALL_PHONE},123);
                                    }
                                    else {
                                        fragment.startActivity(new Intent(Intent.ACTION_CALL).setData(Uri.parse("tel:" + list.get(getAdapterPosition()).getPhone())));
                                    }
                                    break;
                                }
                                case R.id.share: {
                                    Toast.makeText(fragment.getContext(), "Share Button is Clicked", Toast.LENGTH_LONG).show();
                                    break;
                                }
                            }
                            return true;
                        }
                    });
                    popupMenu.show();
                }
            });
            itemView.findViewById(R.id.card_view).setOnClickListener(this);
        }

        /**
         * Binds this view holder to the given adapter position.
         *
         * The binding will load the image into the image view, as well as set its transition name for
         * later.
         */
        void onBind() {
            int adapterPosition = getAdapterPosition();
            setImage(adapterPosition);
            heartImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (heartImage.isSelected()) {
                        heartImage.setSelected(false);
                    } else {
                        // if not selected only
                        // then show animation.
                        heartImage.setSelected(true);
                    }
                    heartImage.likeAnimation();
                }
            });
            // Set the string value of the image resource as the unique transition name for the view.
            image.setTransitionName(String.valueOf(adapterPosition));
        }

        void setImage(final int adapterPosition) {
            // Load the image with Glide to prevent OOM error when the image drawables are very large.
            circularNext.hide();
            requestManager
                    .load(list.get(adapterPosition).getImageUrl())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                    Target<Drawable> target, boolean isFirstResource) {
                            viewHolderListener.onLoadCompleted(image, adapterPosition,circularProgressIndicator);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable>
                                target, DataSource dataSource, boolean isFirstResource) {
                            viewHolderListener.onLoadCompleted(image, adapterPosition,circularProgressIndicator);
                            return false;
                        }
                    })
                    .into(image);
        }

        @Override
        public void onClick(View view) {
            // Let the listener start the ImagePagerFragment.
            viewHolderListener.onItemClicked(view, getAdapterPosition());
        }
    }

}
