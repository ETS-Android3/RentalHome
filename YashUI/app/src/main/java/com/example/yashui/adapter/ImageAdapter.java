package com.example.yashui.adapter;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.yashui.R;
import com.example.yashui.fragment.ImageFragment;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder>{

    private final ArrayList<String> list;
    private final RequestManager requestManager;
    private final ImageFragment fragment;
    public ImageAdapter(ArrayList<String> list,ImageFragment fragment) {
        this.fragment = fragment;
        this.requestManager = Glide.with(fragment);
        this.list = list;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)   {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.images_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        requestManager
                .load(list.get(position))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.circularProgressIndicator.hide();
                        return false;
                    }
                })
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView imageView;
        private final CircularProgressIndicator circularProgressIndicator;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            this.circularProgressIndicator = itemView.findViewById(R.id.next_circularbar);
            imageView = itemView.findViewById(R.id.image_view);
            imageView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            fragment.setImage(list.get(getAdapterPosition()));
        }
    }
}
