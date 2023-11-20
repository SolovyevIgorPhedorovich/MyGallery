package com.example.mygallery.adapters.album;

import android.content.Context;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.example.mygallery.CalculateImageSize;
import com.example.mygallery.adapters.viewholder.AlbumViewHolder;
import com.example.mygallery.databinding.AlbumImageViewBinding;
import com.example.mygallery.interfaces.OnItemClickListener;

import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.models.Album;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AlbumAdapter extends AlbumAdapterHelper<Model> {
    private final Context context;
    private final int spanCount;
    private Point imageSize;

    public AlbumAdapter(Context context, List<Model> dataList, int spanCount, OnItemClickListener listener) {
        super(dataList, listener);
        this.context = context;
        this.spanCount = spanCount;
    }

    private void calculatedImageSize(View view) {
        CalculateImageSize calculateImageSize = new CalculateImageSize(context, view, spanCount);
        calculateImageSize.rectangularShape();
        this.imageSize = calculateImageSize.getResult();
    }

    private void setLayoutParams(View view) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = imageSize.x;
        layoutParams.height = imageSize.y;
        view.setLayoutParams(layoutParams);
    }

    // Создание нового View Holder'а
    @NonNull
    @NotNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        AlbumImageViewBinding binding = AlbumImageViewBinding.inflate(LayoutInflater.from(context), parent, false);
        View itemView = binding.getRoot();
        calculatedImageSize(itemView);
        setLayoutParams(itemView);
        return new AlbumViewHolder(itemView);
    }

    // Настройка элементов View Holder'а
    @Override
    public void onBindViewHolder(@NonNull @NotNull AlbumViewHolder holder, int position) {
        Album album = (Album) dataList.get(position);

        setImage(album.artwork, holder.folderImageView, imageSize);

        holder.fileCountTextView.setText(album.name);
        holder.folderNameTextView.setText(String.valueOf(album.count));

        // Установка обработчика нажатия на изображение
        holder.folderImageView.setOnClickListener(v -> listener.onItemClick(position));
    }
}