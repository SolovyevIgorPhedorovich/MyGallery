package com.example.mygallery.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mygallery.R;

import java.io.File;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private final Context context;
    private final int imageSize;
    private final OnItemClickListener listener;
    private final List<File> imagePathsList;

    // Конструктор
    public ImageAdapter(Context context, List<File> imagePathsList, OnItemClickListener listener) {
        this.context = context;
        this.imagePathsList = imagePathsList;
        this.listener = listener;
        this.imageSize = calculateImageSize();
    }

    // Метод для создания нового ViewHolder
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gallery, parent, false);
        ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
        layoutParams.width = imageSize;
        layoutParams.height = imageSize;
        itemView.setLayoutParams(layoutParams);
        return new ImageViewHolder(itemView);
    }

    // Метод для связывания данных с ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Glide.with(holder.imageView)
                .load(imagePathsList.get(position))
                .apply(RequestOptions.overrideOf(imageSize))
                .into(holder.imageView);

        holder.imageView.setOnClickListener(v -> listener.onItemClick(position));
    }

    // Метод для получения общего количества элементов в списке
    @Override
    public int getItemCount() {
        return imagePathsList.size();
    }

    // Метод для вычисления размера изображения в сетке
    private int calculateImageSize() {
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;

        // Вычисление ширины для отображения изображения в сетке (предполагая, что у вас 4 столбца)
        return screenWidth / 4;
    }

    // ViewHolder
    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}