package com.example.mygallery;

import android.content.Context;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.util.List;

public class ImageViewPagerAdapter extends RecyclerView.Adapter<ImageViewPagerAdapter.ImageViewHolder> {
    private Context context;
    private boolean isVisibleInterface = true;
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
        return new ImageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        loadImage(imagePaths.get(position), holder.imageView);
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

    public void loadImage(String imageUrl, SubsamplingScaleImageView imageView)
    {
        imageView.setImage(ImageSource.uri(imageUrl));
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        SubsamplingScaleImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = (SubsamplingScaleImageView) itemView.findViewById(R.id.imageView);
        }
    }
}
