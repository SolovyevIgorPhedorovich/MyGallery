package com.example.mygallery.viewimage;

import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.example.mygallery.R;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;

public class LoadImage {

    public static void setImage(@NonNull File imageUri, ImageView imageView, Point imageSize) {
        RequestBuilder<Drawable> requestBuilder = Glide.with(imageView.getContext())
                .asDrawable().sizeMultiplier(0.5f);
        Glide.with(imageView)
                .load(imageUri)
                .thumbnail(requestBuilder) // загружаем миниатюру с размером 50% от исходного размера
                .override(imageSize.x, imageSize.y)
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
