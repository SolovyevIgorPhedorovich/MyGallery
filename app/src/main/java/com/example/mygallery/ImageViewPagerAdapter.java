package com.example.mygallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.util.ArrayList;
import java.util.List;

public class ImageViewPagerAdapter extends RecyclerView.Adapter<ImageViewPagerAdapter.ImageViewHolder> {
    private Context context;
    private boolean isVisibleInterface = true;
    private List<ImageViewHolder> holders = new ArrayList<>();
    private List<String> imagePaths;

    public ImageViewPagerAdapter(Context context, List<String> imagePaths){
        this.context = context;
        this.imagePaths = imagePaths;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_view, parent, false);
        ImageViewHolder holder = new ImageViewHolder(itemView);
        holders.add(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        loadImageWithGlide(imagePaths.get(position), holder.imageView);

        holder.imageView.setOnTouchListener(new View.OnTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
               @Override
               public boolean onSingleTapConfirmed(MotionEvent e){
                   if (context instanceof ImageViewActivity){
                       ImageViewActivity imageViewActivity = (ImageViewActivity) context;
                       imageViewActivity.toggleMenu();
                       if (isVisibleInterface){
                           isVisibleInterface = false;
                       }
                       else{
                           isVisibleInterface = true;
                       }
                   }
                   return true;
               }
               @Override
               public boolean onDoubleTap(MotionEvent e){
                   float targetScale;
                   if (context instanceof ImageViewActivity && isVisibleInterface){
                       ImageViewActivity imageViewActivity = (ImageViewActivity) context;
                       imageViewActivity.toggleMenu();
                       isVisibleInterface = false;
                   }
                   return true;
               }
            });
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gestureDetector.onTouchEvent(motionEvent);
                return false;
            }
        });



    }

    @Override
    public int getItemCount() {
        return imagePaths.size();
    }

    public void loadImageWithGlide(String imageUrl, SubsamplingScaleImageView imageView)
    {
        Glide.with(context)
                .asBitmap()
                .load(imageUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        imageView.setImage(ImageSource.bitmap(resource));
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        SubsamplingScaleImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = (SubsamplingScaleImageView) itemView.findViewById(R.id.imageView);
        }
    }
    public void resetScaleViewHolder(int position){
        ImageViewHolder holder = holders.get(holders.size()-2);
        if (holders != null && holder.imageView != null){
            holder.imageView.resetScaleAndCenter();
        }
    }
}
