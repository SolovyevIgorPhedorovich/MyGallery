package com.example.mygallery.adapters.album;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.example.mygallery.R;
import com.example.mygallery.adapters.viewholder.AlbumViewHolder;
import com.example.mygallery.interfaces.OnItemClickListener;
import com.example.mygallery.models.Album;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AlbumAdapter extends AlbumAdapterHelper<Album> {

    private final int imageWidth;

    public AlbumAdapter(List<Album> dataList, int imageWidth, OnItemClickListener listener) {
        super(dataList, listener);
        this.imageWidth = imageWidth;
    }

    // Создание нового View Holder'а
    @NonNull
    @NotNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_image_view, parent, false);
        ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
        layoutParams.width = imageWidth;
        layoutParams.height = (int) (imageWidth * 1.5);
        itemView.setLayoutParams(layoutParams);
        return new AlbumViewHolder(itemView);
    }

    // Настройка элементов View Holder'а
    @Override
    public void onBindViewHolder(@NonNull @NotNull AlbumViewHolder holder, int position) {
        Album album = dataList.get(position);

        setImage(album.artwork, holder.folderImageView);

        holder.fileCountTextView.setText(album.name);
        holder.folderNameTextView.setText(String.valueOf(album.count));

        // Установка обработчика нажатия на изображение
        holder.folderImageView.setOnClickListener(v -> listener.onItemClick(position));
    }
}