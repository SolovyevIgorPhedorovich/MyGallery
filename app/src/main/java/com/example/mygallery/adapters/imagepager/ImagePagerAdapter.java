package com.example.mygallery.adapters.imagepager;

import android.graphics.RectF;
import android.view.GestureDetector;
import android.view.MotionEvent;
import com.example.mygallery.adapters.viewholder.ImagePagerViewHolder;
import com.example.mygallery.interfaces.ToggleMenuListener;
import com.example.mygallery.interfaces.model.Model;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.List;

public class ImagePagerAdapter extends ImagePagerAdapterHelper {

    private final ToggleMenuListener listener;
    private boolean isInterfaceVisible = true;

    public ImagePagerAdapter(List<Model> pathImages, ToggleMenuListener listener) {
        super(pathImages);
        this.listener = listener;
    }

    // Метод для поворота изображения по указанной позиции
    public void rotateImage(int position) {
        ImagePagerViewHolder holder = imageViewHolders.get(position);
        if (holder != null) {
            holder.imageView.setRotationBy(90f); // Поворачиваем изображение на 90 градусов
            toggleScale(holder);
        }
    }

    // Переопределенный метод для обработки события успешной загрузки изображения
    @Override
    protected void onGlideResourceReady(PhotoView photoView, ImagePagerViewHolder holder) {
        super.onGlideResourceReady(photoView, holder);
        calculatedScaleHorizontalOrientation(photoView, holder);
    }

    // Метод для вычисления параметров масштабирования для горизонтальной ориентации
    private void calculatedScaleHorizontalOrientation(PhotoView photoView, ImagePagerViewHolder holder) {
        photoView.post(() -> {
            RectF displayRect = photoView.getDisplayRect();
            float minScale = Math.min(photoView.getWidth() / displayRect.height(),
                    photoView.getHeight() / displayRect.width());
            float midScale = Math.max(photoView.getWidth() / displayRect.width(),
                    photoView.getHeight() / displayRect.height());
            midScale = (midScale == minScale) ? 3f : midScale;
            float maxScale = Math.max(3 * midScale, 9f);
            holder.setScaleHorizontal(minScale, midScale, maxScale);
        });
    }

    // Метод для переключения масштабирования между вертикальной и горизонтальной ориентацией
    private void toggleScale(ImagePagerViewHolder holder) {
        float[] scaleVertical = holder.getScaleVertical();
        float[] scaleHorizontal = holder.getScaleHorizontal();

        PhotoView photoView = holder.imageView;

        if (holder.isRotated) {
            holder.isRotated = false;
            photoView.setScaleLevels(scaleVertical[0], scaleVertical[1], scaleVertical[2]);
        } else {
            holder.isRotated = true;
            photoView.setScaleLevels(scaleHorizontal[0], scaleHorizontal[1], scaleHorizontal[2]);
        }
        photoView.setScale(photoView.getMinimumScale(), true);
    }

    // Переопределенный метод для установки обработчика касаний
    @Override
    protected void setTouchListener(ImagePagerViewHolder holder) {
        PhotoView photoView = holder.imageView;

        photoView.setOnDoubleTapListener(new GestureDetector.SimpleOnGestureListener() {
            // Обработка однократного касания (тапа)
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                listener.onToggleMenu(); // Вызов метода переключения меню
                isInterfaceVisible = !isInterfaceVisible;
                return true;
            }

            // Обработка двойного тапа
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                scaleImage(photoView, e); // Вызов метода для масштабирования изображения
                return true;
            }
        });

        // Установка слушателя изменения масштаба
        photoView.setOnScaleChangeListener((scaleFactor, focusX, focusY) -> {
            if (isInterfaceVisible) {
                listener.onToggleMenu(); // Вызов метода переключения меню
                isInterfaceVisible = false;
            }
        });
    }
}