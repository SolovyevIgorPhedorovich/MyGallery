package com.example.mygallery.adapters.imagepager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.collection.SparseArrayCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mygallery.R;
import com.example.mygallery.adapters.viewholder.ImagePagerViewHolder;
import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.viewimage.LoadImage;
import com.github.chrisbanes.photoview.PhotoView;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public abstract class ImagePagerAdapterHelper extends RecyclerView.Adapter<ImagePagerViewHolder> {

    // Минимальный масштаб
    protected final float MIN_SCALE = 1.0f;
    protected final SparseArrayCompat<ImagePagerViewHolder> imageViewHolders;
    protected List<Model> imageList;
    protected boolean isImageScaled = false;

    public ImagePagerAdapterHelper(List<Model> imageList) {
        this.imageViewHolders = new SparseArrayCompat<>();

        setList(imageList);
    }

    public List<Model> getList() {
        return imageList;
    }

    public void setList(List<Model> pathImages) {
        this.imageList = pathImages;
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
        loadImage(imageList.get(position).getPath(), holder.imageView, 0f);
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
        return imageList.size();
    }

    // Рассчитывает оптимальный масштаб с учетом угла поворота
    protected float calculateOptimalScale(PhotoView imageView) {
        float imageWidth = imageView.getDrawable().getIntrinsicWidth();
        float imageHeight = imageView.getDrawable().getIntrinsicHeight();
        float rotationAngle = imageView.getRotation();

        float screenWidth = imageView.getWidth();
        float screenHeight = imageView.getHeight();

        if (imageHeight > screenHeight || imageWidth > screenWidth) {
            float dimension = Math.max(imageHeight - screenHeight, imageWidth - screenWidth);
            float ratio = imageWidth / imageHeight;
            if (imageWidth - dimension == screenWidth) {
                imageWidth -= dimension;
                imageHeight = imageWidth / ratio;
            } else if (imageHeight - dimension == screenHeight) {
                imageHeight -= dimension;
                imageWidth = imageHeight * ratio;
            }
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

        float scale = Math.max(scaleX, scaleY);

        return scale != 1f ? scale : 2f;
    }
}
