package com.example.mygallery.adapters.imagepager;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import androidx.annotation.NonNull;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.util.List;

public class SelectImagePagerAdapter extends ImagePagerAdapterHelper {
    public SelectImagePagerAdapter(Context context, List<File> pathImages, int statusBarHeight) {
        super(context, pathImages);
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
                imageView.setMaximumScale(optimalScale + 1.5f);
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
