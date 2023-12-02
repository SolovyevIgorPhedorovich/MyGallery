package com.example.mygallery.adapters.imagepager;

import android.view.GestureDetector;
import android.view.MotionEvent;
import androidx.annotation.NonNull;
import com.example.mygallery.interfaces.model.Model;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.List;

public class SelectImagePagerAdapter extends ImagePagerAdapterHelper {
    public SelectImagePagerAdapter(List<Model> pathImages) {
        super(pathImages);
    }

    @Override
    protected void setTouchListener(PhotoView imageView) {
        imageView.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
            @Override
            public boolean onSingleTapConfirmed(@NonNull MotionEvent motionEvent) {
                return false;
            }

            @Override
            public boolean onDoubleTap(@NonNull MotionEvent motionEvent) {
                float optimalScale = calculateOptimalScale(imageView);
                imageView.setMaximumScale(optimalScale + 2.5f);

                // Получаем координаты нажатия
                float x = motionEvent.getX();
                float y = motionEvent.getY();

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
