package com.example.mygallery.viewimage;

import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.example.mygallery.R;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;

public class LoadImage {

    public static void setImage(File imageUri, ImageView imageView) {
        Glide.with(imageView)
                .load(imageUri)
                .error(R.drawable.invalid_image)
                .into(imageView);
    }

    // Загружает изображение с учетом угла поворота
    public static void setImage(File imageUrl, PhotoView imageView, float rotationAngle) {
        Glide.with(imageView)
                .load(imageUrl)
                .transform(new RotateTransformation(rotationAngle))
                .into(imageView);
    }
}
