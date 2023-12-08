package com.example.mygallery.viewimage;

import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.mygallery.R;
import com.example.mygallery.adapters.imagepager.ImagePagerAdapterHelper;
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

    // Загружает изображение для PhotoView
    public static void setImage(File imageUrl, PhotoView photoView, ImagePagerAdapterHelper.OnGlideListener listener) {
        RequestOptions requestOptions = new RequestOptions()
                .format(DecodeFormat.PREFER_ARGB_8888)
                .override(Target.SIZE_ORIGINAL);

        Glide.with(photoView.getContext())
                .load(imageUrl)
                .apply(requestOptions)
                .listener(new RequestListener<>() {
                    @Override
                    public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        listener.onGlideResourceReady(photoView);
                        return false;
                    }
                })
                .into(photoView);
    }
}
