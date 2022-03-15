package com.example.yashui;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.MyView> {
    private final ArrayList<Uri> horizontalList;
    private final AddDataActivity activity;
    RequestManager requestManager;
    public class MyView extends RecyclerView.ViewHolder implements View.OnLongClickListener{
        ImageView imageView ;

        public MyView(View view){
            super(view);
            imageView = view.findViewById(R.id.image_view);
            view.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            Uri temporaryUri = horizontalList.get(getAdapterPosition());
            int position = getAdapterPosition();
            PopupMenu popupMenu = new PopupMenu(activity.getApplicationContext(),v);
            popupMenu.getMenuInflater().inflate(R.menu.delete_menu,popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if(item.getItemId() == R.id.delete){
                        horizontalList.remove(position);
                        Snackbar.make(v, "Image Removed", Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                horizontalList.add(position, temporaryUri);
                                notifyItemInserted(position);
                                activity.setMediaSize(horizontalList.size());
                            }
                        }).show();
                        activity.setMediaSize(horizontalList.size());
                        notifyItemRemoved(position);
                        return true;
                    }
                    return false;
                }
            });
            popupMenu.show();
            return true;
        }
    }
    public Adapter(ArrayList<Uri> horizontalList, AddDataActivity activity){
        this.activity = activity;
        this.horizontalList = horizontalList;
        requestManager = Glide.with(activity);
    }

    @NonNull
    @Override
    public MyView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
        return new MyView(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyView holder, int position) {
//        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),horizontalList[position]);
//        int ow = bitmap.getWidth();
//        int oh = bitmap.getHeight();
//
//        if(oh>=4096 && ow>=4096){
//            if(oh>ow){
//                int nh = 4000;
//                int nw = (ow*nh)/oh;
//                oh = nh;
//                ow = nw;
//            }
//            else if(ow>oh){
//                int nw = 4000;
//                int nh = (oh*nw)/ow;
//                oh = nh;
//                ow = nw;
//            }
//            else
//            {
//                oh =4000;
//                ow = 4000;
//            }
//        }
//        else if(oh>ow){
//            int nh = 4000;
//            int nw = (ow*nh)/oh;
//            oh = nh;
//            ow = nw;
//        }
//        else if(ow>oh){
//            int nw = 4000;
//            int nh = (oh*nw)/ow;
//            oh = nh;
//            ow = nw;
//        }
//        Bitmap bitmap1 = Bitmap.createScaledBitmap(bitmap,ow,oh,true);
//        holder.imageView.setImageBitmap(bitmap1);
//        RequestOptions requestOptions = new RequestOptions();
        requestManager
                .load(horizontalList.get(position))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable>
                            target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(holder.imageView);
    }
    @Override
    public int getItemCount() {
        return horizontalList.size();
    }
}

