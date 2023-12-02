package com.example.mygallery.adapters.imagepager;

import android.view.GestureDetector;
import android.view.MotionEvent;
import androidx.annotation.NonNull;
import com.example.mygallery.adapters.viewholder.ImagePagerViewHolder;
import com.example.mygallery.interfaces.ToggleMenuListener;
import com.example.mygallery.interfaces.model.Model;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.List;

public class ImagePagerAdapter extends ImagePagerAdapterHelper {
    // Минимальный масштаб
    private final float MIN_SCALE = 1.0f;
    private boolean isInterfaceVisible = true;
    private boolean isImageScaled = false;
    private final ToggleMenuListener listener;

    public ImagePagerAdapter(List<Model> pathImages, ToggleMenuListener listener) {
        super(pathImages);
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
            loadImage(imageList.get(position).getPath(), holder.imageView, holder.rotationAngle);
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
                imageView.setMaximumScale(optimalScale + 2.5f);

                // Получаем координаты нажатия
                float x = motionEvent.getX();
                float y = motionEvent.getY();

                if (isInterfaceVisible) {
                    listener.onToggleMenu();
                    isInterfaceVisible = false;
                }
                isImageScaled = !isImageScaled;
                imageView.setScale(isImageScaled ? optimalScale : MIN_SCALE, x, y, true);
                return true;
            }

            @Override
            public boolean onDoubleTapEvent(@NonNull MotionEvent motionEvent) {
                return false;
            }
        });
    }

}