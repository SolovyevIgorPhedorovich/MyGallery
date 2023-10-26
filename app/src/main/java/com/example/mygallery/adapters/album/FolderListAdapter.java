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

public class FolderListAdapter extends AlbumAdapterHelper<Album> {
    private int exceptionFolder;

    public FolderListAdapter(List<Album> dataList, OnItemClickListener listener) {
        super(dataList, listener);
    }

    public FolderListAdapter(List<Album> dataList, int exceptionFolder, OnItemClickListener listener) {
        super(dataList, listener);
        this.exceptionFolder = exceptionFolder;
        excludeFolder(exceptionFolder);
    }

    private void excludeFolder(int position) {
        dataList.remove(position);
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
        Album album = dataList.get(position);
        setImage(album.artwork, holder.folderImageView);

        holder.folderNameTextView.setText(album.name);
        holder.fileCountTextView.setText(String.valueOf(album.count));

        holder.itemView.setOnClickListener(v -> {
            int currentPosition = position;
            if (position >= exceptionFolder)
                currentPosition = currentPosition - 1;

            listener.onItemClick(currentPosition);
        });
    }

}