package com.example.mygallery.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.mygallery.R;
import com.example.mygallery.managers.DataManager;

import java.util.ArrayList;
import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ImageViewHolder> {

    // Поля класса
    private final Context context;
    private final OnItemClickListener listener;
    private List<String> folderNamesList;
    private List<String> folderCoversList;
    private final int imageWidth;
    private List<Integer> folderCountsList;

    // Конструктор класса
    public AlbumAdapter(Context context, int imageWidth, OnItemClickListener listener) {
        this.context = context;
        this.imageWidth = imageWidth;
        this.listener = listener;
        updateData(); // Обновляем данные при создании адаптера
    }

    // Обновление данных из DataManager
    public void updateData() {
        DataManager dataManager = DataManager.getInstance(context);
        this.folderNamesList = new ArrayList<>(dataManager.getFolderNamesList());
        this.folderCoversList = new ArrayList<>(dataManager.getFolderCoversList());
        this.folderCountsList = new ArrayList<>(dataManager.getFileCountList());
    }

    // Создание нового View Holder'а
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_image_view, parent, false);
        ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
        layoutParams.width = imageWidth;
        layoutParams.height = (int) (imageWidth * 1.5);
        itemView.setLayoutParams(layoutParams);
        return new ImageViewHolder(itemView);
    }

    // Загрузка изображения в ImageView с использованием Glide
    private void loadImageView(ImageView imageView, int position) {
        Glide.with(imageView)
                .load(folderCoversList.get(position))
                .into(imageView);
    }

    // Настройка элементов View Holder'а
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        loadImageView(holder.imageView, position);

        holder.folderName.setText(folderNamesList.get(position));
        holder.folderCount.setText(String.valueOf(folderCountsList.get(position)));

        // Установка обработчика нажатия на изображение
        holder.imageView.setOnClickListener(v -> listener.onItemClick(folderNamesList.get(position), position));
    }

    // Возвращает количество элементов в адаптере
    @Override
    public int getItemCount() {
        return folderNamesList.size();
    }

    // Вложенный класс для хранения элементов View Holder'а
    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView folderName;
        TextView folderCount;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.albumImagePreview);
            folderCount = itemView.findViewById(R.id.countImages);
            folderName = itemView.findViewById(R.id.albumDirectoryName);
        }
    }
}