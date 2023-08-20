package com.example.mygallery.adapters;

import android.content.Context;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mygallery.DataManager;
import com.example.mygallery.R;
import com.example.mygallery.activities.ImageViewActivity;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.List;

public class ImageViewPagerAdapter extends RecyclerView.Adapter<ImageViewPagerAdapter.ImageViewHolder> {
    final float MIN_SCALE = 1.0f;
    private final Context context;
    private boolean isVisibleInterface = true;
    private boolean isScaled = false;
    private DataManager dataManager;
    private final float WIDTH, HEIGHT;

    public ImageViewPagerAdapter(Context context, int statusBarHeight){
        this.context = context;
        this.dataManager = DataManager.getInstance();
        this.WIDTH = context.getResources().getDisplayMetrics().widthPixels;
        this.HEIGHT = context.getResources().getDisplayMetrics().heightPixels + statusBarHeight;
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
        loadImage(dataManager.getPathsFiles().get(position), holder.imageView);
        setTouchListener(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return dataManager.getPathsFiles().size();
    }

    public void loadImage(String imageUrl, PhotoView imageView) {
        Glide.with(context)
                .load(imageUrl)
                .into(imageView);
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        PhotoView imageView;
        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = (PhotoView) itemView.findViewById(R.id.imageView);

        }
    }

    private void setTouchListener(PhotoView imageView) {
        imageView.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
            @Override
            public boolean onSingleTapConfirmed(@NonNull MotionEvent motionEvent) {
                if (context instanceof ImageViewActivity){
                    ImageViewActivity imageViewActivity = (ImageViewActivity) context;
                    imageViewActivity.toggleMenu();
                    isVisibleInterface = !isVisibleInterface;
                }
                return true;
            }

            @Override
            public boolean onDoubleTap(@NonNull MotionEvent motionEvent) {
                float optimalScaled = getOptimalScaled(imageView);
                imageView.setMaximumScale(optimalScaled+1.5f);
                if (context instanceof ImageViewActivity && isVisibleInterface){
                    ImageViewActivity imageViewActivity = (ImageViewActivity) context;
                    imageViewActivity.toggleMenu();
                    isVisibleInterface = false;
                }
                if (isScaled) {
                    imageView.setScale(MIN_SCALE, true);
                    isScaled = false;
                }
                else{
                    imageView.setScale(optimalScaled, true);
                    isScaled = true;
                }
                return true;
            }

            @Override
            public boolean onDoubleTapEvent(@NonNull MotionEvent motionEvent) {
                return false;
            }
        });
    }

    private float getOptimalScaled(PhotoView imageView){
        float pWidth = imageView.getDrawable().getIntrinsicWidth();
        float pHeight = imageView.getDrawable().getIntrinsicHeight();
        float result;

        if (pWidth != WIDTH){
            result = WIDTH / pWidth;
        } else if (pHeight != HEIGHT) {
            result = HEIGHT / pHeight;
        }
        else{
            result = 2.0f;
        }
        return result;
    }
}
