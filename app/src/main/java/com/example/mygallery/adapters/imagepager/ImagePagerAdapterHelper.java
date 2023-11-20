package com.example.mygallery.adapters.imagepager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.collection.SparseArrayCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mygallery.R;
import com.example.mygallery.adapters.viewholder.ImagePagerViewHolder;
import com.example.mygallery.viewimage.LoadImage;
import com.github.chrisbanes.photoview.PhotoView;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public abstract class ImagePagerAdapterHelper extends RecyclerView.Adapter<ImagePagerViewHolder> {

    // Минимальный масштаб
    protected final float MIN_SCALE = 1.0f;
    protected final SparseArrayCompat<ImagePagerViewHolder> imageViewHolders;
    protected final float screenWidth, screenHeight;
    protected List<File> pathImages;
    protected boolean isInterfaceVisible = true;
    protected boolean isImageScaled = false;

    public ImagePagerAdapterHelper(Context context, List<File> pathImages) {
        this.screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        this.screenHeight = context.getResources().getDisplayMetrics().heightPixels;
        this.imageViewHolders = new SparseArrayCompat<>();

        setList(pathImages);
    }

    public void setList(List<File> pathImages) {
        this.pathImages = pathImages;
    }

    @NonNull
    @NotNull
    @Override
    public ImagePagerViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_view, parent, false);
        return new ImagePagerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ImagePagerViewHolder holder, int position) {
        loadImage(pathImages.get(position), holder.imageView, 0f);
        setTouchListener(holder.imageView);
        imageViewHolders.put(position, holder);
    }

    // Загружает изображение с учетом угла поворота
    protected void loadImage(File imageUrl, PhotoView imageView, float rotationAngle) {
        LoadImage.setImage(imageUrl, imageView, rotationAngle);
    }

    // Устанавливает обработчик касаний для изображения
    protected abstract void setTouchListener(PhotoView imageView);

    @Override
    public int getItemCount() {
        return pathImages.size();
    }

    // Рассчитывает оптимальный масштаб с учетом угла поворота
    protected float calculateOptimalScale(PhotoView imageView) {
        float imageWidth = imageView.getDrawable().getIntrinsicWidth();
        float imageHeight = imageView.getDrawable().getIntrinsicHeight();
        float rotationAngle = imageView.getRotation();

        if (imageWidth > screenWidth && imageHeight <= screenHeight) {
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
}
