package com.example.mygallery.viewimage;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import androidx.annotation.NonNull;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;

public class RotateTransformation extends BitmapTransformation {
    private static float rotationAngle;

    public RotateTransformation(float rotationAngle) {
        RotateTransformation.rotationAngle = rotationAngle;
    }

    @NonNull
    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        Matrix matrix = new Matrix();
        matrix.postRotate(rotationAngle);
        return Bitmap.createBitmap(toTransform, 0, 0, toTransform.getWidth(), toTransform.getHeight(), matrix, true);
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update(("rotate" + rotationAngle).getBytes());
    }
}
