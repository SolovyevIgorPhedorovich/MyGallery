package com.example.mygallery.adapters.image;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.example.mygallery.R;
import com.example.mygallery.adapters.viewholder.ImageViewHolder;
import com.example.mygallery.interfaces.OnItemClickListener;
import com.example.mygallery.interfaces.model.Model;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ImageAdapter<T> extends ImageAdapterHelper<T> {

    protected final Context context;
    private final int imageSize;

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
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gallery, parent, false);
        ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
        layoutParams.width = imageSize;
        layoutParams.height = imageSize;
        itemView.setLayoutParams(layoutParams);
        return new ImageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        setSwitchMode(holder, position);
        setCheckedState(holder, position);
        setImage(((Model) dataList.get(position)).getPath(), holder.imageView);
    }

    // Метод для вычисления размера изображения в сетке
    private int calculateImageSize() {
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;

        // Вычисление ширины для отображения изображения в сетке (предполагая, что у вас 4 столбца)
        return (screenWidth - 10) / 4;
    }
}