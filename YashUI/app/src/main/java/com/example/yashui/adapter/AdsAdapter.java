package com.example.yashui.adapter;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.yashui.HomeData;
import com.example.yashui.R;
import com.example.yashui.fragment.Show_All_Ads;

import java.util.ArrayList;

public class AdsAdapter extends RecyclerView.Adapter<AdsAdapter.MyAdsViewHolder> {
    private  ArrayList<HomeData> homeDataArrayList;
    private final Show_All_Ads fragment;
    public AdsAdapter(Show_All_Ads fragment){
        this.fragment = fragment;
    }
    public void setList(ArrayList<HomeData> homeData){
        this.homeDataArrayList = homeData;
    }
    static class  MyAdsViewHolder extends RecyclerView.ViewHolder{
        private final ImageView userImage;
        private final TextView address;
        private final TextView rent;
        private final TextView description;
        public MyAdsViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.userImage);
            address = itemView.findViewById(R.id.address);
            rent = itemView.findViewById(R.id.rent);
            description = itemView.findViewById(R.id.description);
        }
    }
    @NonNull
    @Override
    public AdsAdapter.MyAdsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(fragment.getContext()).inflate(R.layout.user_ads_card,parent,false);
        return new MyAdsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdsAdapter.MyAdsViewHolder holder, int position) {
        String address = homeDataArrayList.get(position).getHome().getAddress()+", "+homeDataArrayList.get(position).getHome().getCountry();
        holder.description.setText(homeDataArrayList.get(position).getHome().getDescription());
        holder.address.setText(address);
        holder.rent.setText(String.valueOf(homeDataArrayList.get(position).getHome().getRent()));
        setUserImage(homeDataArrayList.get(position).getImageUrl(),holder);
    }

    @Override
    public int getItemCount() {
        return homeDataArrayList.size();
    }
    private void setUserImage(String imageUrl,@NonNull AdsAdapter.MyAdsViewHolder holder){
        Glide.with(fragment)
                .load(imageUrl)
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
                .into(holder.userImage);
    }
}
