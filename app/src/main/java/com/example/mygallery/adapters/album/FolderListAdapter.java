package com.example.mygallery.adapters.album;

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
    private int exceptionFolder;
    private final Point imageSize;

    public FolderListAdapter(List<Model> dataList, OnItemClickListener listener) {
        super(dataList, listener);
        this.imageSize = setImageSize();
    }

    public FolderListAdapter(List<Model> dataList, int exceptionFolder, OnItemClickListener listener) {
        super(dataList, listener);
        this.exceptionFolder = exceptionFolder;
        excludeFolder(exceptionFolder);
        this.imageSize = setImageSize();
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
        Album album = (Album) dataList.get(position);
        setImage(album.artwork, holder.folderImageView, imageSize);

        holder.folderNameTextView.setText(album.name);
        holder.fileCountTextView.setText(String.valueOf(album.count));

        holder.itemView.setOnClickListener(v -> {
            int currentPosition = position;
            if (position >= exceptionFolder)
                currentPosition = currentPosition - 1;

            listener.onItemClick(currentPosition);
        });
    }

    private Point setImageSize(){
        return new Point(R.dimen.size_image_in_list, R.dimen.size_image_in_list);
    }

}