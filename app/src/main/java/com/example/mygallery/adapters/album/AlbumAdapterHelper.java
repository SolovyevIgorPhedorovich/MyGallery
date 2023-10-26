package com.example.mygallery.adapters.album;

import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mygallery.adapters.viewholder.AlbumViewHolder;
import com.example.mygallery.interfaces.OnItemClickListener;
import com.example.mygallery.viewimage.LoadImage;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class AlbumAdapterHelper<T> extends RecyclerView.Adapter<AlbumViewHolder> {

    protected final OnItemClickListener listener;
    protected List<T> dataList;

    public AlbumAdapterHelper(List<T> dataList, OnItemClickListener listener) {
        this.listener = listener;
        setDataList(dataList);
    }

    public void setDataList(List<T> dataList) {
        this.dataList = new ArrayList<>(dataList);
    }

    // Загрузка изображения в ImageView с использованием Glide
    protected void setImage(File imagePath, ImageView imageView) {
        LoadImage.setImage(imagePath, imageView);
    }

    @NonNull
    @NotNull
    @Override
    public abstract AlbumViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType);

    @Override
    public abstract void onBindViewHolder(@NonNull @NotNull AlbumViewHolder holder, int position);

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
