package com.example.mygallery.adapters.album;

import static com.example.mygallery.viewimage.LoadImage.setImage;

import android.content.Context;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.example.mygallery.ImageSizeCalculator;
import com.example.mygallery.R;
import com.example.mygallery.adapters.viewholder.AlbumViewHolder;
import com.example.mygallery.interfaces.OnCheckedIsChoice;
import com.example.mygallery.interfaces.OnItemClickListener;
import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.models.Album;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AlbumAdapter extends AlbumAdapterHelper<Model> {
    private final Context context;
    private final int spanCount;
    private Point imageSize;

    public AlbumAdapter(Context context, List<Model> dataList, int spanCount, OnItemClickListener listener, OnCheckedIsChoice checked) {
        super(dataList, listener, checked);
        this.context = context;
        this.spanCount = spanCount;
    }

    private void calculateImageSize(View view) {
        ImageSizeCalculator imageSizeCalculator = new ImageSizeCalculator(context, view, spanCount);
        imageSizeCalculator.calculateRectangularShapeSize();
        this.imageSize = imageSizeCalculator.getResult();
    }

    private void setViewLayoutParams(View view) {
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
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_image_view, parent, false);
        calculateImageSize(itemView);
        setViewLayoutParams(itemView);
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