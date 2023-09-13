package com.example.mygallery.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.Rotate;
import com.bumptech.glide.request.RequestOptions;
import com.example.mygallery.managers.DataManager;
import com.example.mygallery.R;
import com.example.mygallery.activities.ImageViewActivity;
import com.github.chrisbanes.photoview.PhotoView;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.security.MessageDigest;

public class ImageViewPagerAdapter extends RecyclerView.Adapter<ImageViewPagerAdapter.ImageViewHolder> {
    final float MIN_SCALE = 1.0f;
    private final Context context;
    private boolean isVisibleInterface = true;
    private boolean isScaled = false;
    private DataManager dataManager;
    private SparseArray<ImageViewHolder> mImageVieHolder;
    private final float WIDTH, HEIGHT;

    public ImageViewPagerAdapter(Context context, int statusBarHeight){
        this.context = context;
        this.dataManager = DataManager.getInstance(context);
        this.WIDTH = context.getResources().getDisplayMetrics().widthPixels;
        this.HEIGHT = context.getResources().getDisplayMetrics().heightPixels + statusBarHeight;
        this.mImageVieHolder = new SparseArray<>();
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
        loadImage(dataManager.getPathsFiles().get(position), holder.imageView, 0f);
        setTouchListener(holder.imageView);
        mImageVieHolder.put(position, holder);
    }

    @Override
    public int getItemCount() {
        return dataManager.getPathsFiles().size();
    }

    public void removedHolderIsArray(int position){
        mImageVieHolder.remove(position);
    }


    private void loadImage(File imageUrl, PhotoView imageView, float rotationAngle) {
        Glide.with(context)
                .load(imageUrl)
                .transform(new RotateTransformation(rotationAngle))
                .into(imageView);
    }

    public void rotation(int position){
        ImageViewHolder holder = mImageVieHolder.get(position);
        if (holder.rotationAngle + 90f == 360f)
            holder.rotationAngle = 0f;
        else
            holder.rotationAngle = holder.rotationAngle + 90f;
        loadImage(dataManager.getPathsFiles().get(position), holder.imageView, holder.rotationAngle);
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        PhotoView imageView;
        float rotationAngle;
        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = (PhotoView) itemView.findViewById(R.id.imageView);
            rotationAngle = 0f;
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

    private class RotateTransformation extends BitmapTransformation {
        private float rotationAngle;

        public RotateTransformation(float rotationAngle){
            this.rotationAngle = rotationAngle;
        }

        @NonNull
        @NotNull

        @Override
        protected Bitmap transform(@NonNull @NotNull BitmapPool pool, @NonNull @NotNull Bitmap toTransform, int outWidth, int outHeight) {
            Matrix matrix = new Matrix();

            matrix.postRotate(rotationAngle);
            return Bitmap.createBitmap(toTransform, 0, 0, toTransform.getWidth(), toTransform.getHeight(), matrix, true);
        }

        @Override
        public void updateDiskCacheKey(@NonNull @NotNull MessageDigest messageDigest) {
            messageDigest.update(("rotate" + rotationAngle).getBytes());
        }
    }
}
