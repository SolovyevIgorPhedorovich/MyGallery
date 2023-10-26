package com.example.mygallery.adapters.imagepager;

import android.content.Context;
import android.view.*;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.collection.SparseArrayCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mygallery.R;
import com.example.mygallery.adapters.viewholder.ImagePagerViewHolder;
import com.example.mygallery.interfaces.ToggleMenuListener;
import com.example.mygallery.viewimage.LoadImage;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.util.List;

public class ImagePagerAdapter extends ImagePagerAdapterHelper {
    // Минимальный масштаб
    private final float MIN_SCALE = 1.0f;
    private boolean isInterfaceVisible = true;
    private boolean isImageScaled = false;
    private final ToggleMenuListener listener;

    public ImagePagerAdapter(Context context, List<File> pathImages, ToggleMenuListener listener) {
        super(context, pathImages);
        this.listener = listener;
    }


    // Поворачивает изображение
    public void rotateImage(int position) {
        ImagePagerViewHolder holder = imageViewHolders.get(position);
        if (holder != null) {
            if (holder.rotationAngle + 90f == 360f)
                holder.rotationAngle = 0f;
            else
                holder.rotationAngle += 90f;
            loadImage(pathImages.get(position), holder.imageView, holder.rotationAngle);
        }
    }

    @Override
    protected void setTouchListener(PhotoView imageView) {
        imageView.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
            @Override
            public boolean onSingleTapConfirmed(@NonNull MotionEvent motionEvent) {
                listener.onToggleMenu();
                isInterfaceVisible = !isInterfaceVisible;
                return true;
            }

            @Override
            public boolean onDoubleTap(@NonNull MotionEvent motionEvent) {
                float optimalScale = calculateOptimalScale(imageView);
                imageView.setMaximumScale(optimalScale + 1.5f);
                if (isInterfaceVisible) {
                    listener.onToggleMenu();
                    isInterfaceVisible = false;
                }
                isImageScaled = !isImageScaled;
                imageView.setScale(isImageScaled ? optimalScale : MIN_SCALE, true);
                return true;
            }

            @Override
            public boolean onDoubleTapEvent(@NonNull MotionEvent motionEvent) {
                return false;
            }
        });
    }

}