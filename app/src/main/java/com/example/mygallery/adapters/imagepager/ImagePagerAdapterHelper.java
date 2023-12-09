package com.example.mygallery.adapters.imagepager;

import android.graphics.RectF;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.collection.SparseArrayCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mygallery.R;
import com.example.mygallery.adapters.viewholder.ImagePagerViewHolder;
import com.example.mygallery.interfaces.OnAdapterInteraction;
import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.viewimage.LoadImage;
import com.github.chrisbanes.photoview.PhotoView;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class ImagePagerAdapterHelper extends RecyclerView.Adapter<ImagePagerViewHolder> implements OnAdapterInteraction<Model> {

    protected final SparseArrayCompat<ImagePagerViewHolder> imageViewHolders;
    protected List<Model> imageList;

    public ImagePagerAdapterHelper(List<Model> imageList) {
        this.imageViewHolders = new SparseArrayCompat<>();
        onSetDataList(imageList);
    }

    @Override
    public List<Model> onGetDataList() {
        return imageList;
    }

    @Override
    public void onSetDataList(List<Model> pathImages) {
        this.imageList = new ArrayList<>(pathImages);
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
        loadImage(imageList.get(position).getPath(), holder.imageView, photoView -> onGlideResourceReady(photoView, holder));
        setTouchListener(holder);
        imageViewHolders.put(position, holder);// Сохраняем ViewHolder для данной позиции
    }

    // Загружает изображение
    protected void loadImage(File imageUrl, PhotoView imageView, OnGlideListener listener) {
        LoadImage.setImage(imageUrl, imageView, listener);
    }

    // Устанавливает обработчик касаний для изображения
    protected void scaleImage(PhotoView photoView, MotionEvent e) {
        try {
            float scale = photoView.getScale();
            float x = e.getX();
            float y = e.getY();
            if (scale < photoView.getMediumScale()) {
                photoView.setScale(photoView.getMediumScale(), x, y, true);
            } else {
                photoView.setScale(photoView.getMinimumScale(), x, y, true);
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            // Can sometimes happen when getX() and getY() is called
        }
    }

    protected abstract void setTouchListener(ImagePagerViewHolder holder);
    @Override
    public int getItemCount() {
        return imageList.size();
    }

    protected void onGlideResourceReady(PhotoView photoView, ImagePagerViewHolder holder) {
        calculatedScaleVerticalOrientation(photoView, holder);
    }

    //Вычисляет вертикальную ориентацию масштабирования изображения
    private void calculatedScaleVerticalOrientation(PhotoView photoView, ImagePagerViewHolder holder) {
        photoView.post(() -> {
            RectF displayRect = photoView.getDisplayRect();
            float minScale = 1f;
            float midScale = Math.max(photoView.getWidth() / displayRect.width(),
                    photoView.getHeight() / displayRect.height());
            midScale = (midScale == minScale) ? 3f : midScale;
            float maxScale = Math.max(3 * midScale, 9f);
            photoView.setScaleLevels(minScale, midScale, maxScale);
            holder.setScaleVertical(minScale, midScale, maxScale);
        });
    }

    public interface OnGlideListener {
        void onGlideResourceReady(PhotoView photoView);
    }

}