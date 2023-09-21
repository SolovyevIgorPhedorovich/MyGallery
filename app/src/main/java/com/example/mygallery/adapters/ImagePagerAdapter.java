package com.example.mygallery.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.collection.SparseArrayCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.example.mygallery.R;
import com.example.mygallery.activities.ImageViewActivity;
import com.example.mygallery.managers.DataManager;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.security.MessageDigest;

public class ImagePagerAdapter extends RecyclerView.Adapter<ImagePagerAdapter.ImageViewHolder> {
    // Минимальный масштаб
    private final float MIN_SCALE = 1.0f;

    private final Context context;
    private final DataManager dataManager;
    private final SparseArrayCompat<ImageViewHolder> imageViewHolders;
    private final float screenWidth, screenHeight;
    private boolean isInterfaceVisible = true;
    private boolean isImageScaled = false;

    public ImagePagerAdapter(Context context, int statusBarHeight) {
        this.context = context;
        this.dataManager = DataManager.getInstance(context);
        this.screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        this.screenHeight = context.getResources().getDisplayMetrics().heightPixels + statusBarHeight;
        this.imageViewHolders = new SparseArrayCompat<>();
    }

    // Создает новый ViewHolder
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_view, parent, false);
        return new ImageViewHolder(itemView);
    }

    // Привязывает данные к ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        loadImage(dataManager.getImageFilesList().get(position), holder.imageView, 0f);
        setTouchListener(holder.imageView);
        imageViewHolders.put(position, holder);
    }

    // Возвращает количество элементов в данных
    @Override
    public int getItemCount() {
        return dataManager.getImageFilesList().size();
    }

    // Удаляет ViewHolder из массива
    public void removeHolderFromArray(int position) {
        imageViewHolders.remove(position);
    }

    // Загружает изображение с учетом угла поворота
    private void loadImage(File imageUrl, PhotoView imageView, float rotationAngle) {
        Glide.with(context)
                .load(imageUrl)
                .transform(new RotateTransformation(rotationAngle))
                .into(imageView);
    }

    // Поворачивает изображение
    public void rotateImage(int position) {
        ImageViewHolder holder = imageViewHolders.get(position);
        if (holder != null) {
            if (holder.rotationAngle + 90f == 360f)
                holder.rotationAngle = 0f;
            else
                holder.rotationAngle += 90f;
            loadImage(dataManager.getImageFilesList().get(position), holder.imageView, holder.rotationAngle);
        }
    }

    // Устанавливает обработчик касаний для изображения
    private void setTouchListener(PhotoView imageView) {
        imageView.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
            @Override
            public boolean onSingleTapConfirmed(@NonNull MotionEvent motionEvent) {
                if (context instanceof ImageViewActivity) {
                    ImageViewActivity imageViewActivity = (ImageViewActivity) context;
                    imageViewActivity.toggleMenu();
                    isInterfaceVisible = !isInterfaceVisible;
                }
                return true;
            }

            @Override
            public boolean onDoubleTap(@NonNull MotionEvent motionEvent) {
                float optimalScale = calculateOptimalScale(imageView);
                imageView.setMaximumScale(optimalScale + 1.5f);
                if (context instanceof ImageViewActivity && isInterfaceVisible) {
                    ImageViewActivity imageViewActivity = (ImageViewActivity) context;
                    imageViewActivity.toggleMenu();
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

    // Рассчитывает оптимальный масштаб с учетом угла поворота
    private float calculateOptimalScale(PhotoView imageView) {
        float imageWidth = imageView.getDrawable().getIntrinsicWidth();
        float imageHeight = imageView.getDrawable().getIntrinsicHeight();
        float rotationAngle = imageView.getRotation();

        if (imageWidth > screenWidth && imageHeight <= imageHeight) {
            imageHeight -= imageWidth - screenWidth;
            imageWidth = screenWidth;
        } else if (imageHeight > screenHeight && imageWidth <= screenWidth) {
            imageWidth -= imageHeight - screenHeight;
            imageHeight = screenHeight;
        }

        // Учитываем угол поворота
        if (rotationAngle % 180 == 90) {
            // Если угол поворота 90 градусов, меняем ширину и высоту местами
            float temp = imageWidth;
            imageWidth = imageHeight;
            imageHeight = temp;
        }

        float scaleX = screenWidth / imageWidth;
        float scaleY = screenHeight / imageHeight;

        // Возвращаем минимальное значение масштаба, чтобы изображение полностью уместилось
        return Math.max(scaleX, scaleY);
    }

    // ViewHolder для изображения
    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        PhotoView imageView;
        float rotationAngle;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            rotationAngle = 0f;
        }
    }

    // Класс для вращения изображения
    private static class RotateTransformation extends BitmapTransformation {
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
}