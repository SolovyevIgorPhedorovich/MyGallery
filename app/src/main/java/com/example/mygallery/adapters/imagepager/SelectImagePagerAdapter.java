package com.example.mygallery.adapters.imagepager;

import android.view.GestureDetector;
import android.view.MotionEvent;
import androidx.annotation.NonNull;
import com.example.mygallery.adapters.viewholder.ImagePagerViewHolder;
import com.example.mygallery.interfaces.model.Model;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.List;

public class SelectImagePagerAdapter extends ImagePagerAdapterHelper {
    public SelectImagePagerAdapter(List<Model> pathImages) {
        super(pathImages);
    }

    @Override
    protected void setTouchListener(ImagePagerViewHolder holder) {
        PhotoView photoView = holder.imageView;
        photoView.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
            @Override
            public boolean onSingleTapConfirmed(@NonNull MotionEvent e) {
                return false;
            }

            @Override
            public boolean onDoubleTap(@NonNull MotionEvent e) {
                scaleImage(photoView, e);
                return true;
            }

            @Override
            public boolean onDoubleTapEvent(@NonNull MotionEvent e) {
                return false;
            }
        });
    }
}
