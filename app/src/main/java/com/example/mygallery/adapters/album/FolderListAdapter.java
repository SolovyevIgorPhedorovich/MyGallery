package com.example.mygallery.adapters.album;

import android.content.Context;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.example.mygallery.R;
import com.example.mygallery.adapters.viewholder.AlbumViewHolder;
import com.example.mygallery.interfaces.OnItemClickListener;
import com.example.mygallery.interfaces.model.Model;
import com.example.mygallery.models.Album;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FolderListAdapter extends AlbumAdapterHelper<Model> {
    private int exceptionFolder = -1;
    private final Point imageSize;

    public FolderListAdapter(Context context, List<Model> dataList, OnItemClickListener listener) {
        super(dataList, listener);
        this.imageSize = calculateImageSize(context);
    }

    public FolderListAdapter(Context context, List<Model> dataList, int exceptionFolder, OnItemClickListener listener) {
        this(context, dataList, listener);
        setExceptionFolder(exceptionFolder);
    }

    private void setExceptionFolder(int position) {
        if (position >= 0 && position < dataList.size()) {
            dataList.remove(position);
            exceptionFolder = position;
        }
    }

    @NonNull
    @NotNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_object, parent, false);
        return new AlbumViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AlbumViewHolder holder, int position) {
        Album album = (Album) dataList.get(position);
        setImage(album.artwork, holder.folderImageView, imageSize);

        holder.folderNameTextView.setText(album.name);
        holder.fileCountTextView.setText(String.valueOf(album.count));

        holder.itemView.setOnClickListener(v -> {
            int currentPosition = position;
            if (exceptionFolder != -1 && position >= exceptionFolder) {
                currentPosition = currentPosition + 1;
            }
            listener.onItemClick(currentPosition);
        });
    }

    private Point calculateImageSize(Context context) {
        int imageSize = context.getResources().getDimensionPixelSize(R.dimen.size_image_in_list);
        return new Point(imageSize, imageSize);
    }
}