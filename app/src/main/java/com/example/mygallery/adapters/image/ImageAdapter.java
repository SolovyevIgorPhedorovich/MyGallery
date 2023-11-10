package com.example.mygallery.adapters.image;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.*;
import androidx.annotation.NonNull;
import com.example.mygallery.R;
import com.example.mygallery.adapters.viewholder.ImageViewHolder;
import com.example.mygallery.databinding.ItemGalleryBinding;
import com.example.mygallery.interfaces.OnItemClickListener;
import com.example.mygallery.interfaces.model.Model;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ImageAdapter<T> extends ImageAdapterHelper<T> {
    private static final int COLUM = 4;

    protected final Context context;
    private final Point imageSize;

    public ImageAdapter(Context context, List<T> dataList, OnItemClickListener listener, OnItemClickListener selectClickListener, OnItemClickListener longClickListener) {
        super(dataList, listener, selectClickListener, longClickListener);
        this.context = context;
        this.imageSize = calculateImageSize();
        mode = Mode.VIEWING;
    }

    @NonNull
    @NotNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ItemGalleryBinding binding = ItemGalleryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        ViewGroup.LayoutParams layoutParams = binding.getRoot().getLayoutParams();
        layoutParams.width = imageSize.x;
        layoutParams.height = imageSize.y;
        binding.getRoot().setLayoutParams(layoutParams);
        return new ImageViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        setMode(holder, position);
        setCheckedState(holder, position);
        setImage(((Model) dataList.get(position)).getPath(), holder.imageView, imageSize);
    }

    // Метод для вычисления размера изображения в сетке
    private Point calculateImageSize() {
        Display display;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        Point imageSize = new Point();
        int screenWidth;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            display = context.getDisplay();
            WindowMetrics windowMetrics = context.getSystemService(WindowManager.class).getCurrentWindowMetrics();
            Rect bounds = windowMetrics.getBounds();
            screenWidth = bounds.width();
        } else {
            display = ((Activity) context).getWindowManager().getDefaultDisplay();
            display.getMetrics(displayMetrics);
            screenWidth = displayMetrics.widthPixels;
        }
        int margin = context.getResources().getDimensionPixelSize(R.dimen.layout_margin_2dp);
        int widthImage = (screenWidth - ((1 + COLUM) * margin)) / COLUM;
        imageSize.set(widthImage, widthImage);
        // Вычисление ширины для отображения изображения в сетке
        return imageSize;
    }
}